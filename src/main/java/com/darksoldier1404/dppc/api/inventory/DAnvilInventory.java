package com.darksoldier1404.dppc.api.inventory;

import com.darksoldier1404.dppc.annotation.DPPCoreVersion;
import com.darksoldier1404.dppc.data.DPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A GUI helper for easily building anvil input prompts, much like {@link DInventory}.
 *
 * <p>To capture the typed text, a real anvil menu must be opened. On the Bukkit API a real anvil is
 * created through {@code MenuType.ANVIL} (added in 1.21, available on Spigot and Paper alike).
 * Because the compile target is the 1.20.1 API, it is invoked via reflection. A real anvil menu
 * cannot carry a custom {@link InventoryHolder}, so open prompts are tracked through a per-player
 * session registry. On servers older than 1.21, where {@code MenuType} is unavailable, it falls back
 * to a plain anvil inventory whose rename text cannot be read.</p>
 *
 * <p>The library assigns no fixed meaning (confirm, cancel, etc.) to the three anvil slots (0, 1, 2).
 * Consuming plugins place items freely with {@link #setItem(int, ItemStack)} and define click
 * behavior themselves through the custom events such as {@code DAnvilInventoryClickEvent}.</p>
 *
 * <pre>{@code
 * DAnvilInventory anvil = new DAnvilInventory("Enter a name", plugin)
 *         .text("default")
 *         .setItem(2, new ItemStack(Material.GREEN_WOOL));
 * anvil.open(player);
 *
 * @EventHandler
 * public void onClick(DAnvilInventoryClickEvent e) {
 *     e.setCancelled(true);
 *     if (e.getRawSlot() == 2) {
 *         String input = e.getRenameText();
 *         e.getDAnvilInventory().close();
 *     }
 * }
 * }</pre>
 */
@DPPCoreVersion(since = "5.4.0")
public class DAnvilInventory implements InventoryHolder {

    /** Number of anvil inventory slots (0: input, 1: secondary input, 2: result). */
    public static final int SIZE = 3;

    private static final Map<UUID, DAnvilInventory> SESSIONS = new ConcurrentHashMap<>();

    private final DPlugin plugin;
    private final String handlerName;
    private String title;
    private String text;

    private final ItemStack[] items = new ItemStack[SIZE];

    private Inventory inventory;
    private Player viewer;
    private String renameText = "";
    private BukkitTask keepAliveTask;
    private boolean closed = false;

    public DAnvilInventory(String title, @NotNull DPlugin plugin) {
        this.title = title;
        this.plugin = plugin;
        this.handlerName = plugin.getName();
    }

    public boolean isValidHandler(JavaPlugin plugin) {
        return plugin.getName().equals(handlerName);
    }

    public DAnvilInventory title(String title) {
        this.title = title;
        return this;
    }

    /**
     * Initial text of the input field (slot 0). When set, it is applied as the display name of the
     * slot 0 item; a default {@link Material#PAPER} is used if slot 0 has no item.
     */
    public DAnvilInventory text(String text) {
        this.text = text;
        return this;
    }

    /**
     * Places an item in an arbitrary slot (slot: 0..2).
     */
    public DAnvilInventory setItem(int slot, ItemStack item) {
        checkSlot(slot);
        items[slot] = item;
        if (inventory != null) {
            inventory.setItem(slot, item == null ? null : item.clone());
        }
        return this;
    }

    private void checkSlot(int slot) {
        if (slot < 0 || slot >= SIZE) {
            throw new IllegalArgumentException("Anvil slot must be 0..2, but was " + slot);
        }
    }

    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }

    public String getTitle() {
        return title;
    }

    public Player getViewer() {
        return viewer;
    }

    public DPlugin getPlugin() {
        return plugin;
    }

    public ItemStack getItem(int slot) {
        checkSlot(slot);
        return items[slot];
    }

    /**
     * The text currently entered in the anvil rename field. Reads it live from the open anvil menu
     * and falls back to the last value captured during a text change.
     */
    public @Nullable String getRenameText() {
        if (viewer != null) {
            Inventory top = viewer.getOpenInventory().getTopInventory();
            if (top instanceof AnvilInventory) {
                String live = ((AnvilInventory) top).getRenameText();
                if (live != null) {
                    return live;
                }
            }
        }
        return renameText;
    }

    /**
     * Updates the cached rename text. Invoked by {@code DAnvilInventoryListener} on text change.
     */
    public void setRenameText(String renameText) {
        this.renameText = renameText == null ? "" : renameText;
    }

    public boolean isActive() {
        return !closed;
    }

    public void open(Player player) {
        if (text != null) {
            ItemStack base = items[0] != null ? items[0].clone() : new ItemStack(Material.PAPER);
            ItemMeta meta = base.getItemMeta();
            if (meta != null) {
                meta.setDisplayName(text);
                base.setItemMeta(meta);
            }
            items[0] = base;
        }

        this.viewer = player;
        this.renameText = text != null ? text : "";
        this.closed = false;
        SESSIONS.put(player.getUniqueId(), this);

        InventoryView view = createAnvilView(player, title);
        if (view != null) {
            this.inventory = view.getTopInventory();
            applyItems(this.inventory);
            player.openInventory(view);
        } else {
            Inventory inv = createFallbackInventory();
            applyItems(inv);
            this.inventory = inv;
            player.openInventory(inv);
        }

        if (!closed) {
            startKeepAlive(player);
        }
    }

    private void applyItems(Inventory inv) {
        for (int i = 0; i < SIZE; i++) {
            if (items[i] != null) {
                inv.setItem(i, items[i].clone());
            }
        }
    }

    /**
     * Closes the prompt. Closing is deferred to the next tick so it is safe to call from within
     * click event handling.
     */
    public void close() {
        if (viewer == null) return;
        Bukkit.getScheduler().runTask(plugin, () -> viewer.closeInventory());
    }

    private Inventory createFallbackInventory() {
        try {
            return Bukkit.createInventory(this, InventoryType.ANVIL, title);
        } catch (Throwable t) {
            return Bukkit.createInventory(this, InventoryType.ANVIL);
        }
    }

    /**
     * Creates a real anvil menu view through {@code MenuType.ANVIL} (Bukkit API, 1.21+), invoked via
     * reflection because the compile target is the 1.20.1 API. Returns null on older servers.
     */
    private InventoryView createAnvilView(Player player, String title) {
        try {
            Class<?> menuTypeClass = Class.forName("org.bukkit.inventory.MenuType");
            Class<?> typedClass = Class.forName("org.bukkit.inventory.MenuType$Typed");
            Object anvil = menuTypeClass.getField("ANVIL").get(null);
            Method create = typedClass.getMethod("create", HumanEntity.class, String.class);
            Object result = create.invoke(anvil, player, title);
            return result instanceof InventoryView ? (InventoryView) result : null;
        } catch (Throwable t) {
            return null;
        }
    }

    private void startKeepAlive(Player player) {
        this.keepAliveTask = new BukkitRunnable() {
            @Override
            public void run() {
                if (closed) {
                    cancel();
                    return;
                }
                Inventory top = player.getOpenInventory().getTopInventory();
                if (!(top instanceof AnvilInventory) || getOpen(player) != DAnvilInventory.this) {
                    cancel();
                    return;
                }
                for (int i = 1; i < SIZE; i++) {
                    if (items[i] != null) {
                        top.setItem(i, items[i].clone());
                    }
                }
                player.updateInventory();
            }
        }.runTaskTimer(plugin, 1L, 1L);
    }

    private void stopKeepAlive() {
        if (keepAliveTask != null) {
            keepAliveTask.cancel();
            keepAliveTask = null;
        }
    }

    /**
     * Invoked by {@code DAnvilInventoryListener} when the prompt is closed, for cleanup.
     */
    public void handleClose() {
        if (closed) return;
        closed = true;
        stopKeepAlive();
        if (viewer != null) {
            SESSIONS.remove(viewer.getUniqueId(), this);
        }
    }

    public static DAnvilInventory getOpen(Player player) {
        return player == null ? null : SESSIONS.get(player.getUniqueId());
    }

    public static boolean isOpen(Player player) {
        return getOpen(player) != null;
    }
}

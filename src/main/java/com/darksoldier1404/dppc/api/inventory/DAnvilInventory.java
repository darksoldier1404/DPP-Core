package com.darksoldier1404.dppc.api.inventory;

import com.darksoldier1404.dppc.annotation.DPPCoreVersion;
import com.darksoldier1404.dppc.data.DPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A GUI helper for easily building anvil input prompts, much like {@link DInventory}.
 *
 * <p>Written against the Spigot Bukkit API. An anvil inventory created with
 * {@code Bukkit.createInventory(holder, InventoryType.ANVIL, title)} is backed by a real anvil menu
 * (ContainerAnvil) once opened, so the rename text field works. Like {@link DInventory}, it tracks
 * itself through a custom {@link InventoryHolder}.</p>
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

    private final DPlugin plugin;
    private final String handlerName;
    private String title;
    private String text;

    private final ItemStack[] items = new ItemStack[SIZE];

    private Inventory inventory;
    private Player viewer;
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
     * The text currently entered the anvil rename field, or null if the prompt is not open.
     */
    public @Nullable String getRenameText() {
        if (viewer == null) return null;
        Inventory top = viewer.getOpenInventory().getTopInventory();
        return top instanceof AnvilInventory ? ((AnvilInventory) top).getRenameText() : null;
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

        this.inventory = createAnvilInventory();
        for (int i = 0; i < SIZE; i++) {
            if (items[i] != null) {
                inventory.setItem(i, items[i].clone());
            }
        }

        this.viewer = player;
        this.closed = false;
        player.openInventory(inventory);

        startKeepAlive(player);
    }

    /**
     * Closes the prompt. Closing is deferred to the next tick so it is safe to call from within
     * click event handling.
     */
    public void close() {
        if (viewer == null) return;
        Bukkit.getScheduler().runTask(plugin, () -> viewer.closeInventory());
    }

    private Inventory createAnvilInventory() {
        try {
            return Bukkit.createInventory(this, InventoryType.ANVIL, title);
        } catch (Throwable t) {
            return Bukkit.createInventory(this, InventoryType.ANVIL);
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
                if (top.getHolder() != DAnvilInventory.this) {
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
    }

    public static DAnvilInventory getOpen(Player player) {
        if (player == null) return null;
        InventoryHolder holder = player.getOpenInventory().getTopInventory().getHolder();
        return holder instanceof DAnvilInventory ? (DAnvilInventory) holder : null;
    }

    public static boolean isOpen(Player player) {
        return getOpen(player) != null;
    }
}

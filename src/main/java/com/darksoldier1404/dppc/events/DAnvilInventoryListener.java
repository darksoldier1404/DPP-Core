package com.darksoldier1404.dppc.events;

import com.darksoldier1404.dppc.api.inventory.DAnvilInventory;
import com.darksoldier1404.dppc.events.danvilinventory.DAnvilInventoryClickEvent;
import com.darksoldier1404.dppc.events.danvilinventory.DAnvilInventoryCloseEvent;
import com.darksoldier1404.dppc.events.danvilinventory.DAnvilInventoryOpenEvent;
import com.darksoldier1404.dppc.events.danvilinventory.DAnvilInventoryTextChangeEvent;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.Inventory;

/**
 * Translates Bukkit anvil-related events into {@link DAnvilInventory} custom events and dispatches them.
 *
 * <p>Plays the same role that {@link InventoryEventListener} does for
 * {@link com.darksoldier1404.dppc.api.inventory.DInventory} custom events. Open prompts are resolved
 * through the per-player session registry ({@link DAnvilInventory#getOpen(Player)}) since a real
 * anvil menu cannot carry a custom holder. The library assigns no meaning to individual slots;
 * consuming plugins handle the custom events themselves. Registered in {@code DPPCore#onEnable()}.</p>
 */
public class DAnvilInventoryListener implements Listener {

    private static Player asPlayer(HumanEntity entity) {
        return entity instanceof Player ? (Player) entity : null;
    }

    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent e) {
        Player player = asPlayer(e.getPlayer());
        if (player == null) return;
        DAnvilInventory inv = DAnvilInventory.getOpen(player);
        if (inv == null || inv.getInventory() != e.getInventory()) return;
        DAnvilInventoryOpenEvent event = new DAnvilInventoryOpenEvent(e.getView(), inv);
        inv.getPlugin().getServer().getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            e.setCancelled(true);
            inv.handleClose();
        }
    }

    @EventHandler
    public void onPrepareAnvil(PrepareAnvilEvent e) {
        Player player = asPlayer(e.getView().getPlayer());
        if (player == null) return;
        DAnvilInventory inv = DAnvilInventory.getOpen(player);
        if (inv == null || inv.getInventory() != e.getInventory()) return;
        e.getInventory().setRepairCost(0);
        String text = e.getInventory().getRenameText();
        inv.setRenameText(text);
        DAnvilInventoryTextChangeEvent event = new DAnvilInventoryTextChangeEvent(e.getView(), inv, text);
        inv.getPlugin().getServer().getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            e.setResult(null);
            return;
        }
        if (inv.getItem(2) != null) {
            e.setResult(inv.getItem(2).clone());
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        Player player = asPlayer(e.getWhoClicked());
        if (player == null) return;
        DAnvilInventory inv = DAnvilInventory.getOpen(player);
        if (inv == null) return;
        Inventory top = e.getView().getTopInventory();
        if (!(top instanceof AnvilInventory) || inv.getInventory() != top) return;

        boolean isPlayerInventory = e.getClickedInventory() != null
                && e.getClickedInventory().getType() == InventoryType.PLAYER;
        String renameText = inv.getRenameText();

        DAnvilInventoryClickEvent event = new DAnvilInventoryClickEvent(
                e.getView(), inv, e.getSlotType(), e.getRawSlot(), e.getClick(), e.getAction(), renameText, isPlayerInventory);
        inv.getPlugin().getServer().getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e) {
        Player player = asPlayer(e.getPlayer());
        if (player == null) return;
        DAnvilInventory inv = DAnvilInventory.getOpen(player);
        if (inv == null || !inv.isActive() || inv.getInventory() != e.getInventory()) return;
        DAnvilInventoryCloseEvent event = new DAnvilInventoryCloseEvent(e.getView(), inv);
        inv.getPlugin().getServer().getPluginManager().callEvent(event);
        inv.handleClose();
    }
}

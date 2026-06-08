package com.darksoldier1404.dppc.events;

import com.darksoldier1404.dppc.api.inventory.DAnvilInventory;
import com.darksoldier1404.dppc.events.danvilinventory.DAnvilInventoryClickEvent;
import com.darksoldier1404.dppc.events.danvilinventory.DAnvilInventoryCloseEvent;
import com.darksoldier1404.dppc.events.danvilinventory.DAnvilInventoryOpenEvent;
import com.darksoldier1404.dppc.events.danvilinventory.DAnvilInventoryTextChangeEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

/**
 * Translates Bukkit anvil-related events into {@link DAnvilInventory} custom events and dispatches them.
 *
 * <p>Plays the same role that {@link InventoryEventListener} does for
 * {@link com.darksoldier1404.dppc.api.inventory.DInventory} custom events. The library assigns no
 * meaning to individual slots; consuming plugins handle the custom events themselves.
 * Registered in {@code DPPCore#onEnable()}.</p>
 */
public class DAnvilInventoryListener implements Listener {

    private static DAnvilInventory holderOf(Inventory inventory) {
        if (inventory == null) return null;
        InventoryHolder holder = inventory.getHolder();
        return holder instanceof DAnvilInventory ? (DAnvilInventory) holder : null;
    }

    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent e) {
        DAnvilInventory inv = holderOf(e.getInventory());
        if (inv == null) return;
        DAnvilInventoryOpenEvent event = new DAnvilInventoryOpenEvent(e.getView(), inv);
        Bukkit.getServer().getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onPrepareAnvil(PrepareAnvilEvent e) {
        DAnvilInventory inv = holderOf(e.getInventory());
        if (inv == null) return;
        e.getInventory().setRepairCost(0);
        String text = e.getInventory().getRenameText();
        DAnvilInventoryTextChangeEvent event = new DAnvilInventoryTextChangeEvent(e.getView(), inv, text);
        Bukkit.getServer().getPluginManager().callEvent(event);
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
        Inventory top = e.getView().getTopInventory();
        DAnvilInventory inv = holderOf(top);
        if (inv == null) return;

        boolean isPlayerInventory = e.getClickedInventory() != null
                && e.getClickedInventory().getType() == InventoryType.PLAYER;
        String renameText = top instanceof AnvilInventory ? ((AnvilInventory) top).getRenameText() : null;

        DAnvilInventoryClickEvent event = new DAnvilInventoryClickEvent(
                e.getView(), inv, e.getSlotType(), e.getRawSlot(), e.getClick(), e.getAction(), renameText, isPlayerInventory);
        Bukkit.getServer().getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e) {
        DAnvilInventory inv = holderOf(e.getView().getTopInventory());
        if (inv == null) return;
        if (!inv.isActive()) return;
        DAnvilInventoryCloseEvent event = new DAnvilInventoryCloseEvent(e.getView(), inv);
        Bukkit.getServer().getPluginManager().callEvent(event);
        inv.handleClose();
    }
}

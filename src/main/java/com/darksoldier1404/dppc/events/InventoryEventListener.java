package com.darksoldier1404.dppc.events;

import com.darksoldier1404.dppc.api.inventory.DInventory;
import com.darksoldier1404.dppc.api.logger.DLogManager;
import com.darksoldier1404.dppc.events.dinventory.*;
import com.darksoldier1404.dppc.utils.NBT;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;

public class InventoryEventListener implements Listener {

    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent e) {
        if (e.getInventory().getHolder() != null && e.getInventory().getHolder() instanceof DInventory) {
            DInventory inv = (DInventory) e.getInventory().getHolder();
            DInventoryOpenEvent event = new DInventoryOpenEvent(e.getView(), inv);
            inv.getPlugin().getServer().getPluginManager().callEvent(event);
            if (event.isCancelled()) {
                e.setCancelled(true);
            }
            inv.getPlugin().getLog().info("Inventory Opened: " + e.getView().getTitle() + " by " + e.getPlayer().getName(), DLogManager.printDInventoryLogs);
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e) {
        if (e.getInventory().getHolder() != null && e.getInventory().getHolder() instanceof DInventory) {
            DInventory inv = (DInventory) e.getInventory().getHolder();
            Bukkit.getServer().getPluginManager().callEvent(new DInventoryCloseEvent(e.getView(), inv));
            inv.getPlugin().getLog().info("Inventory Closed: " + e.getView().getTitle() + " by " + e.getPlayer().getName(), DLogManager.printDInventoryLogs);
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (e.getInventory().getHolder() != null && e.getInventory().getHolder() instanceof DInventory) {
            DInventory inv = (DInventory) e.getInventory().getHolder();
            if (handlePagination(e, inv)) {
                return;
            }
            DInventoryClickEvent event = new DInventoryClickEvent(e.getView(), inv, e.getSlotType(), e.getRawSlot(), e.getClick(), e.getAction(), e.getHotbarButton());
            Bukkit.getServer().getPluginManager().callEvent(event);
            if (event.isCancelled()) {
                e.setCancelled(true);
            }
            inv.getPlugin().getLog().info("Inventory Clicked: " + e.getView().getTitle() + " by " + e.getWhoClicked().getName() + " on slot " + e.getRawSlot(), DLogManager.printDInventoryLogs);
        }
    }

    private boolean handlePagination(InventoryClickEvent e, DInventory inv) {
        ItemStack item = e.getCurrentItem();
        if (item == null || item.getType().isAir()) {
            return false;
        }
        if (NBT.hasTagKey(item, "dppc_prevpage")) {
            DInventoryPreviousPageEvent pageEvent = new DInventoryPreviousPageEvent(e.getView(), inv, e.getSlotType(), e.getRawSlot(), e.getClick(), e.getAction(), e.getHotbarButton());
            Bukkit.getServer().getPluginManager().callEvent(pageEvent);
            e.setCancelled(true);
            if (!pageEvent.isCancelled()) {
                inv.applyChanges();
                inv.prevPage();
                return true;
            }
        }
        if (NBT.hasTagKey(item, "dppc_nextpage")) {
            DInventoryNextPageEvent pageEvent = new DInventoryNextPageEvent(e.getView(), inv, e.getSlotType(), e.getRawSlot(), e.getClick(), e.getAction(), e.getHotbarButton());
            Bukkit.getServer().getPluginManager().callEvent(pageEvent);
            e.setCancelled(true);
            if (!pageEvent.isCancelled()) {
                inv.applyChanges();
                inv.nextPage();
            }
            return true;
        }
        if (NBT.hasTagKey(item, "dppc_clickcancel")) {
            e.setCancelled(true);
            return true;
        }
        return false;
    }
}

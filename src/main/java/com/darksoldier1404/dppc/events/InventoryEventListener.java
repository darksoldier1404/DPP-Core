package com.darksoldier1404.dppc.events;

import com.darksoldier1404.dppc.api.inventory.DInventory;
import com.darksoldier1404.dppc.events.dinventory.DInventoryCloseEvent;
import com.darksoldier1404.dppc.events.dinventory.DInventoryOpenEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;

public class InventoryEventListener implements Listener {
    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent e) {
        if (e.getInventory().getHolder() != null && e.getInventory().getHolder() instanceof DInventory) {
            DInventory inv = (DInventory) e.getInventory().getHolder();
            inv.getPlugin().getServer().getPluginManager().callEvent(new DInventoryOpenEvent(e.getView(), inv));
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryOpenEvent e) {
        if (e.getInventory().getHolder() != null && e.getInventory().getHolder() instanceof DInventory) {
            DInventory inv = (DInventory) e.getInventory().getHolder();
            Bukkit.getServer().getPluginManager().callEvent(new DInventoryCloseEvent(e.getView(), inv));
        }
    }

}

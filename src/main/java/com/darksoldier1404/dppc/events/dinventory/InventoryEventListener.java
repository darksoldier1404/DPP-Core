package com.darksoldier1404.dppc.events.dinventory;

import com.darksoldier1404.dppc.api.inventory.DInventory;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;

public class InventoryEventListener implements Listener {
    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent e) {
        if (e.getInventory().getHolder() != null && e.getInventory().getHolder() instanceof DInventory) {
            Bukkit.getPluginManager().callEvent(new DInventoryOpenEvent(e.getView(), (DInventory) e.getInventory().getHolder()));
        }
    }

}

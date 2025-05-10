package com.darksoldier1404.dppc.api.inventory.events;

import com.darksoldier1404.dppc.api.inventory.DInventory;
import com.darksoldier1404.dppc.utils.NBT;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;

public class DInventoryListener implements Listener {
    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (e.getInventory() instanceof DInventory) {
            DInventory inv = (DInventory) e.getInventory();
            if(inv.isUsePage()) {
                if (e.getCurrentItem() != null) {
                    ItemStack item = e.getCurrentItem();
                    if (NBT.hasTagKey(item, "dinv_pt_next")) {
                        e.setCancelled(true);
                        inv.nextPage();
                    } else if (NBT.hasTagKey(item, "dinv_pt_prev")) {
                        e.setCancelled(true);
                        inv.prevPage();
                    }
                }
            }
        }
    }

    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent e) {
        if (!(e.getInventory() instanceof DInventory)) return;
        DInventory inv = (DInventory) e.getInventory();
        DInventoryOpenEvent event = new DInventoryOpenEvent(e.getView(), inv);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e) {
        if (!(e.getInventory() instanceof DInventory)) return;
        DInventory inv = (DInventory) e.getInventory();
        Bukkit.getPluginManager().callEvent(new DInventoryCloseEvent(e.getView(), inv));
    }
}

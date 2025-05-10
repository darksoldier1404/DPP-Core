package com.darksoldier1404.dppc.api.inventory.events;

import com.darksoldier1404.dppc.api.inventory.DInventory;
import org.bukkit.event.HandlerList;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.InventoryView;
import org.jetbrains.annotations.NotNull;

public class DInventoryCloseEvent extends InventoryCloseEvent {
    private static final HandlerList handlers = new HandlerList();
    private final DInventory inventory;

    public DInventoryCloseEvent(@NotNull InventoryView transaction, DInventory inventory) {
        super(transaction);
        this.inventory = inventory;
    }

    @Override
    public @NotNull DInventory getInventory() {
        return inventory;
    }

    public boolean isValid(String pluginName) {
        if(pluginName == null) return false;
        return inventory.getHandlerName().equals(pluginName);
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}

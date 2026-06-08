package com.darksoldier1404.dppc.events.danvilinventory;

import com.darksoldier1404.dppc.api.inventory.DAnvilInventory;
import com.darksoldier1404.dppc.events.danvilinventory.obj.DAnvilInventoryEvent;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.InventoryView;
import org.jetbrains.annotations.NotNull;

/**
 * Fired when a {@link DAnvilInventory} prompt is closed.
 */
public class DAnvilInventoryCloseEvent extends DAnvilInventoryEvent {
    private static final HandlerList HANDLERS_LIST = new HandlerList();

    public DAnvilInventoryCloseEvent(@NotNull InventoryView transaction, DAnvilInventory inventory) {
        super(transaction, inventory);
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS_LIST;
    }
}

package com.darksoldier1404.dppc.events.danvilinventory;

import com.darksoldier1404.dppc.api.inventory.DAnvilInventory;
import com.darksoldier1404.dppc.events.danvilinventory.obj.DAnvilInventoryEvent;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.InventoryView;
import org.jetbrains.annotations.NotNull;

/**
 * Fired when a {@link DAnvilInventory} prompt is opened. If cancelled, it will not open.
 */
public class DAnvilInventoryOpenEvent extends DAnvilInventoryEvent implements Cancellable {
    private static final HandlerList HANDLERS_LIST = new HandlerList();
    private boolean isCancelled;

    public DAnvilInventoryOpenEvent(@NotNull InventoryView transaction, DAnvilInventory inventory) {
        super(transaction, inventory);
    }

    @Override
    public boolean isCancelled() {
        return isCancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.isCancelled = cancelled;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS_LIST;
    }
}

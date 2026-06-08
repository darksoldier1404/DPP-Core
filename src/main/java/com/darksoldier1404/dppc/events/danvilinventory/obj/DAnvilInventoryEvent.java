package com.darksoldier1404.dppc.events.danvilinventory.obj;

import com.darksoldier1404.dppc.api.inventory.DAnvilInventory;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryEvent;
import org.bukkit.inventory.InventoryView;
import org.jetbrains.annotations.NotNull;

/**
 * Common base for all {@link DAnvilInventory} custom events.
 * Mirrors the design of {@link com.darksoldier1404.dppc.events.dinventory.obj.DInventoryEvent}.
 */
public abstract class DAnvilInventoryEvent extends InventoryEvent {
    private final DAnvilInventory inventory;

    public DAnvilInventoryEvent(@NotNull InventoryView transaction, DAnvilInventory inventory) {
        super(transaction);
        this.inventory = inventory;
    }

    public @NotNull DAnvilInventory getDAnvilInventory() {
        return inventory;
    }

    public HumanEntity getPlayer() {
        return transaction.getPlayer();
    }
}

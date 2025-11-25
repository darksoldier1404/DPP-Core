package com.darksoldier1404.dppc.events.dinventory;

import com.darksoldier1404.dppc.annotation.DPPCoreVersion;
import com.darksoldier1404.dppc.api.inventory.DInventory;
import org.bukkit.event.HandlerList;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.InventoryView;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DInventoryClickEvent extends InventoryClickEvent {
    private static final HandlerList HANDLERS_LIST = new HandlerList();
    private final DInventory inventory;
    private final @Nullable DInventory.PageItemSet pageItemSet;
    private final boolean isPlayerInventory;
    private final boolean isOutSide;

    public DInventoryClickEvent(@NotNull InventoryView transaction, DInventory inventory, @NotNull InventoryType.SlotType type, int slot, @NotNull ClickType click, @NotNull InventoryAction action, boolean isPlayerInventory) {
        super(transaction, type, slot, click, action);
        this.inventory = inventory;
        this.isPlayerInventory = isPlayerInventory;
        this.isOutSide = type == InventoryType.SlotType.OUTSIDE;
        this.pageItemSet = inventory.isUsePage() && !isPlayerInventory && !isOutSide ? new DInventory.PageItemSet(inventory.getCurrentPage(), slot, inventory.getItem(slot)) : null;
    }

    public DInventoryClickEvent(@NotNull InventoryView transaction, DInventory inventory, @NotNull InventoryType.SlotType type, int slot, @NotNull ClickType click, @NotNull InventoryAction action, boolean isPlayerInventory, int key) {
        super(transaction, type, slot, click, action, key);
        this.inventory = inventory;
        this.isPlayerInventory = isPlayerInventory;
        this.isOutSide = type == InventoryType.SlotType.OUTSIDE;
        this.pageItemSet = inventory.isUsePage() && !isPlayerInventory && !isOutSide ? new DInventory.PageItemSet(inventory.getCurrentPage(), slot, inventory.getItem(slot)) : null;
    }

    public @NotNull DInventory getDInventory() {
        return inventory;
    }

    @DPPCoreVersion(since = "3.5.2")
    public @Nullable DInventory.PageItemSet getPageItemSet() {
        return pageItemSet;
    }

    @DPPCoreVersion(since = "3.5.2")
    public boolean isPlayerInventory() {
        return isPlayerInventory;
    }

    @DPPCoreVersion(since = "3.5.2")
    public boolean isOutSide() {
        return isOutSide;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS_LIST;
    }
}

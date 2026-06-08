package com.darksoldier1404.dppc.events.danvilinventory;

import com.darksoldier1404.dppc.api.inventory.DAnvilInventory;
import org.bukkit.event.HandlerList;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.InventoryView;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Fired when a slot of a {@link DAnvilInventory} is clicked.
 *
 * <p>Like {@link com.darksoldier1404.dppc.events.dinventory.DInventoryClickEvent}, it extends
 * {@link InventoryClickEvent}, so the clicked slot, button, and action can be read directly. The
 * library gives no slot a fixed meaning (confirm, cancel, etc.); each slot's behavior is defined by
 * the consuming plugin that handles this event.</p>
 *
 * <p>To prevent item movement/theft, the handler must cancel the event via
 * {@link #setCancelled(boolean)} (otherwise the vanilla anvil behavior applies). The text currently
 * entered is obtained through {@link #getRenameText()}.</p>
 */
public class DAnvilInventoryClickEvent extends InventoryClickEvent {
    private static final HandlerList HANDLERS_LIST = new HandlerList();
    private final DAnvilInventory inventory;
    private final @Nullable String renameText;
    private final boolean isPlayerInventory;

    public DAnvilInventoryClickEvent(@NotNull InventoryView transaction, DAnvilInventory inventory, @NotNull InventoryType.SlotType type, int slot, @NotNull ClickType click, @NotNull InventoryAction action, @Nullable String renameText, boolean isPlayerInventory) {
        super(transaction, type, slot, click, action);
        this.inventory = inventory;
        this.renameText = renameText;
        this.isPlayerInventory = isPlayerInventory;
    }

    public @NotNull DAnvilInventory getDAnvilInventory() {
        return inventory;
    }

    /**
     * The text currently entered in the anvil rename field; may be null when the top inventory is
     * not an anvil.
     */
    public @Nullable String getRenameText() {
        return renameText;
    }

    /**
     * Whether the clicked inventory is the player's inventory (false means the anvil's top inventory).
     */
    public boolean isPlayerInventory() {
        return isPlayerInventory;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS_LIST;
    }
}

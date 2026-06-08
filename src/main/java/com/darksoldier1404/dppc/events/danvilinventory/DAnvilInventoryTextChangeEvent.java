package com.darksoldier1404.dppc.events.danvilinventory;

import com.darksoldier1404.dppc.api.inventory.DAnvilInventory;
import com.darksoldier1404.dppc.events.danvilinventory.obj.DAnvilInventoryEvent;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.InventoryView;
import org.jetbrains.annotations.NotNull;

/**
 * Fired when the text in the anvil rename field changes (i.e. the vanilla {@code PrepareAnvilEvent}).
 *
 * <p>{@link #getText()} returns the current input text. If cancelled, the result slot is left empty
 * so the current input cannot be acted upon (useful for live input validation).</p>
 */
public class DAnvilInventoryTextChangeEvent extends DAnvilInventoryEvent implements Cancellable {
    private static final HandlerList HANDLERS_LIST = new HandlerList();
    private boolean isCancelled;
    private final String text;

    public DAnvilInventoryTextChangeEvent(@NotNull InventoryView transaction, DAnvilInventory inventory, String text) {
        super(transaction, inventory);
        this.text = text;
    }

    public String getText() {
        return text;
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

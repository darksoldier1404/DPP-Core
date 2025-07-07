package com.darksoldier1404.dppc.builder.action.actions;

import com.darksoldier1404.dppc.builder.action.obj.Action;
import com.darksoldier1404.dppc.builder.action.obj.ActionType;
import com.darksoldier1404.dppc.utils.ColorUtils;
import org.bukkit.entity.Player;

public class CloseInventoryAction implements Action {

    public CloseInventoryAction() {
    }

    @Override
    public void execute(Player player) {
        player.closeInventory();
    }

    @Override
    public ActionType getActionTypeName() {
        return ActionType.CLOSE_INVENTORY_ACTION;
    }

    @Override
    public String serialize() {
        return "close_inventory";
    }

    public static CloseInventoryAction parse(String line) {
        String[] parts = line.split("\\s+", 2);
        if (parts.length < 2 || !parts[0].equalsIgnoreCase("close_inventory")) {
            return null;
        }
        return new CloseInventoryAction();
    }
}
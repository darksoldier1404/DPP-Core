package com.darksoldier1404.dppc.builder.action.actions;

import com.darksoldier1404.dppc.builder.action.obj.Action;
import com.darksoldier1404.dppc.builder.action.obj.ActionContext;
import com.darksoldier1404.dppc.builder.action.obj.ActionType;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class TakeItemAction implements Action {
    private final String material;
    private final int amount;

    public TakeItemAction(String material, int amount) {
        this.material = material.toUpperCase();
        this.amount = amount;
    }

    @Override
    public void execute(ActionContext context) {
        try {
            Material mat = Material.valueOf(material);
            context.getPlayer().getInventory().removeItem(new ItemStack(mat, amount));
        } catch (IllegalArgumentException ignored) {
        }
    }

    @Override
    public ActionType getActionType() {
        return ActionType.TAKE_ITEM;
    }

    @Override
    public String serialize() {
        return "take_item " + material + " " + amount;
    }

    public static TakeItemAction parse(String line) {
        String[] parts = line.split("\\s+");
        if (parts.length != 3 || !parts[0].equalsIgnoreCase("take_item")) return null;
        try {
            return new TakeItemAction(parts[1], Integer.parseInt(parts[2]));
        } catch (NumberFormatException e) {
            return null;
        }
    }
}

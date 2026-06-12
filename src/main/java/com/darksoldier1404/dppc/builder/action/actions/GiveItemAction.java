package com.darksoldier1404.dppc.builder.action.actions;

import com.darksoldier1404.dppc.builder.action.obj.Action;
import com.darksoldier1404.dppc.builder.action.obj.ActionContext;
import com.darksoldier1404.dppc.builder.action.obj.ActionType;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class GiveItemAction implements Action {
    private final String material;
    private final int amount;

    public GiveItemAction(String material, int amount) {
        this.material = material.toUpperCase();
        this.amount = amount;
    }

    @Override
    public void execute(ActionContext context) {
        try {
            Material mat = Material.valueOf(material);
            context.getPlayer().getInventory().addItem(new ItemStack(mat, amount));
        } catch (IllegalArgumentException ignored) {
        }
    }

    @Override
    public ActionType getActionType() {
        return ActionType.GIVE_ITEM;
    }

    @Override
    public String serialize() {
        return "give_item " + material + " " + amount;
    }

    public static GiveItemAction parse(String line) {
        String[] parts = line.split("\\s+");
        if (parts.length != 3 || !parts[0].equalsIgnoreCase("give_item")) return null;
        try {
            return new GiveItemAction(parts[1], Integer.parseInt(parts[2]));
        } catch (NumberFormatException e) {
            return null;
        }
    }
}

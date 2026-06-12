package com.darksoldier1404.dppc.builder.action.actions;

import com.darksoldier1404.dppc.builder.action.obj.Action;
import com.darksoldier1404.dppc.builder.action.obj.ActionContext;
import com.darksoldier1404.dppc.builder.action.obj.ActionType;

public class SetHungerAction implements Action {
    private final int foodLevel;

    public SetHungerAction(int foodLevel) {
        this.foodLevel = foodLevel;
    }

    @Override
    public void execute(ActionContext context) {
        context.getPlayer().setFoodLevel(Math.max(0, Math.min(foodLevel, 20)));
    }

    @Override
    public ActionType getActionType() {
        return ActionType.SET_HUNGER;
    }

    @Override
    public String serialize() {
        return "set_hunger " + foodLevel;
    }

    public static SetHungerAction parse(String line) {
        String[] parts = line.split("\\s+");
        if (parts.length != 2 || !parts[0].equalsIgnoreCase("set_hunger")) return null;
        try {
            return new SetHungerAction(Integer.parseInt(parts[1]));
        } catch (NumberFormatException e) {
            return null;
        }
    }
}

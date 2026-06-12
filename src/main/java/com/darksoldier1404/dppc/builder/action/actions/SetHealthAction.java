package com.darksoldier1404.dppc.builder.action.actions;

import com.darksoldier1404.dppc.builder.action.obj.Action;
import com.darksoldier1404.dppc.builder.action.obj.ActionContext;
import com.darksoldier1404.dppc.builder.action.obj.ActionType;

public class SetHealthAction implements Action {
    private final double health;

    public SetHealthAction(double health) {
        this.health = health;
    }

    @Override
    public void execute(ActionContext context) {
        double max = context.getPlayer().getMaxHealth();
        context.getPlayer().setHealth(Math.max(0, Math.min(health, max)));
    }

    @Override
    public ActionType getActionType() {
        return ActionType.SET_HEALTH;
    }

    @Override
    public String serialize() {
        return "set_health " + health;
    }

    public static SetHealthAction parse(String line) {
        String[] parts = line.split("\\s+");
        if (parts.length != 2 || !parts[0].equalsIgnoreCase("set_health")) return null;
        try {
            return new SetHealthAction(Double.parseDouble(parts[1]));
        } catch (NumberFormatException e) {
            return null;
        }
    }
}

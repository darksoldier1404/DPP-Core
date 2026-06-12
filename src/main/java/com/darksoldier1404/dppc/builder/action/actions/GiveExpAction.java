package com.darksoldier1404.dppc.builder.action.actions;

import com.darksoldier1404.dppc.builder.action.obj.Action;
import com.darksoldier1404.dppc.builder.action.obj.ActionContext;
import com.darksoldier1404.dppc.builder.action.obj.ActionType;

public class GiveExpAction implements Action {
    private final int amount;

    public GiveExpAction(int amount) {
        this.amount = amount;
    }

    @Override
    public void execute(ActionContext context) {
        context.getPlayer().giveExp(amount);
    }

    @Override
    public ActionType getActionType() {
        return ActionType.GIVE_EXP;
    }

    @Override
    public String serialize() {
        return "give_exp " + amount;
    }

    public static GiveExpAction parse(String line) {
        String[] parts = line.split("\\s+");
        if (parts.length != 2 || !parts[0].equalsIgnoreCase("give_exp")) return null;
        try {
            return new GiveExpAction(Integer.parseInt(parts[1]));
        } catch (NumberFormatException e) {
            return null;
        }
    }
}

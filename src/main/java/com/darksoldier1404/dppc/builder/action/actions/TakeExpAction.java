package com.darksoldier1404.dppc.builder.action.actions;

import com.darksoldier1404.dppc.builder.action.obj.Action;
import com.darksoldier1404.dppc.builder.action.obj.ActionContext;
import com.darksoldier1404.dppc.builder.action.obj.ActionType;

public class TakeExpAction implements Action {
    private final int amount;

    public TakeExpAction(int amount) {
        this.amount = amount;
    }

    @Override
    public void execute(ActionContext context) {
        context.getPlayer().giveExp(-amount);
    }

    @Override
    public ActionType getActionType() {
        return ActionType.TAKE_EXP;
    }

    @Override
    public String serialize() {
        return "take_exp " + amount;
    }

    public static TakeExpAction parse(String line) {
        String[] parts = line.split("\\s+");
        if (parts.length != 2 || !parts[0].equalsIgnoreCase("take_exp")) return null;
        try {
            return new TakeExpAction(Integer.parseInt(parts[1]));
        } catch (NumberFormatException e) {
            return null;
        }
    }
}

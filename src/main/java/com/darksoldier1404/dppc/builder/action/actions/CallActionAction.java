package com.darksoldier1404.dppc.builder.action.actions;

import com.darksoldier1404.dppc.DPPCore;
import com.darksoldier1404.dppc.builder.action.ActionBuilder;
import com.darksoldier1404.dppc.builder.action.obj.Action;
import com.darksoldier1404.dppc.builder.action.obj.ActionContext;
import com.darksoldier1404.dppc.builder.action.obj.ActionType;

public class CallActionAction implements Action {
    private final String actionName;

    public CallActionAction(String actionName) {
        this.actionName = actionName;
    }

    @Override
    public void execute(ActionContext context) {
        ActionBuilder target = DPPCore.actions.get(actionName);
        if (target != null && context.getPlayer().isOnline()) {
            target.execute(context.getPlayer());
        }
    }

    @Override
    public ActionType getActionType() {
        return ActionType.CALL_ACTION;
    }

    @Override
    public String serialize() {
        return "call_action " + actionName;
    }

    public static CallActionAction parse(String line) {
        String[] parts = line.split("\\s+", 2);
        if (parts.length != 2 || !parts[0].equalsIgnoreCase("call_action")) return null;
        return new CallActionAction(parts[1]);
    }
}

package com.darksoldier1404.dppc.builder.action.actions;

import com.darksoldier1404.dppc.builder.action.obj.Action;
import com.darksoldier1404.dppc.builder.action.obj.ActionContext;
import com.darksoldier1404.dppc.builder.action.obj.ActionType;

public class CancelAction implements Action {

    @Override
    public void execute(ActionContext context) {
        context.cancel();
    }

    @Override
    public ActionType getActionType() {
        return ActionType.CANCEL;
    }

    @Override
    public String serialize() {
        return "cancel";
    }

    public static CancelAction parse(String line) {
        if (!line.trim().equalsIgnoreCase("cancel")) return null;
        return new CancelAction();
    }
}

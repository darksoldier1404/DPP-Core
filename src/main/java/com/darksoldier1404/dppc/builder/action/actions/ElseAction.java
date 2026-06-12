package com.darksoldier1404.dppc.builder.action.actions;

import com.darksoldier1404.dppc.builder.action.obj.Action;
import com.darksoldier1404.dppc.builder.action.obj.ActionContext;
import com.darksoldier1404.dppc.builder.action.obj.ActionType;

public class ElseAction implements Action {

    @Override
    public void execute(ActionContext context) {
        context.flipTopCondition();
    }

    @Override
    public boolean isFlowControl() {
        return true;
    }

    @Override
    public ActionType getActionType() {
        return ActionType.ELSE;
    }

    @Override
    public String serialize() {
        return "else";
    }

    public static ElseAction parse(String line) {
        if (!line.trim().equalsIgnoreCase("else")) return null;
        return new ElseAction();
    }
}

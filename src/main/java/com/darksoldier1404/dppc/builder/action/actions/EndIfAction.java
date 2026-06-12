package com.darksoldier1404.dppc.builder.action.actions;

import com.darksoldier1404.dppc.builder.action.obj.Action;
import com.darksoldier1404.dppc.builder.action.obj.ActionContext;
import com.darksoldier1404.dppc.builder.action.obj.ActionType;

public class EndIfAction implements Action {

    @Override
    public void execute(ActionContext context) {
        context.popCondition();
    }

    @Override
    public boolean isFlowControl() {
        return true;
    }

    @Override
    public ActionType getActionType() {
        return ActionType.END_IF;
    }

    @Override
    public String serialize() {
        return "end_if";
    }

    public static EndIfAction parse(String line) {
        if (!line.trim().equalsIgnoreCase("end_if")) return null;
        return new EndIfAction();
    }
}

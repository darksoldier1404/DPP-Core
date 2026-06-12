package com.darksoldier1404.dppc.builder.action.actions;

import com.darksoldier1404.dppc.builder.action.obj.Action;
import com.darksoldier1404.dppc.builder.action.obj.ActionContext;
import com.darksoldier1404.dppc.builder.action.obj.ActionType;

public class SetVariableAction implements Action {
    private final String name;
    private final String value;

    public SetVariableAction(String name, String value) {
        this.name = name;
        this.value = value;
    }

    @Override
    public void execute(ActionContext context) {
        context.setVariable(name, context.applyVariables(value));
    }

    @Override
    public ActionType getActionType() {
        return ActionType.SET_VARIABLE;
    }

    @Override
    public String serialize() {
        return "set_variable " + name + " " + value;
    }

    public static SetVariableAction parse(String line) {
        String[] parts = line.split("\\s+", 3);
        if (parts.length != 3 || !parts[0].equalsIgnoreCase("set_variable")) return null;
        return new SetVariableAction(parts[1], parts[2]);
    }
}

package com.darksoldier1404.dppc.builder.action.actions;

import com.darksoldier1404.dppc.builder.action.obj.Action;
import com.darksoldier1404.dppc.builder.action.obj.ActionContext;
import com.darksoldier1404.dppc.builder.action.obj.ActionType;

public class IfNotPermissionAction implements Action {
    private final String permission;

    public IfNotPermissionAction(String permission) {
        this.permission = permission;
    }

    @Override
    public void execute(ActionContext context) {
        boolean result = context.shouldExecute() && !context.getPlayer().hasPermission(permission);
        context.pushCondition(result);
    }

    @Override
    public boolean isFlowControl() {
        return true;
    }

    @Override
    public ActionType getActionType() {
        return ActionType.IF_NOT_PERMISSION;
    }

    @Override
    public String serialize() {
        return "if_not_permission " + permission;
    }

    public static IfNotPermissionAction parse(String line) {
        String[] parts = line.split("\\s+", 2);
        if (parts.length != 2 || !parts[0].equalsIgnoreCase("if_not_permission")) return null;
        return new IfNotPermissionAction(parts[1]);
    }
}

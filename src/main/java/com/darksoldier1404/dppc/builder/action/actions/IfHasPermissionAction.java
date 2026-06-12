package com.darksoldier1404.dppc.builder.action.actions;

import com.darksoldier1404.dppc.builder.action.obj.Action;
import com.darksoldier1404.dppc.builder.action.obj.ActionContext;
import com.darksoldier1404.dppc.builder.action.obj.ActionType;

public class IfHasPermissionAction implements Action {
    private final String permission;

    public IfHasPermissionAction(String permission) {
        this.permission = permission;
    }

    @Override
    public void execute(ActionContext context) {
        boolean result = context.shouldExecute() && context.getPlayer().hasPermission(permission);
        context.pushCondition(result);
    }

    @Override
    public boolean isFlowControl() {
        return true;
    }

    @Override
    public ActionType getActionType() {
        return ActionType.IF_HAS_PERMISSION;
    }

    @Override
    public String serialize() {
        return "if_has_permission " + permission;
    }

    public static IfHasPermissionAction parse(String line) {
        String[] parts = line.split("\\s+", 2);
        if (parts.length != 2 || !parts[0].equalsIgnoreCase("if_has_permission")) return null;
        return new IfHasPermissionAction(parts[1]);
    }
}

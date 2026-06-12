package com.darksoldier1404.dppc.builder.action.actions;

import com.darksoldier1404.dppc.builder.action.obj.Action;
import com.darksoldier1404.dppc.builder.action.obj.ActionContext;
import com.darksoldier1404.dppc.builder.action.obj.ActionType;
import com.darksoldier1404.dppc.utils.ColorUtils;

public class KickAction implements Action {
    private final String reason;

    public KickAction(String reason) {
        this.reason = reason;
    }

    @Override
    public void execute(ActionContext context) {
        if (context.getPlayer().isOnline()) {
            context.getPlayer().kickPlayer(ColorUtils.applyColor(context.applyVariables(reason)));
        }
    }

    @Override
    public ActionType getActionType() {
        return ActionType.KICK;
    }

    @Override
    public String serialize() {
        return "kick " + reason;
    }

    public static KickAction parse(String line) {
        String[] parts = line.split("\\s+", 2);
        if (parts.length < 2 || !parts[0].equalsIgnoreCase("kick")) return null;
        return new KickAction(parts[1]);
    }
}

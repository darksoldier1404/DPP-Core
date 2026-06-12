package com.darksoldier1404.dppc.builder.action.actions;

import com.darksoldier1404.dppc.builder.action.obj.Action;
import com.darksoldier1404.dppc.builder.action.obj.ActionContext;
import com.darksoldier1404.dppc.builder.action.obj.ActionType;
import com.darksoldier1404.dppc.utils.ColorUtils;
import org.bukkit.Bukkit;

public class BroadcastAction implements Action {
    private final String message;

    public BroadcastAction(String message) {
        this.message = message;
    }

    @Override
    public void execute(ActionContext context) {
        Bukkit.broadcastMessage(ColorUtils.applyColor(context.applyVariables(message)));
    }

    @Override
    public ActionType getActionType() {
        return ActionType.BROADCAST;
    }

    @Override
    public String serialize() {
        return "broadcast " + message;
    }

    public static BroadcastAction parse(String line) {
        String[] parts = line.split("\\s+", 2);
        if (parts.length < 2 || !parts[0].equalsIgnoreCase("broadcast")) return null;
        return new BroadcastAction(parts[1]);
    }
}

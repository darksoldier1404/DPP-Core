package com.darksoldier1404.dppc.builder.action.actions;

import com.darksoldier1404.dppc.builder.action.obj.Action;
import com.darksoldier1404.dppc.builder.action.obj.ActionContext;
import com.darksoldier1404.dppc.builder.action.obj.ActionType;
import com.darksoldier1404.dppc.utils.ColorUtils;
import org.bukkit.entity.Player;

public class BroadcastWorldAction implements Action {
    private final String message;

    public BroadcastWorldAction(String message) {
        this.message = message;
    }

    @Override
    public void execute(ActionContext context) {
        String msg = ColorUtils.applyColor(context.applyVariables(message));
        for (Player p : context.getPlayer().getWorld().getPlayers()) {
            p.sendMessage(msg);
        }
    }

    @Override
    public ActionType getActionType() {
        return ActionType.BROADCAST_WORLD;
    }

    @Override
    public String serialize() {
        return "broadcast_world " + message;
    }

    public static BroadcastWorldAction parse(String line) {
        String[] parts = line.split("\\s+", 2);
        if (parts.length < 2 || !parts[0].equalsIgnoreCase("broadcast_world")) return null;
        return new BroadcastWorldAction(parts[1]);
    }
}

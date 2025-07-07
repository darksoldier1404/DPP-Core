package com.darksoldier1404.dppc.builder.action.actions;

import com.darksoldier1404.dppc.builder.action.obj.Action;
import com.darksoldier1404.dppc.builder.action.obj.ActionType;
import com.darksoldier1404.dppc.utils.ColorUtils;
import org.bukkit.entity.Player;

public class SendMessageAction implements Action {
    private final String message;

    public SendMessageAction(String message) {
        this.message = ColorUtils.applyColor(message);
    }

    @Override
    public void execute(Player player) {
        if (player != null && player.isOnline()) {
            player.sendMessage(message.replace("{player}", player.getName()));
        }
    }

    @Override
    public ActionType getActionTypeName() {
        return ActionType.SEND_MESSAGE_ACTION;
    }

    @Override
    public String serialize() {
        return "send_message " + message;
    }

    public static SendMessageAction parse(String line) {
        String[] parts = line.split("\\s+", 2);
        if (parts.length < 2 || !parts[0].equalsIgnoreCase("send_message")) {
            return null;
        }
        return new SendMessageAction(parts[1]);
    }
}
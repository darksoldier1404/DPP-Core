package com.darksoldier1404.dppc.builder.action.actions;

import com.darksoldier1404.dppc.builder.action.obj.Action;
import com.darksoldier1404.dppc.builder.action.obj.ActionContext;
import com.darksoldier1404.dppc.builder.action.obj.ActionType;
import com.darksoldier1404.dppc.utils.ColorUtils;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

public class SendActionBarAction implements Action {
    private final String message;

    public SendActionBarAction(String message) {
        this.message = message;
    }

    @Override
    public void execute(ActionContext context) {
        if (context.getPlayer().isOnline()) {
            context.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR,
                    new TextComponent(ColorUtils.applyColor(context.applyVariables(message))));
        }
    }

    @Override
    public ActionType getActionType() {
        return ActionType.SEND_ACTIONBAR;
    }

    @Override
    public String serialize() {
        return "send_actionbar " + message;
    }

    public static SendActionBarAction parse(String line) {
        String[] parts = line.split("\\s+", 2);
        if (parts.length < 2 || !parts[0].equalsIgnoreCase("send_actionbar")) return null;
        return new SendActionBarAction(parts[1]);
    }
}

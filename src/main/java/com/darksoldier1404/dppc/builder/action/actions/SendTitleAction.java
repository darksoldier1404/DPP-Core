package com.darksoldier1404.dppc.builder.action.actions;

import com.darksoldier1404.dppc.builder.action.obj.Action;
import com.darksoldier1404.dppc.builder.action.obj.ActionContext;
import com.darksoldier1404.dppc.builder.action.obj.ActionType;
import com.darksoldier1404.dppc.utils.ColorUtils;

public class SendTitleAction implements Action {
    private final String title;
    private final String subtitle;
    private final int fadeIn;
    private final int stay;
    private final int fadeOut;

    public SendTitleAction(String title, String subtitle, int fadeIn, int stay, int fadeOut) {
        this.title = title;
        this.subtitle = subtitle;
        this.fadeIn = fadeIn;
        this.stay = stay;
        this.fadeOut = fadeOut;
    }

    @Override
    public void execute(ActionContext context) {
        if (context.getPlayer().isOnline()) {
            context.getPlayer().sendTitle(
                    ColorUtils.applyColor(context.applyVariables(title)),
                    ColorUtils.applyColor(context.applyVariables(subtitle)),
                    fadeIn, stay, fadeOut);
        }
    }

    @Override
    public ActionType getActionType() {
        return ActionType.SEND_TITLE;
    }

    @Override
    public String serialize() {
        return String.format("send_title %s|%s|%d|%d|%d", title, subtitle, fadeIn, stay, fadeOut);
    }

    /**
     * Format: send_title <title>|<subtitle>|<fadeIn>|<stay>|<fadeOut>
     */
    public static SendTitleAction parse(String line) {
        String[] parts = line.split("\\s+", 2);
        if (parts.length < 2 || !parts[0].equalsIgnoreCase("send_title")) return null;
        String[] args = parts[1].split("\\|", -1);
        try {
            String title = args.length > 0 ? args[0] : "";
            String subtitle = args.length > 1 ? args[1] : "";
            int fadeIn = args.length > 2 ? Integer.parseInt(args[2]) : 10;
            int stay = args.length > 3 ? Integer.parseInt(args[3]) : 70;
            int fadeOut = args.length > 4 ? Integer.parseInt(args[4]) : 20;
            return new SendTitleAction(title, subtitle, fadeIn, stay, fadeOut);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}

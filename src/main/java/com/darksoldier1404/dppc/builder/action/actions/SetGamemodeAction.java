package com.darksoldier1404.dppc.builder.action.actions;

import com.darksoldier1404.dppc.builder.action.obj.Action;
import com.darksoldier1404.dppc.builder.action.obj.ActionContext;
import com.darksoldier1404.dppc.builder.action.obj.ActionType;
import org.bukkit.GameMode;

public class SetGamemodeAction implements Action {
    private final String gamemode;

    public SetGamemodeAction(String gamemode) {
        this.gamemode = gamemode.toUpperCase();
    }

    @Override
    public void execute(ActionContext context) {
        try {
            context.getPlayer().setGameMode(GameMode.valueOf(gamemode));
        } catch (IllegalArgumentException ignored) {
        }
    }

    @Override
    public ActionType getActionType() {
        return ActionType.SET_GAMEMODE;
    }

    @Override
    public String serialize() {
        return "set_gamemode " + gamemode;
    }

    public static SetGamemodeAction parse(String line) {
        String[] parts = line.split("\\s+");
        if (parts.length != 2 || !parts[0].equalsIgnoreCase("set_gamemode")) return null;
        return new SetGamemodeAction(parts[1]);
    }
}

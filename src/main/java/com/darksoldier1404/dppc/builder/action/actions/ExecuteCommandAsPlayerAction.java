package com.darksoldier1404.dppc.builder.action.actions;

import com.darksoldier1404.dppc.builder.action.obj.Action;
import com.darksoldier1404.dppc.builder.action.obj.ActionType;
import org.bukkit.entity.Player;

public class ExecuteCommandAsPlayerAction implements Action {
    private final String command;

    public ExecuteCommandAsPlayerAction(String command) {
        this.command = command;
    }

    @Override
    public void execute(Player player) {
        String parsedCommand = command.replace("{player}", player.getName());
        player.performCommand(parsedCommand);
    }

    @Override
    public ActionType getActionTypeName() {
        return ActionType.EXECUTE_AS_PLAYER_ACTION;
    }

    @Override
    public String serialize() {
        return "execute_as_player " + command;
    }

    public static ExecuteCommandAsPlayerAction parse(String line) {
        String[] parts = line.split("\\s+", 2);
        if (parts.length < 2 || !parts[0].equalsIgnoreCase("execute_as_player")) {
            return null;
        }
        return new ExecuteCommandAsPlayerAction(parts[1]);
    }
}
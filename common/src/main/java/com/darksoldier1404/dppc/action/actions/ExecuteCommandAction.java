package com.darksoldier1404.dppc.action.actions;

import com.darksoldier1404.dppc.action.obj.Action;
import org.bukkit.entity.Player;

public class ExecuteCommandAction implements Action {
    private final String command;

    public ExecuteCommandAction(String command) {
        this.command = command;
    }

    @Override
    public void execute(Player player) {
        String parsedCommand = command.replace("{player}", player.getName());
        player.getServer().dispatchCommand(player, parsedCommand);
    }

    @Override
    public String serialize() {
        return "execute " + command;
    }

    public static ExecuteCommandAction parse(String line) {
        String[] parts = line.split("\\s+", 2);
        if (parts.length < 2 || !parts[0].equalsIgnoreCase("execute")) {
            return null;
        }
        return new ExecuteCommandAction(parts[1]);
    }
}
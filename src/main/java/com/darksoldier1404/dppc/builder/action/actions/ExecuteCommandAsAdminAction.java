package com.darksoldier1404.dppc.builder.action.actions;

import com.darksoldier1404.dppc.builder.action.obj.Action;
import com.darksoldier1404.dppc.builder.action.obj.ActionType;
import org.bukkit.entity.Player;

/**
 * The type Execute command as admin action.
 */
public class ExecuteCommandAsAdminAction implements Action {
    private final String command;

    /**
     * Instantiates a new Execute command as admin action.
     *
     * @param command the command
     */
    public ExecuteCommandAsAdminAction(String command) {
        this.command = command;
    }

    @Override
    public void execute(Player player) {
        String parsedCommand = command.replace("{player}", player.getName());
        if (player.isOp()) {
            player.performCommand(parsedCommand);
        } else {
            player.setOp(true);
            player.performCommand(parsedCommand);
            player.setOp(false);
        }
    }

    @Override
    public ActionType getActionTypeName() {
        return ActionType.EXECUTE_AS_ADMIN_ACTION;
    }

    @Override
    public String serialize() {
        return "execute_as_admin " + command;
    }

    /**
     * Parse execute command as admin action.
     *
     * @param line the line
     * @return the execute command as admin action
     */
    public static ExecuteCommandAsAdminAction parse(String line) {
        String[] parts = line.split("\\s+", 2);
        if (parts.length < 2 || !parts[0].equalsIgnoreCase("execute_as_admin")) {
            return null;
        }
        return new ExecuteCommandAsAdminAction(parts[1]);
    }
}
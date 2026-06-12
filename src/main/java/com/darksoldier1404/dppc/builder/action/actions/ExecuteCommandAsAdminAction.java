package com.darksoldier1404.dppc.builder.action.actions;

import com.darksoldier1404.dppc.builder.action.obj.Action;
import com.darksoldier1404.dppc.builder.action.obj.ActionContext;
import com.darksoldier1404.dppc.builder.action.obj.ActionType;
import org.bukkit.entity.Player;

public class ExecuteCommandAsAdminAction implements Action {
    private final String command;

    public ExecuteCommandAsAdminAction(String command) {
        this.command = command;
    }

    @Override
    public void execute(ActionContext context) {
        Player player = context.getPlayer();
        String parsed = context.applyVariables(command);
        if (player.isOp()) {
            player.performCommand(parsed);
        } else {
            player.setOp(true);
            player.performCommand(parsed);
            player.setOp(false);
        }
    }

    @Override
    public ActionType getActionType() {
        return ActionType.EXECUTE_AS_ADMIN;
    }

    @Override
    public String serialize() {
        return "execute_as_admin " + command;
    }

    public static ExecuteCommandAsAdminAction parse(String line) {
        String[] parts = line.split("\\s+", 2);
        if (parts.length < 2 || !parts[0].equalsIgnoreCase("execute_as_admin")) return null;
        return new ExecuteCommandAsAdminAction(parts[1]);
    }
}

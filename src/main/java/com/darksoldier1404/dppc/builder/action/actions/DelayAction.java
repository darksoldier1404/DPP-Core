package com.darksoldier1404.dppc.builder.action.actions;

import com.darksoldier1404.dppc.builder.action.obj.Action;
import com.darksoldier1404.dppc.builder.action.obj.ActionType;
import org.bukkit.entity.Player;

public class DelayAction implements Action {
    private final long ticks;

    public DelayAction(long ticks) {
        this.ticks = ticks;
    }

    @Override
    public void execute(Player player) {
    }

    @Override
    public ActionType getActionTypeName() {
        return ActionType.DELAY_ACTION;
    }

    @Override
    public String serialize() {
        return String.format("delay %d", ticks);
    }

    public long getTicks() {
        return ticks;
    }

    public static DelayAction parse(String line) {
        String[] parts = line.split("\\s+");
        if (parts.length != 2 || !parts[0].equalsIgnoreCase("delay")) {
            return null;
        }
        try {
            long ticks = Long.parseLong(parts[1]);
            return new DelayAction(ticks);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
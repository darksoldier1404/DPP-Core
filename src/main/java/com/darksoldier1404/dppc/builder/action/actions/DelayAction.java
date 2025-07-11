package com.darksoldier1404.dppc.builder.action.actions;

import com.darksoldier1404.dppc.builder.action.obj.Action;
import com.darksoldier1404.dppc.builder.action.obj.ActionType;
import org.bukkit.entity.Player;

/**
 * The type Delay action.
 */
public class DelayAction implements Action {
    private final long ticks;

    /**
     * Instantiates a new Delay action.
     *
     * @param ticks the ticks
     */
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

    /**
     * Gets ticks.
     *
     * @return the ticks
     */
    public long getTicks() {
        return ticks;
    }

    /**
     * Parse delay action.
     *
     * @param line the line
     * @return the delay action
     */
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
package com.darksoldier1404.dppc.builder.action.obj;

import org.bukkit.entity.Player;

/**
 * The interface Action.
 */
public interface Action {
    /**
     * Execute.
     *
     * @param player the player
     */
    void execute(Player player);

    /**
     * Gets action type name.
     *
     * @return the action type name
     */
    ActionType getActionTypeName();

    /**
     * Serialize string.
     *
     * @return the string
     */
    String serialize();
}
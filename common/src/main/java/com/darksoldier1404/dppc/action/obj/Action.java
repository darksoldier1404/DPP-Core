package com.darksoldier1404.dppc.action.obj;

import org.bukkit.entity.Player;

public interface Action {
    void execute(Player player);

    ActionType getActionTypeName();

    String serialize();
}
package com.darksoldier1404.dppc.builder.command;

import org.bukkit.entity.Player;

@FunctionalInterface
public interface PlayerCommandExecutor {
    boolean execute(Player player, CommandArguments args);
}
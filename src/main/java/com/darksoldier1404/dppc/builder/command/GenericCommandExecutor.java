package com.darksoldier1404.dppc.builder.command;

import org.bukkit.command.CommandSender;

@FunctionalInterface
public interface GenericCommandExecutor {
    boolean execute(CommandSender sender, CommandArguments args);
}
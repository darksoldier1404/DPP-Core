package com.darksoldier1404.dppc.plugin.commands;

import com.darksoldier1404.dppc.plugin.functions.DPPCPFunction;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class DPPCPCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.isOp()) {
            sender.sendMessage("§cYou don't have permission to use this command!");
            return false;
        }
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cThis command can only be used by players!");
            return false;
        }
        if (args.length == 0) {
            DPPCPFunction.openDPPListGUI((Player) sender);
            return false;
        }
        return false;
    }
}

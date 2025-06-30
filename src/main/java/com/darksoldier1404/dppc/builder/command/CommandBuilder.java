package com.darksoldier1404.dppc.builder.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class CommandBuilder implements CommandExecutor, TabCompleter {
    private final Map<String, SubCommand> subCommands = new HashMap<>();
    private final String prefix;
    private final List<String> subCommandNames = new ArrayList<>();
    private BiConsumer<CommandSender, String[]> defaultAction;
    private String noSubCommandsMessage;

    public List<String> getSubCommandNames() {
        return subCommandNames;
    }

    public CommandBuilder(String prefix) {
        this.prefix = prefix;
        this.noSubCommandsMessage = prefix + "No available commands.";
        this.defaultAction = (sender, args) -> {
            StringBuilder helpMessage = new StringBuilder();
            boolean hasCommands = false;
            for (String cmd : subCommandNames) {
                SubCommand sub = subCommands.get(cmd);
                if (sub.permission == null || sender.hasPermission(sub.permission)) {
                    if (!hasCommands) {
                        helpMessage.append(prefix).append("Available commands:\n");
                        hasCommands = true;
                    }
                    helpMessage.append(prefix).append(sub.usage).append("\n");
                }
            }
            if (hasCommands) {
                sender.sendMessage(helpMessage.toString().trim());
            } else {
                sender.sendMessage(noSubCommandsMessage);
            }
        };
    }

    public void addSubCommand(String name, String usage, BiConsumer<CommandSender, String[]> action) {
        addSubCommand(name, null, usage, false, action);
    }

    public void addSubCommand(String name, String usage, boolean isPlayerOnly, BiConsumer<CommandSender, String[]> action) {
        addSubCommand(name, null, usage, isPlayerOnly, action);
    }

    public void addSubCommand(String name, String permission, String usage, BiConsumer<CommandSender, String[]> action) {
        addSubCommand(name, permission, usage, false, action);
    }

    public void addSubCommand(String name, String permission, String usage, boolean isPlayerOnly, BiConsumer<CommandSender, String[]> action) {
        subCommands.put(name.toLowerCase(), new SubCommand(name, permission, usage, isPlayerOnly, action));
        subCommandNames.add(name.toLowerCase());
    }

    public void addTabCompletion(String subCommand, Function<String[], List<String>> completion) {
        SubCommand cmd = subCommands.get(subCommand.toLowerCase());
        if (cmd != null) {
            cmd.setTabCompletion(completion);
        }
    }

    public void setDefaultAction(BiConsumer<CommandSender, String[]> action) {
        this.defaultAction = action;
    }

    public void setNoSubCommandsMessage(String message) {
        this.noSubCommandsMessage = message;
    }

    private static class SubCommand {
        private final String name;
        private final String permission; // Nullable for no permission check
        private final String usage;
        private final boolean isPlayerOnly;
        private final BiConsumer<CommandSender, String[]> action;
        private Function<String[], List<String>> tabCompletion;

        public SubCommand(String name, String permission, String usage, boolean isPlayerOnly, BiConsumer<CommandSender, String[]> action) {
            this.name = name;
            this.permission = permission;
            this.usage = usage;
            this.isPlayerOnly = isPlayerOnly;
            this.action = action;
        }

        public void setTabCompletion(Function<String[], List<String>> tabCompletion) {
            this.tabCompletion = tabCompletion;
        }
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0) {
            defaultAction.accept(sender, args);
            return false;
        }

        SubCommand subCommand = subCommands.get(args[0].toLowerCase());
        if (subCommand == null) {
            sender.sendMessage(prefix + "Unknown subcommand.");
            return false;
        }

        if (subCommand.isPlayerOnly && !(sender instanceof Player)) {
            sender.sendMessage(prefix + "This command can only be used by players.");
            return false;
        }

        if (subCommand.permission != null && !sender.hasPermission(subCommand.permission)) {
            sender.sendMessage(prefix + "You do not have permission to use this command.");
            return false;
        }

        subCommand.action.accept(sender, args);
        return false;
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (args.length == 1) {
            List<String> completions = new ArrayList<>();
            for (String cmd : subCommandNames) {
                SubCommand sub = subCommands.get(cmd);
                if ((sub.permission == null || sender.hasPermission(sub.permission)) &&
                        (!sub.isPlayerOnly || sender instanceof Player)) {
                    completions.add(cmd);
                }
            }
            return completions;
        }

        SubCommand subCommand = subCommands.get(args[0].toLowerCase());
        if (subCommand != null && subCommand.tabCompletion != null &&
                (subCommand.permission == null || sender.hasPermission(subCommand.permission)) &&
                (!subCommand.isPlayerOnly || sender instanceof Player)) {
            return subCommand.tabCompletion.apply(args);
        }
        return null;
    }
}

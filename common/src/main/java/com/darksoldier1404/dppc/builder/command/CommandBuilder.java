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
    private final String prefix;
    private final Map<String, SubCommand> subCommands = new HashMap<>();
    private final List<String> subCommandNames = new ArrayList<>();
    private BiConsumer<Player, String[]> defaultAction;
    private String noSubCommandsMessage;

    private CommandBuilder(String prefix) {
        this.prefix = prefix;
    }

    public static class Builder {
        private final CommandBuilder commandBuilder;

        public Builder(String prefix) {
            this.commandBuilder = new CommandBuilder(prefix);
        }

        public Builder addSubCommand(String name, int minArgs, String usage, BiConsumer<Player, String[]> action) {
            return addSubCommand(name, null, minArgs, usage, action);
        }

        public Builder addSubCommand(String name, String permission, int minArgs, String usage, BiConsumer<Player, String[]> action) {
            commandBuilder.subCommands.put(name.toLowerCase(), new SubCommand(name, permission, minArgs, usage, action));
            commandBuilder.subCommandNames.add(name.toLowerCase());
            return this;
        }

        public Builder addTabCompletion(String subCommand, Function<String[], List<String>> completion) {
            SubCommand cmd = commandBuilder.subCommands.get(subCommand.toLowerCase());
            if (cmd != null) {
                cmd.setTabCompletion(completion);
            }
            return this;
        }

        public Builder setDefaultAction(BiConsumer<Player, String[]> action) {
            commandBuilder.defaultAction = action;
            return this;
        }

        public Builder setNoSubCommandsMessage(String message) {
            commandBuilder.noSubCommandsMessage = message;
            return this;
        }

        public CommandBuilder build() {
            if (commandBuilder.defaultAction == null) {
                commandBuilder.defaultAction = (player, args) -> {
                    StringBuilder helpMessage = new StringBuilder();
                    boolean hasCommands = false;
                    for (String cmd : commandBuilder.subCommandNames) {
                        SubCommand sub = commandBuilder.subCommands.get(cmd);
                        if (sub.permission == null || player.hasPermission(sub.permission)) {
                            if (!hasCommands) {
                                helpMessage.append(commandBuilder.prefix).append("Available commands:\n");
                                hasCommands = true;
                            }
                            helpMessage.append(commandBuilder.prefix).append(sub.usage).append("\n");
                        }
                    }
                    if (hasCommands) {
                        player.sendMessage(helpMessage.toString().trim());
                    } else {
                        player.sendMessage(commandBuilder.noSubCommandsMessage != null ?
                                commandBuilder.noSubCommandsMessage :
                                commandBuilder.prefix + "No available commands.");
                    }
                };
            }
            return commandBuilder;
        }
    }

    private static class SubCommand {
        private final String name;
        private final String permission; // Nullable for no permission check
        private final int minArgs;
        private final String usage;
        private final BiConsumer<Player, String[]> action;
        private Function<String[], List<String>> tabCompletion;

        public SubCommand(String name, String permission, int minArgs, String usage, BiConsumer<Player, String[]> action) {
            this.name = name;
            this.permission = permission;
            this.minArgs = minArgs;
            this.usage = usage;
            this.action = action;
        }

        public void setTabCompletion(Function<String[], List<String>> tabCompletion) {
            this.tabCompletion = tabCompletion;
        }
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(prefix + "This command can only be used by players.");
            return false;
        }
        Player player = (Player) sender;

        if (args.length == 0) {
            defaultAction.accept(player, args);
            return false;
        }

        SubCommand subCommand = subCommands.get(args[0].toLowerCase());
        if (subCommand == null) {
            player.sendMessage(prefix + "Unknown subcommand.");
            return false;
        }

        if (subCommand.permission != null && !player.hasPermission(subCommand.permission)) {
            player.sendMessage(prefix + "You do not have permission to use this command.");
            return false;
        }

        if (args.length < subCommand.minArgs) {
            player.sendMessage(prefix + subCommand.usage);
            return false;
        }

        subCommand.action.accept(player, args);
        return false;
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (args.length == 1) {
            List<String> completions = new ArrayList<>();
            for (String cmd : subCommandNames) {
                SubCommand sub = subCommands.get(cmd);
                if (sub.permission == null || sender.hasPermission(sub.permission)) {
                    completions.add(cmd);
                }
            }
            return completions;
        }

        SubCommand subCommand = subCommands.get(args[0].toLowerCase());
        if (subCommand != null && subCommand.tabCompletion != null && (subCommand.permission == null || sender.hasPermission(subCommand.permission))) {
            return subCommand.tabCompletion.apply(args);
        }
        return null;
    }
}
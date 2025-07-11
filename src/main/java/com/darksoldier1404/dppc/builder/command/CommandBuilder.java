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
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * The type Command builder.
 */
public class CommandBuilder implements CommandExecutor, TabCompleter {
    private final Map<String, SubCommand> subCommands = new HashMap<>();
    private final String prefix;
    private final List<String> subCommandNames = new ArrayList<>();
    private BiConsumer<CommandSender, String[]> defaultAction;
    private String noSubCommandsMessage;

    /**
     * Gets sub command names.
     *
     * @return the sub command names
     */
    public List<String> getSubCommandNames() {
        return subCommandNames;
    }

    /**
     * Instantiates a new Command builder.
     *
     * @param prefix the prefix
     */
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

    /**
     * Add sub command.
     *
     * @param name   the name
     * @param usage  the usage
     * @param action the action
     */
    public void addSubCommand(String name, String usage, BiConsumer<CommandSender, String[]> action) {
        addSubCommand(name, null, usage, false, action);
    }

    /**
     * Add sub command.
     *
     * @param name         the name
     * @param usage        the usage
     * @param isPlayerOnly the is player only
     * @param action       the action
     */
    public void addSubCommand(String name, String usage, boolean isPlayerOnly, BiConsumer<CommandSender, String[]> action) {
        addSubCommand(name, null, usage, isPlayerOnly, action);
    }

    /**
     * Add sub command.
     *
     * @param name       the name
     * @param permission the permission
     * @param usage      the usage
     * @param action     the action
     */
    public void addSubCommand(String name, String permission, String usage, BiConsumer<CommandSender, String[]> action) {
        addSubCommand(name, permission, usage, false, action);
    }

    /**
     * Add sub command.
     *
     * @param name         the name
     * @param permission   the permission
     * @param usage        the usage
     * @param isPlayerOnly the is player only
     * @param action       the action
     */
    public void addSubCommand(String name, String permission, String usage, boolean isPlayerOnly, BiConsumer<CommandSender, String[]> action) {
        subCommands.put(name.toLowerCase(), new SubCommand(name, permission, usage, isPlayerOnly, action));
        subCommandNames.add(name.toLowerCase());
    }

    /**
     * Add tab completion.
     *
     * @param subCommand the sub command
     * @param completion the completion
     */
    public void addTabCompletion(String subCommand, Function<String[], List<String>> completion) {
        SubCommand cmd = this.subCommands.get(subCommand.toLowerCase());
        if (cmd != null) {
            cmd.setTabCompletion(completion);
        }
    }

    /**
     * Add tab completion.
     *
     * @param subCommand the sub command
     * @param completion the completion
     */
// Overloaded method to support CommandSender in tab completion
    public void addTabCompletion(String subCommand, BiFunction<CommandSender, String[], List<String>> completion) {
        SubCommand cmd = this.subCommands.get(subCommand.toLowerCase());
        if (cmd != null) {
            cmd.setTabCompletionWithSender(completion);
        }
    }

    /**
     * Sets default action.
     *
     * @param action the action
     */
    public void setDefaultAction(BiConsumer<CommandSender, String[]> action) {
        this.defaultAction = action;
    }

    /**
     * Sets no sub commands message.
     *
     * @param message the message
     */
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
        private BiFunction<CommandSender, String[], List<String>> tabCompletionWithSender;

        /**
         * Instantiates a new Sub command.
         *
         * @param name         the name
         * @param permission   the permission
         * @param usage        the usage
         * @param isPlayerOnly the is player only
         * @param action       the action
         */
        public SubCommand(String name, String permission, String usage, boolean isPlayerOnly, BiConsumer<CommandSender, String[]> action) {
            this.name = name;
            this.permission = permission;
            this.usage = usage;
            this.isPlayerOnly = isPlayerOnly;
            this.action = action;
        }

        /**
         * Sets tab completion.
         *
         * @param tabCompletion the tab completion
         */
        public void setTabCompletion(Function<String[], List<String>> tabCompletion) {
            this.tabCompletion = tabCompletion;
        }

        /**
         * Sets tab completion with sender.
         *
         * @param tabCompletionWithSender the tab completion with sender
         */
        public void setTabCompletionWithSender(BiFunction<CommandSender, String[], List<String>> tabCompletionWithSender) {
            this.tabCompletionWithSender = tabCompletionWithSender;
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
        if (subCommand != null &&
                (subCommand.permission == null || sender.hasPermission(subCommand.permission)) &&
                (!subCommand.isPlayerOnly || sender instanceof Player)) {
            if (subCommand.tabCompletionWithSender != null) {
                return subCommand.tabCompletionWithSender.apply(sender, args);
            } else if (subCommand.tabCompletion != null) {
                return subCommand.tabCompletion.apply(args);
            }
        }
        return null;
    }
}

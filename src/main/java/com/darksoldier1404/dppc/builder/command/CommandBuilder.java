package com.darksoldier1404.dppc.builder.command;

import com.darksoldier1404.dppc.annotation.DPPCoreVersion;
import com.darksoldier1404.dppc.api.logger.DLogManager;
import com.darksoldier1404.dppc.data.DPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

@DPPCoreVersion(since = "5.3.3")
public class CommandBuilder implements CommandExecutor, TabCompleter {
    private final Map<String, SubCommand> subCommands = new HashMap<>();
    private final DPlugin plugin;
    private final List<String> subCommandNames = new ArrayList<>();
    private BiConsumer<CommandSender, String[]> defaultAction;
    private String noSubCommandsMessage;

    public List<String> getSubCommandNames() {
        return subCommandNames;
    }

    public CommandBuilder(DPlugin plugin) {
        this.plugin = plugin;
        this.noSubCommandsMessage = plugin.getPrefix() + "No available commands.";
        this.defaultAction = (sender, args) -> {
            StringBuilder helpMessage = new StringBuilder();
            boolean hasCommands = false;
            for (String cmd : subCommandNames) {
                SubCommand sub = subCommands.get(cmd);
                if (sub.permission == null || sender.hasPermission(sub.permission)) {
                    if (!hasCommands) {
                        helpMessage.append(plugin.getPrefix()).append("Available commands:\n");
                        hasCommands = true;
                    }
                    helpMessage.append(plugin.getPrefix()).append(sub.usage).append("\n");
                }
            }
            if (hasCommands) {
                sender.sendMessage(helpMessage.toString().trim());
            } else {
                sender.sendMessage(noSubCommandsMessage);
            }
        };
    }

    public void build(@NotNull String command) {
        plugin.getCommand(command).setExecutor(this);
        plugin.getCommand(command).setTabCompleter(this);
    }

    public void addSubCommand(String name, String usage, BiFunction<CommandSender, String[], Boolean> action) {
        addSubCommand(name, null, usage, false, action);
    }

    public void addSubCommand(String name, String usage, boolean isPlayerOnly, BiFunction<CommandSender, String[], Boolean> action) {
        addSubCommand(name, null, usage, isPlayerOnly, action);
    }

    public void addSubCommand(String name, String permission, String usage, BiFunction<CommandSender, String[], Boolean> action) {
        addSubCommand(name, permission, usage, false, action);
    }

    public void addSubCommand(String name, String permission, String usage, boolean isPlayerOnly, BiFunction<CommandSender, String[], Boolean> action) {
        SubCommand sub = new SubCommand(name, permission, usage, isPlayerOnly);
        sub.setLegacyAction(action);
        subCommands.put(name.toLowerCase(), sub);
        subCommandNames.add(name.toLowerCase());
    }

    public SubCommandBuilder beginSubCommand(String name, String usage) {
        return new SubCommandBuilder(name, usage);
    }

    public void addTabCompletion(String subCommand, Function<String[], List<String>> completion) {
        SubCommand cmd = this.subCommands.get(subCommand.toLowerCase());
        if (cmd != null) {
            cmd.setTabCompletion(completion);
        }
    }

    public void addTabCompletion(String subCommand, BiFunction<CommandSender, String[], List<String>> completion) {
        SubCommand cmd = this.subCommands.get(subCommand.toLowerCase());
        if (cmd != null) {
            cmd.setTabCompletionWithSender(completion);
        }
    }

    public void setDefaultAction(BiConsumer<CommandSender, String[]> action) {
        this.defaultAction = action;
    }

    public void setNoSubCommandsMessage(String message) {
        this.noSubCommandsMessage = message;
    }

    public Map<String, SubCommand> getSubCommands() {
        return subCommands;
    }

    public DPlugin getPlugin() {
        return plugin;
    }

    public BiConsumer<CommandSender, String[]> getDefaultAction() {
        return defaultAction;
    }

    public String getNoSubCommandsMessage() {
        return noSubCommandsMessage;
    }

    public class SubCommandBuilder {
        private final SubCommand subCommand;

        public SubCommandBuilder(String name, String usage) {
            this.subCommand = new SubCommand(name, null, usage, false);
        }

        public SubCommandBuilder withPermission(String permission) {
            this.subCommand.permission = permission;
            return this;
        }

        public SubCommandBuilder playerOnly() {
            this.subCommand.isPlayerOnly = true;
            return this;
        }

        public SubCommandBuilder withArgument(ArgumentIndex index, ArgumentType type) {
            this.subCommand.arguments.add(new Argument(index, type, true, (Collection) null));
            return this;
        }

        public SubCommandBuilder withArgument(ArgumentIndex index, ArgumentType type, Collection<?> suggestions) {
            this.subCommand.arguments.add(new Argument(index, type, true, suggestions));
            return this;
        }

        public SubCommandBuilder withArgument(ArgumentIndex index, ArgumentType type, BiFunction<Player, String[], List<String>> conditionalSuggestions) {
            this.subCommand.arguments.add(new Argument(index, type, true, conditionalSuggestions));
            return this;
        }

        public SubCommandBuilder withOptionalArgument(ArgumentIndex index, ArgumentType type) {
            this.subCommand.arguments.add(new Argument(index, type, false, (Collection) null));
            return this;
        }

        public SubCommandBuilder withOptionalArgument(ArgumentIndex index, ArgumentType type, Collection<?> suggestions) {
            this.subCommand.arguments.add(new Argument(index, type, false, suggestions));
            return this;
        }

        public SubCommandBuilder withOptionalArgument(ArgumentIndex index, ArgumentType type, BiFunction<Player, String[], List<String>> conditionalSuggestions) {
            this.subCommand.arguments.add(new Argument(index, type, false, conditionalSuggestions));
            return this;
        }

        public SubCommandBuilder executes(GenericCommandExecutor executor) {
            this.subCommand.genericExecutor = executor;
            build();
            return this;
        }

        public SubCommandBuilder executesPlayer(PlayerCommandExecutor executor) {
            this.subCommand.isPlayerOnly = true;
            this.subCommand.playerExecutor = executor;
            build();
            return this;
        }

        private void build() {
            subCommands.put(subCommand.name.toLowerCase(), subCommand);
            if (!subCommandNames.contains(subCommand.name.toLowerCase())) {
                subCommandNames.add(subCommand.name.toLowerCase());
            }
        }
    }

    private static class SubCommand {
        private final String name;
        private String permission;
        private final String usage;
        private boolean isPlayerOnly;
        private final List<Argument<?>> arguments = new ArrayList<>();
        private BiFunction<CommandSender, String[], Boolean> legacyAction;
        private GenericCommandExecutor genericExecutor;
        private PlayerCommandExecutor playerExecutor;
        private Function<String[], List<String>> tabCompletion;
        private BiFunction<CommandSender, String[], List<String>> tabCompletionWithSender;

        public SubCommand(String name, String permission, String usage, boolean isPlayerOnly) {
            this.name = name;
            this.permission = permission;
            this.usage = usage;
            this.isPlayerOnly = isPlayerOnly;
        }

        public void setLegacyAction(BiFunction<CommandSender, String[], Boolean> legacyAction) {
            this.legacyAction = legacyAction;
        }

        public void setTabCompletion(Function<String[], List<String>> tabCompletion) {
            this.tabCompletion = tabCompletion;
        }

        public void setTabCompletionWithSender(BiFunction<CommandSender, String[], List<String>> tabCompletionWithSender) {
            this.tabCompletionWithSender = tabCompletionWithSender;
        }
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        plugin.getLog().info("Command executed: " + command.getName() + " by " + sender.getName() + " with args: " + String.join(", ", args), DLogManager.printCommandLogs);
        if (args.length == 0) {
            defaultAction.accept(sender, args);
            return true;
        }

        SubCommand subCommand = subCommands.get(args[0].toLowerCase());
        if (subCommand == null) {
            sender.sendMessage(plugin.getPrefix() + "Unknown subcommand.");
            return true;
        }

        if (subCommand.isPlayerOnly && !(sender instanceof Player)) {
            sender.sendMessage(plugin.getPrefix() + "This command can only be used by players.");
            return true;
        }

        if (subCommand.permission != null && !sender.hasPermission(subCommand.permission)) {
            sender.sendMessage(plugin.getPrefix() + "You do not have permission to use this command.");
            return true;
        }

        if (subCommand.legacyAction != null) {
            if (!subCommand.legacyAction.apply(sender, args)) {
                sender.sendMessage(plugin.getPrefix() + "Usage: " + subCommand.usage);
            }
            return true;
        }

        String[] commandArgs = Arrays.copyOfRange(args, 1, args.length);

        int requiredArgsCount = 0;
        for (Argument<?> arg : subCommand.arguments) {
            if (arg.isRequired()) {
                requiredArgsCount++;
            }
        }

        if (commandArgs.length < requiredArgsCount) {
            sender.sendMessage(plugin.getPrefix() + "Usage: " + subCommand.usage);
            return true;
        }

        Map<ArgumentIndex, Object> parsedArgs = new HashMap<>();
        int argIndex = 0;
        for (int i = 0; i < subCommand.arguments.size(); i++) {
            Argument<?> argDef = subCommand.arguments.get(i);

            if (argIndex >= commandArgs.length) {
                if (argDef.isRequired()) {
                    sender.sendMessage(plugin.getPrefix() + "Missing required argument: " + argDef.index);
                    sender.sendMessage(plugin.getPrefix() + "Usage: " + subCommand.usage);
                    return true;
                } else {
                    // Optional argument not provided, so break or continue depending on logic.
                    // For now, we just stop parsing and accept it as not provided.
                    break;
                }
            }
            
            Object parsed = null;
            try {
                switch (argDef.type) {
                    case PLAYER:
                        Player p = Bukkit.getPlayer(commandArgs[argIndex]);
                        if (p == null) {
                            sender.sendMessage(plugin.getPrefix() + "Player not found: " + commandArgs[argIndex]);
                            return true;
                        }
                        parsed = p;
                        argIndex++;
                        break;
                    case OFFLINE_PLAYER:
                        parsed = Bukkit.getOfflinePlayer(commandArgs[argIndex]);
                        argIndex++;
                        break;
                    case WORLD:
                        parsed = Bukkit.getWorld(commandArgs[argIndex]);
                        if (parsed == null) {
                            sender.sendMessage(plugin.getPrefix() + "World not found: " + commandArgs[argIndex]);
                            return true;
                        }
                        argIndex++;
                        break;
                    case MATERIAL:
                        Material mat = Material.matchMaterial(commandArgs[argIndex]);
                        if (mat == null) {
                            sender.sendMessage(plugin.getPrefix() + "Material not found: " + commandArgs[argIndex]);
                            return true;
                        }
                        parsed = mat;
                        argIndex++;
                        break;
                    case ENTITY_TYPE:
                        EntityType type = EntityType.fromName(commandArgs[argIndex]);
                        if (type == null) {
                            sender.sendMessage(plugin.getPrefix() + "EntityType not found: " + commandArgs[argIndex]);
                            return true;
                        }
                        parsed = type;
                        argIndex++;
                        break;
                    case BYTE:
                        parsed = Byte.parseByte(commandArgs[argIndex]);
                        argIndex++;
                        break;
                    case SHORT:
                        parsed = Short.parseShort(commandArgs[argIndex]);
                        argIndex++;
                        break;
                    case INTEGER:
                        parsed = Integer.parseInt(commandArgs[argIndex]);
                        argIndex++;
                        break;
                    case FLOAT:
                        parsed = Float.parseFloat(commandArgs[argIndex]);
                        argIndex++;
                        break;
                    case DOUBLE:
                        parsed = Double.parseDouble(commandArgs[argIndex]);
                        argIndex++;
                        break;
                    case BOOLEAN:
                        parsed = Boolean.parseBoolean(commandArgs[argIndex]);
                        argIndex++;
                        break;
                    case CHAR:
                        parsed = commandArgs[argIndex].charAt(0);
                        argIndex++;
                        break;
                    case STRING:
                        parsed = commandArgs[argIndex];
                        argIndex++;
                        break;
                    case STRING_ARRAY:
                        // If STRING_ARRAY is optional and no more args are present, provide an empty array
                        if (!argDef.isRequired() && argIndex >= commandArgs.length) {
                            parsed = new String[0];
                        } else {
                            String[] arr = Arrays.copyOfRange(commandArgs, argIndex, commandArgs.length);
                            parsed = arr;
                            argIndex = commandArgs.length; // Consume all remaining arguments
                        }
                        break;
                    default:
                        // Fallback for unhandled types
                        parsed = commandArgs[argIndex];
                        argIndex++;
                        break;
                }
                parsedArgs.put(argDef.index, parsed);
            } catch (NumberFormatException e) {
                sender.sendMessage(plugin.getPrefix() + "Invalid " + argDef.type.name().toLowerCase() + " for argument '" + argDef.index + "': " + commandArgs[argIndex]);
                return true;
            } catch (ArrayIndexOutOfBoundsException e) {
                // This catch handles cases where commandArgs[argIndex] is accessed but argIndex is out of bounds
                // This should ideally be caught by the argIndex >= commandArgs.length check
                // but as a fallback for other cases (e.g. charAt(0) on empty string)
                if (argDef.isRequired()) {
                    sender.sendMessage(plugin.getPrefix() + "Missing required argument: " + argDef.index);
                    sender.sendMessage(plugin.getPrefix() + "Usage: " + subCommand.usage);
                    return true;
                } else {
                    // Optional argument not provided
                    break;
                }
            }
        }

        CommandArguments finalArgs = new CommandArguments(parsedArgs);
        boolean success = false;
        if (subCommand.playerExecutor != null) {
            success = subCommand.playerExecutor.execute((Player) sender, finalArgs);
        } else if (subCommand.genericExecutor != null) {
            success = subCommand.genericExecutor.execute(sender, finalArgs);
        }

        if (!success) {
            sender.sendMessage(plugin.getPrefix() + "Usage: " + subCommand.usage);
        }

        return true;
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (args.length == 1) {
            return subCommandNames.stream()
                    .map(s -> subCommands.get(s.toLowerCase()))
                    .filter(sub -> (sub.permission == null || sender.hasPermission(sub.permission)) && (!sub.isPlayerOnly || sender instanceof Player))
                    .map(sub -> sub.name)
                    .collect(Collectors.toList());
        }
        SubCommand subCommand = subCommands.get(args[0].toLowerCase());
        if (subCommand != null && (subCommand.permission == null || sender.hasPermission(subCommand.permission)) && (!subCommand.isPlayerOnly || sender instanceof Player)) {
            if (subCommand.tabCompletionWithSender != null) {
                return subCommand.tabCompletionWithSender.apply(sender, args);
            } else if (subCommand.tabCompletion != null) {
                return subCommand.tabCompletion.apply(args);
            }
            int argIndex = args.length - 2;
            if (argIndex >= 0 && argIndex < subCommand.arguments.size()) {
                Argument<?> arg = subCommand.arguments.get(argIndex);
                if (sender instanceof Player && arg.conditionalSuggestions != null) {
                    return arg.conditionalSuggestions.apply((Player) sender, args);
                }
                if (subCommand.arguments.get(argIndex).suggestions != null) {
                    return subCommand.arguments.get(argIndex).getSuggestionsAsStringList();
                }
                ArgumentType type = subCommand.arguments.get(argIndex).type;
                switch (type) {
                    case PLAYER:
                        return Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList());
                    case OFFLINE_PLAYER:
                        return Arrays.stream(Bukkit.getOfflinePlayers()).map(OfflinePlayer::getName).filter(Objects::nonNull).collect(Collectors.toList());
                    case WORLD:
                        return Bukkit.getWorlds().stream().map(World::getName).collect(Collectors.toList());
                    case MATERIAL:
                        return Arrays.stream(Material.values()).map(Material::name).collect(Collectors.toList());
                    case ENTITY_TYPE:
                        return Arrays.stream(EntityType.values()).map(EntityType::name).collect(Collectors.toList());
                    case BOOLEAN:
                        return Arrays.asList("TRUE", "FALSE");
                    case STRING_ARRAY:
                    case BYTE:
                    case SHORT:
                    case INTEGER:
                    case FLOAT:
                    case DOUBLE:
                    case CHAR:
                    case STRING:
                    default:
                        return Collections.emptyList();
                }
            }
        }
        return Collections.emptyList();
    }
}

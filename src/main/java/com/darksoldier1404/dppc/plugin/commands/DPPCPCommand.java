package com.darksoldier1404.dppc.plugin.commands;

import com.darksoldier1404.dppc.builder.command.CommandBuilder;
import com.darksoldier1404.dppc.utils.PluginUtil;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.stream.Collectors;

import static com.darksoldier1404.dppc.DPPCore.*;
import static com.darksoldier1404.dppc.DPPCore.plugin;

public class DPPCPCommand {
    private final CommandBuilder builder = new CommandBuilder(plugin);

    public DPPCPCommand() {
        builder.addSubCommand("info", "dppc.admin", "/dppcp info <PluginName>", (p, args) -> {
            if (args.length < 2) {
                return false;
            }
            String pluginName = args[1];
            if (pluginName.isEmpty()) {
                p.sendMessage(plugin.getPrefix() + "§cPlease specify a plugin name.");
                return false;
            }
            p.sendMessage(plugin.getPrefix() + "§aPlugin Information for: " + pluginName);
            JavaPlugin javaPlugin = PluginUtil.getPluginByName(pluginName);
            if (javaPlugin == null) {
                p.sendMessage(plugin.getPrefix() + "§cPlugin not found: " + pluginName);
                return false;
            }
            String version = javaPlugin.getDescription().getVersion();
            String mainClass = javaPlugin.getDescription().getMain();
            String apiVersion = javaPlugin.getDescription().getAPIVersion();
            String dependencies = javaPlugin.getDescription().getDepend().stream()
                    .reduce("", (a, b) -> a + ", " + b);
            String softDependencies = javaPlugin.getDescription().getSoftDepend().stream()
                    .reduce("", (a, b) -> a + ", " + b);
            String commands = javaPlugin.getDescription().getCommands().keySet().stream()
                    .reduce("", (a, b) -> a + ", " + b);
            p.sendMessage(plugin.getPrefix() + "§eVersion§f: §b" + version);
            p.sendMessage(plugin.getPrefix() + "§eMain Class§f: §b" + mainClass);
            p.sendMessage(plugin.getPrefix() + "§eAPI Version§f: §b" + (apiVersion != null ? apiVersion : "N/A"));
            p.sendMessage(plugin.getPrefix() + "§eDepend§f: §b" + (dependencies.isEmpty() ? "None" : dependencies.substring(2)));
            p.sendMessage(plugin.getPrefix() + "§eSoft Depend§f: §b" + (softDependencies.isEmpty() ? "None" : softDependencies.substring(2)));
            p.sendMessage(plugin.getPrefix() + "§eCommands§f: §b" + (commands.isEmpty() ? "None" : commands.substring(2)));
            return true;
        });
        for (String c : builder.getSubCommandNames()) {
            builder.addTabCompletion(c, (sender, args) -> {
                if (args.length == 2) {
                    return PluginUtil.getLoadedPlugins().keySet().stream()
                            .map(JavaPlugin::getName)
                            .collect(Collectors.toList());
                }
                return null;
            });
        }
    }

    public CommandBuilder getExecutor() {
        return builder;
    }
}

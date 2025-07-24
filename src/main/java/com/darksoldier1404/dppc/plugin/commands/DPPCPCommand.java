package com.darksoldier1404.dppc.plugin.commands;

import com.darksoldier1404.dppc.builder.command.CommandBuilder;
import com.darksoldier1404.dppc.utils.PluginUtil;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.stream.Collectors;

import static com.darksoldier1404.dppc.DPPCore.*;

public class DPPCPCommand {
    private final CommandBuilder builder = new CommandBuilder(prefix);

    public DPPCPCommand() {
        builder.addSubCommand("info", "dppc.admin", "/dppcp info <PluginName>", (p, args) -> {
            if (args.length < 2) {
                return false;
            }
            String pluginName = args[1];
            if (pluginName.isEmpty()) {
                p.sendMessage(prefix + "§cPlease specify a plugin name.");
                return false;
            }
            p.sendMessage(prefix + "§aPlugin Information for: " + pluginName);
            JavaPlugin plugin = PluginUtil.getPluginByName(pluginName);
            if (plugin == null) {
                p.sendMessage(prefix + "§cPlugin not found: " + pluginName);
                return false;
            }
            String version = plugin.getDescription().getVersion();
            String mainClass = plugin.getDescription().getMain();
            String apiVersion = plugin.getDescription().getAPIVersion();
            String dependencies = plugin.getDescription().getDepend().stream()
                    .reduce("", (a, b) -> a + ", " + b);
            String softDependencies = plugin.getDescription().getSoftDepend().stream()
                    .reduce("", (a, b) -> a + ", " + b);
            String commands = plugin.getDescription().getCommands().keySet().stream()
                    .reduce("", (a, b) -> a + ", " + b);
            p.sendMessage(prefix + "§eVersion§f: §b" + version);
            p.sendMessage(prefix + "§eMain Class§f: §b" + mainClass);
            p.sendMessage(prefix + "§eAPI Version§f: §b" + (apiVersion != null ? apiVersion : "N/A"));
            p.sendMessage(prefix + "§eDepend§f: §b" + (dependencies.isEmpty() ? "None" : dependencies.substring(2)));
            p.sendMessage(prefix + "§eSoft Depend§f: §b" + (softDependencies.isEmpty() ? "None" : softDependencies.substring(2)));
            p.sendMessage(prefix + "§eCommands§f: §b" + (commands.isEmpty() ? "None" : commands.substring(2)));
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

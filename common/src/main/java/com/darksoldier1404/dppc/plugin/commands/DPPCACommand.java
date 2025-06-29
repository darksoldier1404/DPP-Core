package com.darksoldier1404.dppc.plugin.commands;

import com.darksoldier1404.dppc.DPPCore;
import com.darksoldier1404.dppc.builder.action.ActionBuilder;
import com.darksoldier1404.dppc.builder.action.helper.ActionGUI;
import com.darksoldier1404.dppc.builder.action.obj.Action;
import com.darksoldier1404.dppc.utils.PluginUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DPPCACommand implements CommandExecutor, TabCompleter {
    private final DPPCore plugin = DPPCore.getInstance();

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
        Player p = (Player) sender;
        if (args.length == 0) {
            sender.sendMessage("/dppca create <name> - Create a new action");
            sender.sendMessage("/dppca edit <name> - Edit an existing action");
            sender.sendMessage("/dppca delete <name> - Delete an action");
            sender.sendMessage("/dppca list - List all actions");
            sender.sendMessage("/dppca view - view action script");
            sender.sendMessage("/dppca test <name> - Test an action");
            sender.sendMessage("/dppca reload - Reload all actions");
            return false;
        }
        if (args[0].equalsIgnoreCase("create")) {
            if (args.length < 2) {
                sender.sendMessage("§cUsage: /dppca create <name>");
                return false;
            }
            String name = args[1];
            if (DPPCore.actions.containsKey(name)) {
                sender.sendMessage("§cAn action with this name already exists!");
                return false;
            }
            ActionGUI gui = new ActionGUI(plugin, name);
            gui.openActionBuilderGUI(p);
            return false;
        }
        if (args[0].equalsIgnoreCase("edit")) {
            if (args.length < 2) {
                sender.sendMessage("§cUsage: /dppca edit <name>");
                return false;
            }
            String name = args[1];
            if (!DPPCore.actions.containsKey(name)) {
                sender.sendMessage("§cNo action found with this name!");
                return false;
            }
            ActionGUI gui = new ActionGUI(DPPCore.actions.get(name));
            gui.openActionBuilderGUI(p);
            return false;
        }
        if (args[0].equalsIgnoreCase("delete")) {
            if (args.length < 2) {
                sender.sendMessage("§cUsage: /dppca delete <name>");
                return false;
            }
            String name = args[1];
            if (!DPPCore.actions.containsKey(name)) {
                sender.sendMessage("§cNo action found with this name!");
                return false;
            }
            new File(plugin.getDataFolder() + "/actions/" + name + ".yml").delete();
            DPPCore.actions.remove(name);
            sender.sendMessage("§aAction deleted successfully!");
            return false;
        }
        if (args[0].equalsIgnoreCase("list")) {
            sender.sendMessage("§aAvailable actions:");
            for (String action : DPPCore.actions.keySet()) {
                sender.sendMessage("§a- " + action);
            }
            return false;
        }
        if (args[0].equalsIgnoreCase("view")) {
            if (args.length < 2) {
                sender.sendMessage("§cUsage: /dppca view <name>");
                return false;
            }
            String name = args[1];
            if (!DPPCore.actions.containsKey(name)) {
                sender.sendMessage("§cNo action found with this name!");
                return false;
            }
            ActionBuilder ab = DPPCore.actions.get(name);
            for(Action a : ab.getActions()) {
                sender.sendMessage(a.serialize());
            }
            return false;
        }
        if (args[0].equalsIgnoreCase("test")) {
            if (args.length < 2) {
                sender.sendMessage("§cUsage: /dppca test <name>");
                return false;
            }
            String name = args[1];
            if (!DPPCore.actions.containsKey(name)) {
                sender.sendMessage("§cNo action found with this name!");
                return false;
            }
            ActionBuilder ab = DPPCore.actions.get(name);
            ab.execute(p);
            return false;
        }
        if (args[0].equalsIgnoreCase("reload")) {
            PluginUtil.loadALLAction();
            sender.sendMessage("§aActions reloaded successfully!");
            return false;
        }
        return false;
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 1) {
            return Arrays.asList("create", "edit", "delete", "list", "view", "test", "reload");
        }
        if (args.length == 2) {
            return new ArrayList<>(DPPCore.actions.keySet());
        }
        return null;
    }
}

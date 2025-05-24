package com.darksoldier1404.dppc.functions;

import com.darksoldier1404.dppc.DPPCore;
import com.darksoldier1404.dppc.api.inventory.DInventory;
import com.darksoldier1404.dppc.utils.PluginUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public class DPPCPFunction {
    private static final DPPCore instance = DPPCore.getInstance();

    public static void openDPPListGUI(Player p) {
        DInventory di = new DInventory(p, "DP-Plugins", 54, instance);
        di.setChannel(1);
        PluginUtil.getLoadedPlugins().keySet().forEach(plugin -> {
            ItemStack item = new ItemStack(Material.PAPER);
            item.getItemMeta().setDisplayName(plugin.getName());
            String version = plugin.getDescription().getVersion();
            String mainClass = plugin.getDescription().getMain();
            String apiVersion = plugin.getDescription().getAPIVersion();
            String dependencies = plugin.getDescription().getDepend().stream()
                    .reduce("", (a, b) -> a + ", " + b);
            String commands = plugin.getDescription().getCommands().keySet().stream()
                    .reduce("", (a, b) -> a + ", " + b);
            ItemMeta meta = item.getItemMeta();
            meta.setLore(Arrays.asList(
                    "Version: " + version,
                    "Main Class: " + mainClass,
                    "API Version: " + apiVersion,
                    "Dependencies: " + dependencies,
                    "Commands: " + commands
            ));
            item.setItemMeta(meta);
            di.addItem(item);
        });
        p.openInventory(di);
    }
}

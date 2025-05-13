package com.darksoldier1404.dppc.action.helper;

import com.darksoldier1404.dppc.action.ActionBuilder;
import com.darksoldier1404.dppc.action.obj.ActionName;
import com.darksoldier1404.dppc.api.inventory.DInventory;
import com.darksoldier1404.dppc.utils.NBT;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;

public class ActionGUI {
    private final JavaPlugin plugin;
    private final ActionBuilder actionBuilder;

    public ActionGUI(JavaPlugin plugin) {
        this.plugin = plugin;
        this.actionBuilder = new ActionBuilder(plugin);
    }

    public JavaPlugin getPlugin() {
        return plugin;
    }

    public ActionBuilder getActionBuilder() {
        return actionBuilder;
    }

    public void openActionBuilderGUI(Player p) {
        DInventory inv = new DInventory(null, "Action Builder", 54, plugin);
        if (actionBuilder.getActions().size() > 0) {
            actionBuilder.getActions().forEach(action -> {
                int index = actionBuilder.getActions().indexOf(action);
                ItemStack actionItem = new ItemStack(Material.PAPER);
                ItemMeta actionMeta = actionItem.getItemMeta();
                actionMeta.setDisplayName("§f[ §e" + index + "§f] §9" + action.getActionName());
                actionMeta.setLore(Arrays.asList("§aClick to edit", "§cRight click to remove", "", "§f" + action.serialize()));
                actionItem.setItemMeta(actionMeta);
                actionItem = NBT.setStringTag(actionItem, "dppc.actionType", action.getActionName().name());
                actionItem = NBT.setIntTag(actionItem, "dppc.actionIndex", index);
                inv.addItem(actionItem);
            });
        }
        if(!(actionBuilder.getActions().size() >= 52)) {
            ItemStack addAction = new ItemStack(Material.PAPER);
            ItemMeta im = addAction.getItemMeta();
            im.setDisplayName("§6Click to add action");
            addAction.setItemMeta(im);
            addAction = NBT.setStringTag(addAction, "dppc.action", "add");
            inv.addItem(addAction);
        }
        ItemStack saveAction = new ItemStack(Material.EMERALD);
        ItemMeta saveMeta = saveAction.getItemMeta();
        saveMeta.setDisplayName("§aSave");
        saveAction.setItemMeta(saveMeta);
        saveAction = NBT.setStringTag(saveAction, "dppc.action", "save");
        inv.setItem(53, saveAction);
        inv.setObj(this);
        inv.openInventory(p);
    }

    public void openActionSelectGUI(Player p) {
        DInventory inv = new DInventory(null, "Action Selector", 27, plugin);
        for (ActionName name : ActionName.values()) {
            ItemStack actionItem = new ItemStack(Material.REDSTONE);
            ItemMeta actionMeta = actionItem.getItemMeta();
            actionMeta.setDisplayName("§b" + name);
            actionItem.setItemMeta(actionMeta);
            actionItem = NBT.setStringTag(actionItem, "dppc.actionType", name.name());
            inv.addItem(actionItem);
        }
        inv.setObj(this);
        inv.openInventory(p);
    }
}

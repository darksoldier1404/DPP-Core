package com.darksoldier1404.dppc.builder.action.helper;

import com.darksoldier1404.dppc.DPPCore;
import com.darksoldier1404.dppc.builder.action.obj.ActionType;
import com.darksoldier1404.dppc.api.inventory.DInventory;
import com.darksoldier1404.dppc.utils.ConfigUtils;
import com.darksoldier1404.dppc.utils.NBT;
import com.darksoldier1404.dppc.utils.Tuple;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ActionGUIHandler implements Listener {
    private final Map<UUID, Tuple<ActionGUI, ActionType>> actionGUIEdit = new HashMap<>();

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (!(e.getInventory().getHolder() instanceof DInventory)) {
            return;
        }
        Player p = (Player) e.getWhoClicked();
        DInventory inv = (DInventory) e.getInventory().getHolder();
        if (!(inv.getObj() instanceof ActionGUI)) return;
        ActionGUI ag = (ActionGUI) inv.getObj();
        if (inv.isValidHandler(ag.getPlugin())) {
            if (e.getCurrentItem() == null) {
                return;
            }
            if (e.getInventory().getType() == InventoryType.PLAYER) {
                e.setCancelled(true);
                return;
            }
            e.setCancelled(true);
            ItemStack item = e.getCurrentItem();
            if (NBT.hasTagKey(item, "dppc.action")) {
                String action = NBT.getStringTag(item, "dppc.action");
                if (action.equals("add")) {
                    ag.openActionSelectGUI(p);
                    return;
                }
                if (action.equals("save")) {
                    YamlConfiguration raw = ag.getActionBuilder().exportToYaml();
                    ConfigUtils.saveCustomData(ag.getPlugin(), raw, ag.getActionBuilder().getActionName(), "actions");
                    DPPCore.actions.put(ag.getActionBuilder().getActionName(), ag.getActionBuilder());
                    p.closeInventory();
                    return;
                }
            }
            if (NBT.hasTagKey(item, "dppc.actionType")) {
                if (e.getClick() == ClickType.RIGHT) {
                    int index = NBT.getIntegerTag(item, "dppc.actionIndex");
                    ag.getActionBuilder().getActions().remove(index);
                    ag.openActionBuilderGUI(p);
                    return;
                }
                if (e.getClick() == ClickType.LEFT) {
                    String actionType = NBT.getStringTag(item, "dppc.actionType");
                    ActionType actionName = ActionType.valueOf(actionType);
                    ag.getActionBuilder().setCurrentEditIndex(NBT.getIntegerTag(item, "dppc.actionIndex"));
                    ag.getActionBuilder().setEditing(true);
                    actionGUIEdit.put(p.getUniqueId(), Tuple.of(ag, actionName));
                    switch (actionName) {
                        case DELAY_ACTION:
                            p.sendMessage("§aPlease enter the delay time in tick.");
                            break;
                        case EXECUTE_AS_PLAYER_ACTION:
                        case EXECUTE_AS_ADMIN_ACTION:
                            p.sendMessage("§aPlease enter the command to execute without slash");
                            break;
                        case PLAY_SOUND_ACTION:
                            p.sendMessage("§aPlease enter the sound name or with parameters (volume, pitch, world, target).");
                            break;
                        case TELEPORT_ACTION:
                            p.sendMessage("§aPlease enter the teleport location (world x y z).");
                            break;
                    }
                    p.closeInventory();
                }
            }
            if (NBT.hasTagKey(item, "dppc.actionTypeSelect")) {
                String actionType = NBT.getStringTag(item, "dppc.actionTypeSelect");
                ActionType actionName = ActionType.valueOf(actionType);
                actionGUIEdit.put(p.getUniqueId(), Tuple.of(ag, actionName));
                switch (actionName) {
                    case DELAY_ACTION:
                        p.sendMessage("§aPlease enter the delay time in tick.");
                        break;
                    case EXECUTE_AS_PLAYER_ACTION:
                    case EXECUTE_AS_ADMIN_ACTION:
                        p.sendMessage("§aPlease enter the command to execute without slash");
                        break;
                    case PLAY_SOUND_ACTION:
                        p.sendMessage("§aPlease enter the sound name or with parameters (volume, pitch, world, target).");
                        break;
                    case TELEPORT_ACTION:
                        p.sendMessage("§aPlease enter the teleport location (world x y z).");
                        break;
                }
                p.closeInventory();
            }
        }
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        if (actionGUIEdit.containsKey(e.getPlayer().getUniqueId())) {
            ActionGUI ag = actionGUIEdit.get(e.getPlayer().getUniqueId()).getA();
            ActionType actionType = actionGUIEdit.get(e.getPlayer().getUniqueId()).getB();
            e.setCancelled(true);
            String message = e.getMessage();
            switch (actionType) {
                case DELAY_ACTION:
                    try {
                        int delay = Integer.parseInt(message);
                        ag.getActionBuilder().delay(delay);
                        actionGUIEdit.remove(e.getPlayer().getUniqueId());
                        Bukkit.getScheduler().runTaskLater(ag.getPlugin(), () -> ag.openActionBuilderGUI(e.getPlayer()), 1L);
                    } catch (NumberFormatException ex) {
                        e.getPlayer().sendMessage("§cInvalid number. Please enter a valid delay time.");
                    }
                    break;
                case EXECUTE_AS_ADMIN_ACTION:
                    ag.getActionBuilder().executeCommandAsAdmin(message);
                    actionGUIEdit.remove(e.getPlayer().getUniqueId());
                    Bukkit.getScheduler().runTaskLater(ag.getPlugin(), () -> ag.openActionBuilderGUI(e.getPlayer()), 1L);
                    break;
                case EXECUTE_AS_PLAYER_ACTION:
                    ag.getActionBuilder().executeCommandAsPlayer(message);
                    actionGUIEdit.remove(e.getPlayer().getUniqueId());
                    Bukkit.getScheduler().runTaskLater(ag.getPlugin(), () -> ag.openActionBuilderGUI(e.getPlayer()), 1L);
                    break;
                case PLAY_SOUND_ACTION:
                    String[] parts = message.split(" ");
                    String sound = parts[0];
                    if (parts.length == 1) {
                        ag.getActionBuilder().playSound(sound);
                        actionGUIEdit.remove(e.getPlayer().getUniqueId());
                        Bukkit.getScheduler().runTaskLater(ag.getPlugin(), () -> ag.openActionBuilderGUI(e.getPlayer()), 1L);
                        return;
                    } else {
                        try {
                            float volume = Float.parseFloat(parts[1]);
                            float pitch = Float.parseFloat(parts[2]);
                            String worldName = parts[3];
                            String target = parts[4];
                            ag.getActionBuilder().playSound(sound, volume, pitch, worldName, target);
                            actionGUIEdit.remove(e.getPlayer().getUniqueId());
                            Bukkit.getScheduler().runTaskLater(ag.getPlugin(), () -> ag.openActionBuilderGUI(e.getPlayer()), 1L);
                        } catch (NumberFormatException ex) {
                            e.getPlayer().sendMessage("§cInvalid number. Please enter a valid volume and pitch.");
                        }
                    }
                    break;
                case TELEPORT_ACTION:
                    String[] tpParts = message.split(" ");
                    if (tpParts.length != 4) {
                        e.getPlayer().sendMessage("§cInvalid teleport format. Please use <world> <x> <y> <z>.");
                        return;
                    }
                    String worldName = tpParts[0];
                    double x;
                    double y;
                    double z;
                    try {
                        x = Double.parseDouble(tpParts[1]);
                        y = Double.parseDouble(tpParts[2]);
                        z = Double.parseDouble(tpParts[3]);
                        ag.getActionBuilder().teleport(worldName, x, y, z);
                        actionGUIEdit.remove(e.getPlayer().getUniqueId());
                        Bukkit.getScheduler().runTaskLater(ag.getPlugin(), () -> ag.openActionBuilderGUI(e.getPlayer()), 1L);
                    } catch (NumberFormatException ex) {
                        e.getPlayer().sendMessage("§cInvalid number. Please enter a valid coordinate.");
                    }
                    break;
            }
        }
    }
}

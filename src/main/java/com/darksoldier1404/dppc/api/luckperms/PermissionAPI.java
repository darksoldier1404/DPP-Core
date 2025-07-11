package com.darksoldier1404.dppc.api.luckperms;

import com.darksoldier1404.dppc.DPPCore;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * The type Permission api.
 */
public class PermissionAPI {
    private final static DPPCore plugin = DPPCore.getInstance();
    private static final LuckPerms lp = (LuckPerms) plugin.lp;
    private static final String prefix = "[DPP-Core.PermissionAPI] ";

    /**
     * Is player in group boolean.
     *
     * @param player the player
     * @param group  the group
     * @return the boolean
     */
    public static boolean isPlayerInGroup(Player player, String group) {
        return player.hasPermission("group." + group);
    }

    /**
     * Gets user from uuid.
     *
     * @param uuid the uuid
     * @return the user from uuid
     */
    @Nullable
    public static User getUserFromUUID(UUID uuid) {
        return lp.getUserManager().getUser(uuid);
    }

    /**
     * Add permission.
     *
     * @param uuid       the uuid
     * @param permission the permission
     * @param sender     the sender
     */
    public static void addPermission(UUID uuid, String permission, CommandSender sender) {
        if(!addPermission(uuid, permission)) {
            sender.sendMessage(prefix + "User not found.");
        }
    }

    /**
     * Add permission.
     *
     * @param name       the name
     * @param permission the permission
     * @param sender     the sender
     */
    public static void addPermission(String name, String permission, CommandSender sender) {
        Player p = Bukkit.getPlayer(name);
        if(p == null) {
            sender.sendMessage(prefix + "User not found or offline.");
            return;
        }
        UUID uuid = p.getUniqueId();
        addPermission(uuid, permission);
    }

    /**
     * Add permission boolean.
     *
     * @param uuid       the uuid
     * @param permission the permission
     * @return the boolean
     */
    public static boolean addPermission(UUID uuid, String permission) {
        User user = getUserFromUUID(uuid);
        if(user == null) {
            return false;
        }
        user.data().add(Node.builder(permission).build());

        lp.getUserManager().saveUser(user);
        return true;
    }

    /**
     * Del permission.
     *
     * @param uuid       the uuid
     * @param permission the permission
     * @param sender     the sender
     */
    public static void delPermission(UUID uuid, String permission, CommandSender sender) {
        if(!delPermission(uuid, permission)) {
            sender.sendMessage(prefix + "존재하지 않는 유저입니다.");
        }
    }

    /**
     * Del permission.
     *
     * @param name       the name
     * @param permission the permission
     * @param sender     the sender
     */
    public static void delPermission(String name, String permission, CommandSender sender) {
        Player p = Bukkit.getPlayer(name);
        if(p == null) {
            sender.sendMessage(prefix + "존재하지 않는 유저이거나 오프라인 유저입니다.");
            return;
        }
        UUID uuid = p.getUniqueId();
        delPermission(uuid, permission);
    }

    /**
     * Del permission boolean.
     *
     * @param uuid       the uuid
     * @param permission the permission
     * @return the boolean
     */
    public static boolean delPermission(UUID uuid, String permission) {
        User user = getUserFromUUID(uuid);
        if(user == null) {
            return false;
        }
        user.data().remove(Node.builder(permission).build());

        lp.getUserManager().saveUser(user);
        return true;
    }
}

package com.darksoldier1404.dppc.utils;

import com.darksoldier1404.dppc.DPPCore;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

@SuppressWarnings("all")
public class ItemStackUtils {
    private static final DPPCore plugin = DPPCore.getInstance();

    public static ItemStack getPlayerHead(Player p) {
        ItemStack head = new ItemStack(Material.PLAYER_HEAD, 1, (short) SkullType.PLAYER.ordinal());
        SkullMeta sm = (SkullMeta) head.getItemMeta();
        sm.setOwner(p.getName());
        head.setItemMeta(sm);
        return head;
    }
}

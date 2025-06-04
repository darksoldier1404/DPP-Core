package com.darksoldier1404.dppc.api.placeholder;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.entity.Player;

public class PlaceholderUtils {
    public static String applyPlaceholder(Player p, String context) {
        return PlaceholderAPI.setPlaceholders(p, context);
    }

    public static boolean isRegistered(String identifier) {
        return PlaceholderAPI.isRegistered(identifier);
    }
}

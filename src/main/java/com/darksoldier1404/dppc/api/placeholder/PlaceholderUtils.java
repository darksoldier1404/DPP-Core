package com.darksoldier1404.dppc.api.placeholder;

import com.darksoldier1404.dppc.utils.PluginUtil;
import com.darksoldier1404.dppc.utils.enums.DependPlugin;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.entity.Player;

public class PlaceholderUtils {
    public static String applyPlaceholder(Player p, String context) {
        if (PluginUtil.isDependPluginLoaded(DependPlugin.PlaceholderAPI)) {
            return PlaceholderAPI.setPlaceholders(p, context);
        } else {
            return context;
        }
    }

    public static boolean isRegistered(String identifier) {
        if (!PluginUtil.isDependPluginLoaded(DependPlugin.PlaceholderAPI)) {
            return false;
        }
        return PlaceholderAPI.isRegistered(identifier);
    }
}

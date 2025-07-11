package com.darksoldier1404.dppc.api.placeholder;

import com.darksoldier1404.dppc.utils.PluginUtil;
import com.darksoldier1404.dppc.utils.enums.DependPlugin;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.entity.Player;

/**
 * The type Placeholder utils.
 */
public class PlaceholderUtils {
    /**
     * Apply placeholder string.
     *
     * @param player  the player
     * @param context the context
     * @return the string
     */
    public static String applyPlaceholder(Player player, String context) {
        if (PluginUtil.isDependPluginLoaded(DependPlugin.PlaceholderAPI)) {
            return PlaceholderAPI.setPlaceholders(player, context);
        } else {
            return context;
        }
    }

    /**
     * Is registered boolean.
     *
     * @param identifier the identifier
     * @return the boolean
     */
    public static boolean isRegistered(String identifier) {
        if (!PluginUtil.isDependPluginLoaded(DependPlugin.PlaceholderAPI)) {
            return false;
        }
        return PlaceholderAPI.isRegistered(identifier);
    }
}

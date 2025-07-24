package com.darksoldier1404.dppc.lang;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

public class DLangContainer {
    private static final Set<DLangContext> langContexts = new HashSet<>();

    public Set<DLangContext> getLangContexts() {
        return langContexts;
    }

    public static void initPluginLang(JavaPlugin plugin, YamlConfiguration data) {
        if (data.get("Lang") == null) {
            plugin.getLogger().warning("Language key 'Lang' not found in the configuration file. Please ensure it is set correctly.");
            return;
        }
        DLangContext context = new DLangContext(plugin, Lang.valueOf(data.getString("Lang").toUpperCase()));
        for (String key : data.getKeys(false)) {
            String value = data.getString(key);
            if (value != null) {
                if (context.hasDefaultValue(key)) {
                    plugin.getLogger().warning("Language key '" + key + "' already exists in the context.");
                } else {
                    context.initDefaultValues(key, value);
                }
            } else {
                plugin.getLogger().warning("Language key '" + key + "' has no value in the configuration file. Please ensure it is set correctly.");
            }
        }
        langContexts.add(context);
    }

    @NotNull
    public String get(String key) {
        for (DLangContext context : langContexts) {
            return ChatColor.translateAlternateColorCodes('&', context.getValue(key));
        }
        return "[DLang] Error: Language key not found: " + key;
    }

    @NotNull
    public String getWithArgs(String key, String... args) {
        String s = null;
        for (DLangContext context : langContexts) {
            s = context.getValue(key);
            break;
        }
        if (s != null) {
            for (int i = 0; i < args.length; i++) {
                s = s.replace("{" + i + "}", args[i]);
            }
            return ChatColor.translateAlternateColorCodes('&', s);
        }
        return "[DLang] Error: Language key not found: " + key;
    }
}

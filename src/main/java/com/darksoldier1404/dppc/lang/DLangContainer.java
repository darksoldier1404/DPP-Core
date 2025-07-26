package com.darksoldier1404.dppc.lang;

import com.darksoldier1404.dppc.utils.ConfigUtils;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import static com.sk89q.worldguard.config.WorldConfiguration.log;

public class DLangContainer {
    private static final Set<DLangContext> langContexts = new HashSet<>();
    public static Lang currentLang = Lang.ENGLISH;

    public Set<DLangContext> getLangContexts() {
        return langContexts;
    }

    public static void setCurrentLang(Lang lang) {
        currentLang = lang;
    }

    public static Lang getCurrentLang() {
        return currentLang;
    }

    public static void loadDefaultLangFiles(JavaPlugin plugin) {
        File f = new File(plugin.getDataFolder() + "/lang", "English.yml");
        if (!f.exists()) {
            plugin.saveResource("lang/English.yml", false);
            plugin.saveResource("lang/Korean.yml", false);
        }
        for (YamlConfiguration data : ConfigUtils.loadCustomDataList(plugin, "lang")) {
            try {
                initPluginLang(plugin, data);
            } catch (Exception e) {
                log.warning("[DLang] Error loading lang file: " + data.getName());
            }
        }
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

    public static String find(String key) {
        for (DLangContext context : langContexts) {
            if (context.getLang() == currentLang) {
                if (context.hasValue(key)) {
                    return context.getValue(key);
                }
            }
        }
        return "[DLang] Error: Language key not found: " + key;
    }

    @NotNull
    public String get(String key) {
        return find(key);
    }

    @NotNull
    public String getWithArgs(String key, String... args) {
        String s = find(key);
        for (int i = 0; i < args.length; i++) {
            s = s.replace("{" + i + "}", args[i]);
        }
        return ChatColor.translateAlternateColorCodes('&', s);
    }
}

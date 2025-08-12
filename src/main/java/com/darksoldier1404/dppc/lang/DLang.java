package com.darksoldier1404.dppc.lang;

import com.darksoldier1404.dppc.utils.ConfigUtils;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class DLang {
    private static final Set<DLangContext> langContexts = new HashSet<>();
    public static Locale currentLang = Locale.forLanguageTag("en-US");

    public static Set<DLangContext> getLangContexts() {
        return langContexts;
    }

    public static void setCurrentLang(Locale lang) {
        currentLang = lang;
    }

    public static Locale getCurrentLang() {
        return currentLang;
    }

    public static void initPluginLang(JavaPlugin plugin) {
        File f = new File(plugin.getDataFolder() + "/lang", "en_US.yml");
        if (!f.exists()) {
            plugin.saveResource("lang/en_US.yml", false);
            plugin.saveResource("lang/ko_KR.yml", false);
        }
        ConfigUtils.loadCustomDataMap(plugin, "lang").forEach((name, data) -> {
            try {
                loadDefaultLangFiles(plugin, data, name);
            } catch (Exception e) {
                plugin.getLogger().warning("[DLang] Error loading lang file: " + data.getName());
            }
        });
    }

    public static void loadDefaultLangFiles(JavaPlugin plugin, YamlConfiguration data, String fileName) {
        DLangContext context = new DLangContext(plugin, Locale.forLanguageTag(fileName.split("\\.")[0].replace("_", "-")));
        for (String key : data.getKeys(false)) {
            String value = data.getString(key);
            if (value != null) {
                if (context.hasValue(key)) {
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
            System.out.println(currentLang + " vs " + context.getLang());
            if (context.getLang().getLanguage().equals(currentLang.getLanguage())) {
                if (context.hasValue(key)) {
                    return context.getValue(key);
                }
            }
        }
        return "[DLang] Error: Language key not found: " + key;
    }

    @NotNull
    public static String get(String key) {
        return find(key);
    }

    @NotNull
    public static String getWithArgs(String key, String... args) {
        String s = find(key);
        for (int i = 0; i < args.length; i++) {
            s = s.replace("{" + i + "}", args[i]);
        }
        return ChatColor.translateAlternateColorCodes('&', s);
    }
}

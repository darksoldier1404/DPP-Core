package com.darksoldier1404.dppc.lang;

import com.darksoldier1404.dppc.utils.ConfigUtils;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("all")
public class DLang {
    private final JavaPlugin plugin;
    private YamlConfiguration currentLang;
    private Map<String, YamlConfiguration> langFiles = new HashMap<>();

    public DLang(@NotNull String langKey, JavaPlugin plugin) {
        this.plugin = plugin;
        loadDefaultLangFiles();
        try {
            currentLang = langFiles.get(langKey);
        } catch (Exception e) {
            System.out.println("[DLang] Error: Language file not found!");
        }
    }

    public void setLangFile(YamlConfiguration lang) {
        currentLang = lang;
    }

    public YamlConfiguration getCurrentLang() {
        return currentLang;
    }

    public void setCurrentLang(YamlConfiguration currentLang) {
        this.currentLang = currentLang;
    }

    public Map<String, YamlConfiguration> getLangFiles() {
        return langFiles;
    }

    public void setLangFiles(Map<String, YamlConfiguration> langFiles) {
        this.langFiles = langFiles;
    }

    public void setLang(String lang) {
        try {
            currentLang = langFiles.get(lang);
        } catch (Exception e) {
            System.out.println("[DLang] Error: Language file not found!");
            System.out.println("[DLang] input: " + lang);
        }
    }

    @Nullable
    public String get(String key) {
        String s = currentLang.getString(key);
        if(s == null) {
            return null;
        }
        return ChatColor.translateAlternateColorCodes('&', s);
    }

    @Nullable
    public String getWithArgs(String key, String... args) {
        String s = currentLang.getString(key);
        if (s != null) {
            for (int i = 0; i < args.length; i++) {
                s = s.replace("{" + i + "}", args[i]);
            }
            return ChatColor.translateAlternateColorCodes('&', s);
        }
        return null;
    }

    public void loadDefaultLangFiles() {
        File f = new File(plugin.getDataFolder() + "/lang", "English.yml");
        if (!f.exists()) {
            plugin.saveResource("lang/English.yml", false);
        }
        for (YamlConfiguration data : ConfigUtils.loadCustomDataList(plugin, "lang")) {
            try {
                langFiles.put(data.getString("Lang"), data);
            } catch (Exception e) {
                System.out.println("[DLang] Error loading lang file: " + data.getName());
            }
        }
    }
}

/*
 * Special thanks to: tr7zw, SoSeDiK, Broken arrow
 * From TR's Mod Workshop
 */
package com.darksoldier1404.dppc.data;

import com.darksoldier1404.dppc.lang.DLang;
import com.darksoldier1404.dppc.utils.ColorUtils;
import com.darksoldier1404.dppc.utils.ConfigUtils;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class DPlugin extends JavaPlugin {
    private YamlConfiguration config;
    private String prefix;
    private final Map<String, DataContainer<?, ?>> data = new HashMap<>();
    private final boolean useDLang;

    public DPlugin() {
        this(false);
    }

    public DPlugin(boolean useDLang) {
        this.useDLang = useDLang;
    }

    public void init() {
        this.config = ConfigUtils.loadDefaultPluginConfig(this);
        this.prefix = ColorUtils.applyColor(config.getString("Settings.prefix"));
        if (this.useDLang) {
            if (config.getString("Settings.Lang") == null) {
                config.set("Settings.Lang", "en_US");
            }
            DLang.initPluginLang(this);
            DLang.setCurrentLang(Locale.forLanguageTag(config.getString("Settings.Lang").replace("_", "-")));
        }
    }

    @Override
    public @NotNull YamlConfiguration getConfig() {
        return config;
    }

    public void setConfig(YamlConfiguration config) {
        this.config = config;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public void set(String key, DataContainer value) {
        data.put(key, value);
    }

    public DataContainer get(String key) {
        return data.get(key);
    }

    public void reload() {
        config = ConfigUtils.reloadPluginConfig(this, config);
        prefix = ColorUtils.applyColor(config.getString("Settings.prefix"));
        if (useDLang) {
            if (config.getString("Settings.Lang") == null) {
                config.set("Settings.Lang", "en_US");
            }
            DLang.initPluginLang(this);
            DLang.setCurrentLang(Locale.forLanguageTag(config.getString("Settings.Lang").replace("_", "-")));
        }
    }

    public void saveDataContainer() {
        ConfigUtils.savePluginConfig(this, config);
        for (Map.Entry<String, DataContainer<?, ?>> entry : data.entrySet()) {
            DataContainer<?, ?> data = entry.getValue();
            data.save();
        }
    }

    public DataContainer loadDataContainer(DataContainer container, Class<?> clazz) {
        try {
            data.put(container.getPath(), container);
            return container.load(clazz);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}

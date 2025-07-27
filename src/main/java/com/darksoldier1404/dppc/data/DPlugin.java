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
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DPlugin extends JavaPlugin {
    private YamlConfiguration config;
    private DLang lang;
    private String prefix;
    private final Map<String, DataContainer<?, ?>> data = new HashMap<>();
    private boolean useDLang = false;

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
            this.lang = new DLang(config.getString("Settings.Lang") == null ? "English" : config.getString("Settings.Lang"), this);
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

    public DLang getLang() {
        return lang;
    }

    public void setLang(DLang lang) {
        this.lang = lang;
    }

    public void initUserData(UUID uuid) {
//        if (!hasUserData(uuid)) {
//            YamlConfiguration data = ConfigUtils.loadCustomData(plugin, String.valueOf(uuid), "udata");
//            addUserData(uuid, data);
//        }
    }

//    public void addUserData(UUID uuid, YamlConfiguration data) {
//        if (this.data.containsKey("udata")) {
//            Map<UUID, YamlConfiguration> udata = (Map<UUID, YamlConfiguration>) this.data.get("udata");
//            udata.put(uuid, data);
//        } else {
//            Map<UUID, YamlConfiguration> udata = new HashMap<>();
//            udata.put(uuid, data);
//            this.data.put("udata", udata);
//        }
//    }

    public void removeUserData(UUID uuid) {
        if (this.data.containsKey("udata")) {
            Map<UUID, YamlConfiguration> udata = (Map<UUID, YamlConfiguration>) this.data.get("udata");
            udata.remove(uuid);
        }
    }

    @Nullable
    public YamlConfiguration getUserData(UUID uuid) {
        if (this.data.containsKey("udata")) {
            Map<UUID, YamlConfiguration> udata = (Map<UUID, YamlConfiguration>) this.data.get("udata");
            return udata.get(uuid);
        }
        return null;
    }

    public boolean hasUserData(UUID uuid) {
        if (this.data.containsKey("udata")) {
            Map<UUID, YamlConfiguration> udata = (Map<UUID, YamlConfiguration>) this.data.get("udata");
            return udata.containsKey(uuid);
        }
        return false;
    }

    public void clearUserData() {
        if (this.data.containsKey("udata")) {
            Map<UUID, YamlConfiguration> udata = (Map<UUID, YamlConfiguration>) this.data.get("udata");
            udata.clear();
        }
    }

    public void saveUserData(UUID uuid) {
        if (this.data.containsKey("udata")) {
            Map<UUID, YamlConfiguration> udata = (Map<UUID, YamlConfiguration>) this.data.get("udata");
            ConfigUtils.saveCustomData(this, udata.get(uuid), String.valueOf(uuid), "udata");
        }
    }

    public void saveAndLeave(UUID uuid) {
        if (this.data.containsKey("udata")) {
            Map<UUID, YamlConfiguration> udata = (Map<UUID, YamlConfiguration>) this.data.get("udata");
            ConfigUtils.saveCustomData(this, udata.get(uuid), String.valueOf(uuid), "udata");
            udata.remove(uuid);
        }
    }

    public void saveAllUserData() {
        if (this.data.containsKey("udata")) {
            Map<UUID, YamlConfiguration> udata = (Map<UUID, YamlConfiguration>) this.data.get("udata");
            for (Map.Entry<UUID, YamlConfiguration> entry : udata.entrySet()) {
                ConfigUtils.saveCustomData(this, entry.getValue(), String.valueOf(entry.getKey()), "udata");
            }
        }
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
            lang = new DLang(config.getString("Settings.Lang") == null ? "English" : config.getString("Settings.Lang"), this);
            if (config.getString("Settings.Lang") == null) {
                config.set("Settings.Lang", "English");
            }
        }
    }

    public void save() {
        ConfigUtils.savePluginConfig(this, config);
        saveAllUserData();
        for (Map.Entry<String, DataContainer<?, ?>> entry : data.entrySet()) {
            DataContainer<?, ?> cargo = entry.getValue();
            cargo.save();
        }
    }

    public void load(DataContainer container, Class<?> clazz) {
        try {
            container.load(clazz);
            data.put(container.getPath(), container);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

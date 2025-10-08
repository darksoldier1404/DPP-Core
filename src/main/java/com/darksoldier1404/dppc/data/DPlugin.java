/*
 * Special thanks to: tr7zw, SoSeDiK, Broken arrow
 * From TR's Mod Workshop
 */
package com.darksoldier1404.dppc.data;

import com.darksoldier1404.dppc.annotation.DPPCoreVersion;
import com.darksoldier1404.dppc.lang.DLang;
import com.darksoldier1404.dppc.utils.ColorUtils;
import com.darksoldier1404.dppc.utils.ConfigUtils;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@DPPCoreVersion(since = "5.3.0")
public class DPlugin extends JavaPlugin {
    public YamlConfiguration config;
    public String prefix;
    private final Map<String, IDataHandler<?, ?>> data = new HashMap<>();
    private final boolean useDLang;
    private DLang lang;

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
            lang = new DLang();
            if (this.config.getString("Settings.Lang") == null) {
                this.config.set("Settings.Lang", "en_US");
            }
            lang.initPluginLang(this);
            lang.setCurrentLang(Locale.forLanguageTag(this.config.getString("Settings.Lang").replace("_", "-")));
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

    public void set(String key, IDataHandler<?, ?> value) {
        data.put(key, value);
    }

    @SuppressWarnings("unchecked")
    public <T extends IDataHandler<?, ?>> T get(String key) {
        return (T) data.get(key);
    }

    public void reload() {
        init();
    }

    @Override
    public void saveConfig() {
        ConfigUtils.savePluginConfig(this, config);
    }

    public void saveDataContainer() {
        ConfigUtils.savePluginConfig(this, config);
        for (Map.Entry<String, IDataHandler<?, ?>> entry : data.entrySet()) {
            IDataHandler<?, ?> handler = entry.getValue();
            handler.saveAll();
        }
    }

    public void saveDataContainerWithoutConfig() {
        for (Map.Entry<String, IDataHandler<?, ?>> entry : data.entrySet()) {
            IDataHandler<?, ?> handler = entry.getValue();
            handler.saveAll();
        }
    }

    @Nullable
    public <K, V, T extends IDataHandler<K, V>> T loadDataContainer(T container) {
        return loadDataContainer(container, null);
    }

    @Nullable
    @SuppressWarnings("unchecked")
    public <K, V, T extends IDataHandler<K, V>> T loadDataContainer(T container, Class<?> clazz) {
        try {
            data.put(container.getPath(), container);
            return (T) container.loadAll(clazz);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean isUseDLang() {
        return useDLang;
    }

    public DLang getLang() {
        return this.lang;
    }
}
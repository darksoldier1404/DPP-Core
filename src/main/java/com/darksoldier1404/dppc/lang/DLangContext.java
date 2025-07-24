package com.darksoldier1404.dppc.lang;

import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DLangContext {
    private final JavaPlugin plugin;
    private final Lang lang;
    private final Map<String, String> defaultValueMap = new ConcurrentHashMap<>();
    private final Map<String, String> valueMap = new ConcurrentHashMap<>();

    public DLangContext(JavaPlugin plugin, Lang lang) {
        this.plugin = plugin;
        this.lang = lang;
    }

    public JavaPlugin getPlugin() {
        return plugin;
    }

    public Lang getLang() {
        return lang;
    }

    public Map<String, String> getDefaultValueMap() {
        return defaultValueMap;
    }

    public Map<String, String> getValueMap() {
        return valueMap;
    }

    public void initDefaultValues(String key, String value) {
        defaultValueMap.put(key, value);
        valueMap.put(key, value);
    }

    public void setDefaultValue(String key, String value) {
        defaultValueMap.put(key, value);
    }

    public void removeDefaultValue(Lang lang) {
        defaultValueMap.remove(lang);
    }

    public void setValue(String key, String value) {
        this.valueMap.put(key, value);
    }

    public void removeValue(String key) {
        valueMap.remove(key);
    }

    @NotNull
    public String getValue(String key) {
        return valueMap.getOrDefault(key, defaultValueMap.get(key));
    }

    @NotNull
    public String getDefaultValue(String key) {
        return defaultValueMap.get(key);
    }

    public boolean hasValue(String key) {
        return valueMap.containsKey(key);
    }

    public boolean hasDefaultValue(String key) {
        return defaultValueMap.containsKey(key);
    }

    public void clear() {
        defaultValueMap.clear();
        valueMap.clear();
    }
}

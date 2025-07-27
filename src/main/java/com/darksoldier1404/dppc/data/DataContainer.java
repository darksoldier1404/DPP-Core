package com.darksoldier1404.dppc.data;

import com.darksoldier1404.dppc.utils.ConfigUtils;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DataContainer<K, V> extends HashMap<K, V> {
    private final JavaPlugin plugin;
    private final DataType dataType;
    private String path = null;

    public DataContainer(JavaPlugin plugin, DataType dataType) {
        super();
        this.plugin = plugin;
        this.dataType = dataType;
    }

    public DataContainer(JavaPlugin plugin, DataType dataType, String path) {
        super();
        this.plugin = plugin;
        this.dataType = dataType;
        this.path = path;
    }

    public JavaPlugin getPlugin() {
        return plugin;
    }

    public DataType getDataType() {
        return dataType;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void save() {
        switch (dataType) {
            case USER:
                for (Map.Entry<K, V> entry : entrySet()) {
                    UUID uuid = (UUID) entry.getKey();
                    YamlConfiguration userData = (YamlConfiguration) entry.getValue();
                    ConfigUtils.saveCustomData(plugin, userData, uuid.toString(), "udata");
                }
                break;
            case CUSTOM:
                for (Map.Entry<K, V> entry : entrySet()) {
                    String fileName = (String) entry.getKey();
                    DataCargo dataCargo = (DataCargo) entry.getValue();
                    Object serializedData = dataCargo.serialize();
                    if (serializedData instanceof YamlConfiguration) {
                        ConfigUtils.saveCustomData(plugin, (YamlConfiguration) serializedData, fileName, path != null ? path : "data");
                    } else {
                        System.err.println("Failed to save data for " + fileName + ": serialized data is not a YamlConfiguration.");
                    }
                }
                break;
        }
    }

    public void load(Class<?> clazz) {
        switch (dataType) {
            case USER:
                for (K key : keySet()) {
                    UUID uuid = (UUID) key;
                    YamlConfiguration userData = ConfigUtils.loadCustomData(plugin, uuid.toString(), "udata");
                    if (userData != null) {
                        put(key, (V) userData);
                    }
                }
                break;
            case CUSTOM:
                HashMap<String, YamlConfiguration> dataMap = ConfigUtils.loadCustomDataMap(plugin, path != null ? path : "data");
                for (Map.Entry<String, YamlConfiguration> entry : dataMap.entrySet()) {
                    String fileName = entry.getKey();
                    YamlConfiguration data = entry.getValue();
                    if (data != null) {
                        try {
                            DataCargo dataCargo = (DataCargo) clazz.getDeclaredConstructor().newInstance();
                            Object key = fileName.split("\\.")[0];
                            Object value = dataCargo.deserialize(data);
                            if (clazz.isInstance(value) && key instanceof String) {
                                put((K) key, (V) value);
                            } else {
                                System.err.println("Type mismatch: Key or value is not compatible with the expected types.");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            System.err.println("Failed to load data for " + fileName + " in " + clazz.getSimpleName());
                        }
                    }
                }
                break;
        }
    }
}

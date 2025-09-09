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
                    if (entry.getKey() instanceof UUID) {
                        UUID uuid = (UUID) entry.getKey();
                        YamlConfiguration userData = (YamlConfiguration) entry.getValue();
                        ConfigUtils.saveCustomData(plugin, userData, uuid.toString(), path != null ? path : "udata");
                    } else {
                        System.err.println("Invalid key type: Expected UUID but found " + entry.getKey().getClass().getSimpleName());
                    }
                }
                break;
            case YAML:
                for (Map.Entry<K, V> entry : entrySet()) {
                    String fileName = (String) entry.getKey();
                    YamlConfiguration yamlData = (YamlConfiguration) entry.getValue();
                    ConfigUtils.saveCustomData(plugin, yamlData, fileName, path != null ? path : "data");
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

    public DataContainer load(Class<?> clazz) {
        switch (dataType) {
            case USER: {
                HashMap<String, YamlConfiguration> userData = ConfigUtils.loadCustomDataMap(plugin, path != null ? path : "udata");
                for (Map.Entry<String, YamlConfiguration> entry : userData.entrySet()) {
                    YamlConfiguration yamlData = entry.getValue();
                    if (yamlData != null) {
                        put((K) entry.getKey(), (V) yamlData);
                    }
                }
                break;
            }
            case YAML: {
                HashMap<String, YamlConfiguration> yamlDataMap = ConfigUtils.loadCustomDataMap(plugin, path != null ? path : "data");
                for (Map.Entry<String, YamlConfiguration> entry : yamlDataMap.entrySet()) {
                    YamlConfiguration yamlData = entry.getValue();
                    if (yamlData != null) {
                        put((K) entry.getKey(), (V) yamlData);
                    }
                }
                break;
            }
            case CUSTOM: {
                if (!DataCargo.class.isAssignableFrom(clazz)) {
                    System.err.println("Class " + clazz.getSimpleName() + " does not implement DataCargo.");
                    break;
                }
                HashMap<String, YamlConfiguration> dataMap = ConfigUtils.loadCustomDataMap(plugin, path != null ? path : "data");
                for (Map.Entry<String, YamlConfiguration> entry : dataMap.entrySet()) {
                    YamlConfiguration data = entry.getValue();
                    if (data != null) {
                        try {
                            DataCargo dataCargo = (DataCargo) clazz.getDeclaredConstructor().newInstance();
                            String key = entry.getKey().split("\\.")[0];
                            Object value = dataCargo.deserialize(data);
                            if (clazz.isInstance(value)) {
                                put((K) key, (V) value);
                            } else {
                                System.err.println("Type mismatch: Value is not compatible with the expected type.");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            System.err.println("Failed to load data for " + entry.getKey() + " in " + clazz.getSimpleName());
                        }
                    }
                }
                break;
            }
        }
        return this;
    }
}

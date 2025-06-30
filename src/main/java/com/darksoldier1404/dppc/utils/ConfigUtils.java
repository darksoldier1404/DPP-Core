package com.darksoldier1404.dppc.utils;

import com.darksoldier1404.dppc.DPPCore;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class ConfigUtils {
    private static final DPPCore core = DPPCore.getInstance();
    private static final Logger log = core.log;

    @NotNull
    public static YamlConfiguration loadDefaultPluginConfig(@NotNull JavaPlugin plugin) {
        File config = new File(plugin.getDataFolder(), "config.yml");
        if (!config.exists()) {
            plugin.saveResource("config.yml", false);
            log.info(plugin.getName() + " creating config file.");
        }
        log.info(plugin.getName() + " config file loaded.");
        return YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "config.yml"));
    }

    public static void savePluginConfig(@NotNull JavaPlugin plugin, @NotNull YamlConfiguration config) {
        try {
            config.save(new File(plugin.getDataFolder(), "config.yml"));
            log.info(plugin.getName() + " config file saved.");
        } catch (Exception e) {
            log.warning(plugin.getName() + " config file save failed.");
            e.printStackTrace();
        }
    }

    @Nullable
    public static YamlConfiguration reloadPluginConfig(@NotNull JavaPlugin plugin, @NotNull YamlConfiguration config) {
        try {
            return YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "config.yml"));
        } catch (Exception e) {
            log.warning(plugin.getName() + " config file reload failed, file dose not exist.");
            e.printStackTrace();
        }
        return null;
    }

    private static File getCustomFile(JavaPlugin plugin, String fileName, String path) {
        File dir = (path == null || path.isEmpty()) ? plugin.getDataFolder() : new File(plugin.getDataFolder(), path);
        if (!dir.exists()) dir.mkdirs();
        return new File(dir, fileName + ".yml");
    }

    public static void saveCustomData(@NotNull JavaPlugin plugin, @NotNull YamlConfiguration config, @NotNull String fileName, @NotNull String path) {
        File file = getCustomFile(plugin, fileName, path);
        try {
            config.save(file);
            log.info(plugin.getName() + " " + fileName + " file saved. Path: " + file.getPath());
        } catch (IOException e) {
            log.warning(plugin.getName() + " " + fileName + " file save failed. " + e.getMessage());
        }
    }

    public static void saveCustomData(@NotNull JavaPlugin plugin, @NotNull YamlConfiguration config, @NotNull String fileName) {
        saveCustomData(plugin, config, fileName, null);
    }

    @Nullable
    public static YamlConfiguration loadCustomData(@NotNull JavaPlugin plugin, @NotNull String fileName, @NotNull String path) {
        File file = getCustomFile(plugin, fileName, path);
        if (!file.exists()) {
            log.warning(plugin.getName() + " " + fileName + " file does not exist. Path: " + file.getPath());
            return null;
        }
        try {
            YamlConfiguration data = YamlConfiguration.loadConfiguration(file);
            log.info(plugin.getName() + " " + fileName + " file loaded. Path: " + file.getPath());
            return data;
        } catch (Exception e) {
            log.warning(plugin.getName() + " " + fileName + " file load failed. " + e.getMessage());
        }
        return null;
    }

    public static List<YamlConfiguration> loadCustomDataList(@NotNull JavaPlugin plugin, @NotNull String path) {
        List<YamlConfiguration> dataList = new ArrayList<>();
        File folder = new File(plugin.getDataFolder() + "/" + path);
        File[] files = folder.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    try {
                        YamlConfiguration data = YamlConfiguration.loadConfiguration(file);
                        log.info(plugin.getName() + " " + file.getName() + " file loaded.");
                        dataList.add(data);
                    } catch (Exception e) {
                        log.warning(plugin.getName() + " " + file.getName() + " file load failed.");
                        e.printStackTrace();
                    }
                }
            }
        }
        return dataList;
    }

    @Nullable
    public static YamlConfiguration createCustomData(@NotNull JavaPlugin plugin, @NotNull String fileName, @NotNull String path) {
        try {
            File file = new File(plugin.getDataFolder() + "/" + path, fileName + ".yml");
            if (!file.exists()) {
                file.createNewFile();
                log.info(plugin.getName() + " " + fileName + " file created.");
                return YamlConfiguration.loadConfiguration(file);
            }
            log.info(plugin.getName() + " " + fileName + " load exist file.");
            return YamlConfiguration.loadConfiguration(file);
        } catch (Exception e) {
            log.warning(plugin.getName() + " " + fileName + " file create failed.");
            e.printStackTrace();
            return null;
        }
    }

    @Nullable
    public static YamlConfiguration createCustomData(@NotNull JavaPlugin plugin, @NotNull String fileName) {
        try {
            File file = new File(plugin.getDataFolder(), fileName + ".yml");
            if (!file.exists()) {
                file.createNewFile();
                log.info(plugin.getName() + " " + fileName + " file created.");
                return YamlConfiguration.loadConfiguration(file);
            }
            log.info(plugin.getName() + " " + fileName + " load exist file.");
            return YamlConfiguration.loadConfiguration(file);
        } catch (Exception e) {
            log.warning(plugin.getName() + " " + fileName + " file create failed.");
            e.printStackTrace();
            return null;
        }
    }

    @NotNull
    public static YamlConfiguration initUserData(@NotNull JavaPlugin plugin, @NotNull String fileName, @NotNull String path) {
        File file = new File(plugin.getDataFolder() + "/" + path, fileName + ".yml");
        if (!file.exists()) {
            try {
                file.createNewFile();
                log.info(plugin.getName() + " " + fileName + " file created.");
                return YamlConfiguration.loadConfiguration(file);
            } catch (IOException e) {
                log.warning(plugin.getName() + " " + fileName + " file create failed.");
            }
        } else {
            return YamlConfiguration.loadConfiguration(file);
        }
        log.warning(plugin.getName() + " " + fileName + " file create failed.");
        log.warning(plugin.getName() + " return empty file.");
        return new YamlConfiguration();
    }

    @NotNull
    public static YamlConfiguration initUserData(@NotNull JavaPlugin plugin, @NotNull String fileName) {
        File file = new File(plugin.getDataFolder() + "/data", fileName + ".yml");
        if (!file.exists()) {
            try {
                file.createNewFile();
                log.info(plugin.getName() + " " + fileName + " file created.");
                return YamlConfiguration.loadConfiguration(file);
            } catch (IOException e) {
                log.warning(plugin.getName() + " " + fileName + " file create failed.");
            }
        } else {
            return YamlConfiguration.loadConfiguration(file);
        }
        log.warning(plugin.getName() + " " + fileName + " file create failed.");
        log.warning(plugin.getName() + " return empty file.");
        return new YamlConfiguration();
    }
}

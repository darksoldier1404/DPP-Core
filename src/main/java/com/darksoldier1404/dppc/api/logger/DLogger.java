package com.darksoldier1404.dppc.api.logger;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

public class DLogger {
    private final ConcurrentHashMap<String, YamlConfiguration> logMap = new ConcurrentHashMap<>();
    private YamlConfiguration mainLog = new YamlConfiguration();
    private final JavaPlugin plugin;
    private final Logger log;
    private BukkitTask task;
    private boolean useConsoleLog = false;

    public DLogger(JavaPlugin plugin) {
        this.plugin = plugin;
        log = this.plugin.getLogger();
        log.info("DLogger activated");
    }

    public DLogger(JavaPlugin plugin, boolean useConsoleLog) {
        this.plugin = plugin;
        this.useConsoleLog = useConsoleLog;
        log = this.plugin.getLogger();
        log.info("DLogger activated - Console logging enabled: " + useConsoleLog);
    }

    public boolean addLog(String name) {
        if (logMap.containsKey(name)) {
            log.warning("A log with the name " + name + " already exists.");
            return false;
        } else {
            logMap.put(name, new YamlConfiguration());
            log.info("Added " + name + " log.");
            return true;
        }
    }

    @Nullable
    public YamlConfiguration getLog(String name) {
        YamlConfiguration logData = logMap.get(name);
        if (logData == null) {
            log.warning(name + " log does not exist.");
        } else {
            log.info(name + " log retrieved.");
        }
        return logData;
    }

    public boolean removeLog(String name) {
        if (logMap.containsKey(name)) {
            logMap.remove(name);
            log.info(name + " log removed.");
            return true;
        } else {
            log.warning(name + " log does not exist.");
            return false;
        }
    }

    public boolean log(String name, String key, Object value) {
        YamlConfiguration data = logMap.get(name);
        if (data == null) {
            log.warning(name + " log does not exist.");
            return false;
        }

        if (data.contains(key)) {
            log.warning(name + " log already contains the key " + key);
            return false;
        } else {
            data.set(key, value);
            log.info("Added key " + key + " with value " + value + " to " + name + " log.");
            return true;
        }
    }

    public boolean log(String key, Object value) {
        if (mainLog == null) {
            log.warning("Main log does not exist.");
            return false;
        }

        if (mainLog.contains(key)) {
            log.warning("Main log already contains the key " + key);
            return false;
        } else {
            mainLog.set(key, value);
            log.info("Added key " + key + " with value " + value + " to the main log.");
            return true;
        }
    }

    public boolean log(Object value) {
        if (mainLog == null) {
            log.warning("Main log does not exist.");
            return false;
        }

        String nanoTimeStr = String.valueOf(System.nanoTime());
        String key = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date()) + "-" + nanoTimeStr.substring(nanoTimeStr.length() - 6);
        if (mainLog.contains(key)) {
            log.warning("Main log already contains the key " + key);
            return false;
        }
        mainLog.set(key, value);
        log.info("Added value to the main log with key " + key);
        return true;
    }

    public void initAutoSave(long delay, long period, String logMapPath, String logMapName, String mainLogPath, String mainLogName, boolean withReset) {
        if (task != null) {
            log.warning("Automatic saving is already enabled.");
            return;
        }
        task = new BukkitRunnable() {
            @Override
            public void run() {
                log.info("Starting automatic save of DLogger.");
                saveLogs(logMapPath, logMapName, withReset);
                saveMainLog(mainLogPath, mainLogName, withReset);
                log.info("Automatic save of DLogger completed.");
            }
        }.runTaskTimer(plugin, delay, period);
        log.info("Automatic save has been activated.");
    }

    private void saveLogs(String logMapPath, String logMapName, boolean withReset) {
        if (!logMap.isEmpty()) {
            for (String name : logMap.keySet()) {
                YamlConfiguration data = logMap.get(name);
                try {
                    if (!data.getKeys(false).isEmpty()) {
                        String nanoTimeStr = String.valueOf(System.nanoTime());
                        String key = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date()) + "-" + nanoTimeStr.substring(nanoTimeStr.length() - 6);
                        data.save(new File(logMapPath, logMapName + "-" + key + ".yml"));
                    }
                    if (withReset) {
                        logMap.put(name, new YamlConfiguration());
                    }
                    log.info("Saved " + name + " log to " + logMapPath + "/" + logMapName + ".yml.");
                } catch (IOException e) {
                    log.warning("Failed to save " + name + " log.");
                    e.printStackTrace();
                }
            }
        }
    }

    private void saveMainLog(String mainLogPath, String mainLogName, boolean withReset) {
        try {
            if (!mainLog.getKeys(false).isEmpty()) {
                String nanoTimeStr = String.valueOf(System.nanoTime());
                String key = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date()) + "-" + nanoTimeStr.substring(nanoTimeStr.length() - 6);
                mainLog.save(new File(mainLogPath, mainLogName + "-" + key + ".yml"));
            }
            if (withReset) {
                mainLog = new YamlConfiguration();
            }
            log.info("Saved main log to " + mainLogPath + "/" + mainLogName + ".yml.");
        } catch (IOException e) {
            log.warning("Failed to save the main log.");
            e.printStackTrace();
        }
    }

    public void cancelAutoSaveTask() {
        if (task != null) {
            task.cancel();
            task = null;
            log.info("Automatic save has been deactivated.");
        } else {
            log.warning("Automatic save is already deactivated.");
        }
    }
}

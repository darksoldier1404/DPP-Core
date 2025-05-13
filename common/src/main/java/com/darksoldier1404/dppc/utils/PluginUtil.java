package com.darksoldier1404.dppc.utils;

import com.darksoldier1404.dppc.DPPCore;
import com.earth2me.essentials.Essentials;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sk89q.worldguard.WorldGuard;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import static org.bukkit.Bukkit.getServer;

@SuppressWarnings("all")
public class PluginUtil {
    private static final DPPCore plugin = DPPCore.getInstance();
    private static final Map<JavaPlugin, Integer> loadedPlugins = new HashMap<>();

    public static void addPlugin(JavaPlugin plugin, int id) {
        loadedPlugins.put(plugin, id);
    }

    public static Map<JavaPlugin, Integer> getLoadedPlugins() {
        return loadedPlugins;
    }

    public static void loadALLPlugins() {
        for (JavaPlugin pl : loadedPlugins.keySet()) {
            if (pl != null) {
                if (isMetricsEnabled(pl.getName())) {
                    new Metrics((JavaPlugin) pl, loadedPlugins.get(pl));
                    DPPCore.getInstance().log.info(pl.getName() + " plugin metrics enabled.");
                }
            }
        }
    }

    public static boolean isMetricsEnabled(String name) {
        return DPPCore.getInstance().config.getStringList("Settings.metrics-excluded") == null ? true : !DPPCore.getInstance().config.getStringList("Settings.metrics-excluded").contains(name);
    }

    public static void initializeSoftDependPlugins() {
        DPPCore.ess = getPluginInstance("Essentials", Essentials.class, "MoneyAPI");
        DPPCore.lp = getLuckPermsInstance();
        DPPCore.wg = getWorldGuardInstance();
    }

    public static <T> T getPluginInstance(String pluginName, Class<T> pluginClass, String apiName) {
        Plugin instance = getServer().getPluginManager().getPlugin(pluginName);
        if (instance == null) {
            plugin.getLogger().warning(pluginName + " plugin is not installed.");
            plugin.getLogger().warning(apiName + " is disabled.");
            return null;
        }
        if (!pluginClass.isInstance(instance)) {
            plugin.getLogger().warning(pluginName + " plugin does not match the required class.");
            return null;
        }
        return pluginClass.cast(instance);
    }

    public static LuckPerms getLuckPermsInstance() {
        Plugin lpPlugin = getServer().getPluginManager().getPlugin("LuckPerms");
        if (lpPlugin == null) {
            plugin.getLogger().warning("LuckPerms plugin is not installed.");
            plugin.getLogger().warning("PermissionAPI is disabled.");
            return null;
        }
        return LuckPermsProvider.get();
    }

    public static WorldGuard getWorldGuardInstance() {
        Plugin wgPlugin = getServer().getPluginManager().getPlugin("WorldGuard");
        if (wgPlugin == null) {
            plugin.getLogger().warning("WorldGuard plugin is not installed.");
            plugin.getLogger().warning("WorldGuardAPI is disabled.");
            return null;
        }
        return WorldGuard.getInstance();
    }


    private static final String API_URL = "https://raw.githubusercontent.com/darksoldier1404/DPP-Releases/main/releases.json";

    @NotNull
    public static String getLatestVersion(String pluginName) {
        try {
            URL url = new URL(API_URL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("accept", "application/json");

            int responseCode = connection.getResponseCode();
            if (responseCode != 200) {
                System.out.println("Warn : Unable to get version data. HTTP Response Code: " + responseCode);
                System.out.println("Name : " + pluginName);
                return null;
            }

            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
            StringBuilder response = new StringBuilder();
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            JsonArray array = JsonParser.parseString(response.toString()).getAsJsonArray();
            for (JsonElement element : array) {
                JsonObject obj = element.getAsJsonObject();
                String repo = obj.get("repo").getAsString();
                if (repo.equalsIgnoreCase("darksoldier1404/" + pluginName)) {
                    return obj.get("tag").getAsString();
                }
            }
            return "0.0.0.0";
        } catch (Exception e) {
            e.printStackTrace();
            return "0.0.0.0";
        }
    }

    private static boolean isNewVersion(String currentVersion, String latestVersion) {
        String[] currentParts = currentVersion.split("\\.");
        String[] latestParts = latestVersion.split("\\.");

        for (int i = 0; i < Math.min(currentParts.length, latestParts.length); i++) {
            int currentPart = Integer.parseInt(currentParts[i]);
            int latestPart = Integer.parseInt(latestParts[i]);

            if (latestPart > currentPart) {
                return true;
            } else if (latestPart < currentPart) {
                return false;
            }
        }

        return false;
    }

    public static void updateCheck() {
        updateCheck(getServer().getConsoleSender());
    }

    public static void updateCheck(CommandSender sender) {
        Bukkit.getScheduler().runTask(plugin, () -> {
            for (JavaPlugin plugin : loadedPlugins.keySet()) {
                String latestVersion = getLatestVersion(plugin.getName());
                if (latestVersion != null) {
                    String currentVersion = plugin.getDescription().getVersion();
                    if (isNewVersion(currentVersion, latestVersion)) {
                        sender.sendMessage("§f[ §bDPP-Core §f] §e" + plugin.getName() + " §f| §cA new version of " + plugin.getName() + " is available: " + latestVersion + ". You are running version " + currentVersion);
                        sender.sendMessage("§fDownload: §ehttps://dpnw.site/");
                    } else {
                        sender.sendMessage("§f[ §bDPP-Core §f] §e" + plugin.getName() + " §f| §aYou are running the latest version §f(§a" + currentVersion + "§f)");
                    }
                }
            }
        });
    }

    public static void updateCheck(CommandSender sender, String name) {
        Bukkit.getScheduler().runTask(plugin, () -> {
            if (loadedPlugins.keySet().stream().noneMatch(plugin -> plugin.getName().equalsIgnoreCase(name))) {
                sender.sendMessage("Plugin " + name + " not found.");
                return;
            }
            String latestVersion = getLatestVersion(name);
            if (latestVersion != null) {
                JavaPlugin plugin = loadedPlugins.keySet().stream().filter(p -> p.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
                String currentVersion = plugin.getDescription().getVersion();
                if (isNewVersion(currentVersion, latestVersion)) {
                    sender.sendMessage("§f[ §bDPP-Core §f] §e" + name + " §f| §cA new version of " + name + " is available: " + latestVersion + ". You are running version " + currentVersion);
                    sender.sendMessage("§fDownload: §ehttps://dpnw.site/");
                } else {
                    sender.sendMessage("§f[ §bDPP-Core §f] §e" + name + " §f| §aYou are running the latest version §f(§a" + currentVersion + "§f)");
                }
            }
        });
    }
}

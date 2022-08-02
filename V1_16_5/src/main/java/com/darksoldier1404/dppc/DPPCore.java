package com.darksoldier1404.dppc;

import com.darksoldier1404.dppc.api.placeholder.DPHManager;
import com.darksoldier1404.dppc.api.twitch.TwitchAPI;
import com.darksoldier1404.dppc.commands.DUCCommand;
import com.darksoldier1404.dppc.enums.PluginName;
import com.darksoldier1404.dppc.utils.ConfigUtils;
import com.darksoldier1404.dppc.utils.PluginUtil;
import com.darksoldier1404.dppc.utils.SchedulerUtils;
import com.earth2me.essentials.Essentials;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

@SuppressWarnings("all")
public class DPPCore extends JavaPlugin {
    private static DPPCore plugin;
    public YamlConfiguration config;
    public Logger log;
    public final String prefix = "§f[ §eDPPC §f] ";
    public final Map<PluginName, JavaPlugin> enabledPlugins = new HashMap<>();
    public DPHManager dphm;
    public Essentials ess;
    public TwitchAPI tapi;

    public static DPPCore getInstance() {
        return plugin;
    }

    public Map<PluginName, JavaPlugin> getEnabledPlugins() {
        return enabledPlugins;
    }

    @Override
    public void onEnable() {
        plugin = this;
        log = getLogger();
        log.info(prefix + "DPP-Core 플러그인 활성화.");
        config = ConfigUtils.loadDefaultPluginConfig(plugin);
        dphm = new DPHManager();
        PluginUtil.loadALLPlugins();
        if (config.getBoolean("Settings.use-twitch-api")) {
            TwitchAPI.init();
        }else {
            log.warning(prefix + "TwitchAPI를 사용이 비활성화 되어있습니다.");
        }
        Plugin pl = getServer().getPluginManager().getPlugin("Essentials");
        if (pl == null) {
            getLogger().warning("Essentials 플러그인이 설치되어있지 않습니다.");
            getLogger().warning("MoneyAPI 사용 불가.");
            return;
        } else {
            ess = (Essentials) pl;
        }
        Bukkit.getScheduler().runTaskLater(plugin, () -> enabledPlugins.keySet().forEach(SchedulerUtils::initUpdateChecker), 1200L);
        getCommand("dppc").setExecutor(new DUCCommand());
    }

    @Override
    public void onDisable() {
        log.info(prefix + "DPP-Core 플러그인 비활성화.");
        ConfigUtils.savePluginConfig(plugin, config);
    }
}

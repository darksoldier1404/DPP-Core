package com.darksoldier1404.dppc;

import com.darksoldier1404.dppc.api.placeholder.PlaceholderBuilder;
import com.darksoldier1404.dppc.builder.action.ActionBuilder;
import com.darksoldier1404.dppc.builder.action.helper.ActionGUIHandler;
import com.darksoldier1404.dppc.data.DPlugin;
import com.darksoldier1404.dppc.plugin.commands.DPPCACommand;
import com.darksoldier1404.dppc.plugin.commands.DPPCCommand;
import com.darksoldier1404.dppc.plugin.commands.DPPCPCommand;
import com.darksoldier1404.dppc.utils.ConfigUtils;
import com.darksoldier1404.dppc.utils.PluginUtil;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

@SuppressWarnings("all")
public class DPPCore extends DPlugin {
    public static DPPCore plugin;
    public YamlConfiguration config;
    public Logger log;
    public static Plugin ess;
    public static Plugin lp;
    public static Set<PlaceholderBuilder.InternalExpansion> placeholders = new HashSet<>();
    public static Map<String, ActionBuilder> actions = new HashMap<>();

    public DPPCore() {
        super(true);
        plugin = this;
        init();
    }

    public static DPPCore getInstance() {
        return plugin;
    }

    public static Map<String, ActionBuilder> getActions() {
        return actions;
    }

    @Override
    public void onLoad() {
        PluginUtil.addPlugin(plugin, 24432);
    }

    @Override
    public void onEnable() {
        log = getLogger();
        config = ConfigUtils.loadDefaultPluginConfig(plugin);
        PluginUtil.loadALLPlugins();
        PluginUtil.initializeSoftDependPlugins();
        PluginUtil.loadALLAction();
        PluginUtil.initPlaceholders();
        getServer().getPluginManager().registerEvents(new ActionGUIHandler(), this);
        getCommand("dppc").setExecutor(new DPPCCommand());
        getCommand("dppca").setExecutor(new DPPCACommand());
        getCommand("dppcp").setExecutor(new DPPCPCommand().getExecutor());
        PluginUtil.showBanner();
    }

    @Override
    public void onDisable() {
        log.info("plugin disabled.");
        ConfigUtils.savePluginConfig(plugin, config);
    }
}

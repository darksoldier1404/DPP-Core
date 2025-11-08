package com.darksoldier1404.dppc.builder.command;

import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.Map;

public class CommandArguments {
    private final Map<String, Object> parsedArgs;

    public CommandArguments(Map<String, Object> parsedArgs) {
        this.parsedArgs = parsedArgs;
    }

    public Player getPlayer(String key) {
        return (Player) parsedArgs.get(key);
    }

    public Integer getInteger(String key) {
        return (Integer) parsedArgs.get(key);
    }

    public Double getDouble(String key) {
        return (Double) parsedArgs.get(key);
    }

    public Boolean getBoolean(String key) {
        return (Boolean) parsedArgs.get(key);
    }

    public String getString(String key) {
        return (String) parsedArgs.get(key);
    }

    public Object get(String key) {
        return parsedArgs.get(key);
    }

    public String[] getStringArray(String key) {
        Object value = parsedArgs.get(key);
        if (value instanceof String[]) {
            return (String[]) value;
        }
        return null;
    }

    public OfflinePlayer getOfflinePlayer(String key) {
        Object value = parsedArgs.get(key);
        if (value instanceof OfflinePlayer) {
            return (OfflinePlayer) value;
        }
        return null;
    }

    public World getWorld(String key) {
        Object value = parsedArgs.get(key);
        if (value instanceof World) {
            return (World) value;
        }
        return null;
    }
}
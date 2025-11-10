package com.darksoldier1404.dppc.builder.command;

import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.Material;

import java.util.Map;

public class CommandArguments {
    private final Map<String, Object> parsedArgs;

    public CommandArguments(Map<String, Object> parsedArgs) {
        this.parsedArgs = parsedArgs;
    }

    public Player getPlayer(String key) {
        Object value = parsedArgs.get(key);
        if (value instanceof Player) {
            return (Player) value;
        }
        return null;
    }

    public Integer getInteger(String key) {
        Object value = parsedArgs.get(key);
        if (value instanceof Integer) {
            return (Integer) value;
        }
        return null;
    }

    public Double getDouble(String key) {
        Object value = parsedArgs.get(key);
        if (value instanceof Double) {
            return (Double) value;
        }
        return null;
    }

    public Boolean getBoolean(String key) {
        Object value = parsedArgs.get(key);
        if (value instanceof Boolean) {
            return (Boolean) value;
        }
        return null;
    }

    public String getString(String key) {
        Object value = parsedArgs.get(key);
        if (value instanceof String) {
            return (String) value;
        }
        return null;
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

    public Material getMaterial(String key) {
        Object value = parsedArgs.get(key);
        if (value instanceof Material) {
            return (Material) value;
        }
        return null;
    }

    public EntityType getEntityType(String key) {
        Object value = parsedArgs.get(key);
        if (value instanceof EntityType) {
            return (EntityType) value;
        }
        return null;
    }
}
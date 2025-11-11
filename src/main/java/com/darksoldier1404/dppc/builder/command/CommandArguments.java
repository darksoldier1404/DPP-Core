package com.darksoldier1404.dppc.builder.command;

import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.Material;

import java.util.Map;

public class CommandArguments {
    private final Map<ArgumentIndex, Object> parsedArgs;

    public CommandArguments(Map<ArgumentIndex, Object> parsedArgs) {
        this.parsedArgs = parsedArgs;
    }

    public Player getPlayer(ArgumentIndex key) {
        Object value = parsedArgs.get(key);
        if (value instanceof Player) {
            return (Player) value;
        }
        return null;
    }

    public Byte getByte(ArgumentIndex key) {
        Object value = parsedArgs.get(key);
        if (value instanceof Byte) {
            return (Byte) value;
        }
        return null;
    }

    public Short getShort(ArgumentIndex key) {
        Object value = parsedArgs.get(key);
        if (value instanceof Short) {
            return (Short) value;
        }
        return null;
    }

    public Integer getInteger(ArgumentIndex key) {
        Object value = parsedArgs.get(key);
        if (value instanceof Integer) {
            return (Integer) value;
        }
        return null;
    }

    public Float getFloat(ArgumentIndex key) {
        Object value = parsedArgs.get(key);
        if (value instanceof Float) {
            return (Float) value;
        }
        return null;
    }

    public Double getDouble(ArgumentIndex key) {
        Object value = parsedArgs.get(key);
        if (value instanceof Double) {
            return (Double) value;
        }
        return null;
    }

    public Boolean getBoolean(ArgumentIndex key) {
        Object value = parsedArgs.get(key);
        if (value instanceof Boolean) {
            return (Boolean) value;
        }
        return null;
    }

    public Character getCharacter(ArgumentIndex key) {
        Object value = parsedArgs.get(key);
        if (value instanceof Character) {
            return (Character) value;
        }
        return null;
    }

    public String getString(ArgumentIndex key) {
        Object value = parsedArgs.get(key);
        if (value instanceof String) {
            return (String) value;
        }
        return null;
    }

    public Object get(ArgumentIndex key) {
        return parsedArgs.get(key);
    }

    public String[] getStringArray(ArgumentIndex key) {
        Object value = parsedArgs.get(key);
        if (value instanceof String[]) {
            return (String[]) value;
        }
        return null;
    }

    public OfflinePlayer getOfflinePlayer(ArgumentIndex key) {
        Object value = parsedArgs.get(key);
        if (value instanceof OfflinePlayer) {
            return (OfflinePlayer) value;
        }
        return null;
    }

    public World getWorld(ArgumentIndex key) {
        Object value = parsedArgs.get(key);
        if (value instanceof World) {
            return (World) value;
        }
        return null;
    }

    public Material getMaterial(ArgumentIndex key) {
        Object value = parsedArgs.get(key);
        if (value instanceof Material) {
            return (Material) value;
        }
        return null;
    }

    public EntityType getEntityType(ArgumentIndex key) {
        Object value = parsedArgs.get(key);
        if (value instanceof EntityType) {
            return (EntityType) value;
        }
        return null;
    }
}
package com.darksoldier1404.dppc.utils;

import com.darksoldier1404.dppc.nbt.NBTAPI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("all")
public class NBT {

    @NotNull
    public static ItemStack setObjectTag(ItemStack item, String key, Object value) {
        NBTAPI.modify(item, (nbt) -> {
            nbt.setString(key, value.toString());
        });
        return item;
    }

    @NotNull
    public static ItemStack removeTag(ItemStack item, String key) {
        NBTAPI.modify(item, (nbt) -> {
            nbt.removeKey(key);
        });
        return item;
    }

    @NotNull
    public static ItemStack removeAllTags(ItemStack item) {
        NBTAPI.modify(item, (nbt) -> {
            nbt.getKeys().forEach(nbt::removeKey);
        });
        return item;
    }

    @NotNull
    public static String getStringTag(ItemStack item, String key) {
        return NBTAPI.get(item, (nbt) -> {
            return nbt.getString(key);
        });
    }

    /**
     * @param item ItemStack
     * @param key  String
     * @return byte
     */
    @NotNull
    public static byte getByteTag(ItemStack item, String key) {
        return NBTAPI.get(item, (nbt) -> {
            return nbt.getByte(key);
        });
    }

    /**
     * @param item ItemStack
     * @param key  String
     * @return short
     */
    @NotNull
    public static short getShortTag(ItemStack item, String key) {
        return NBTAPI.get(item, (nbt) -> {
            return nbt.getShort(key);
        });
    }

    /**
     * @param item ItemStack
     * @param key  String
     * @return int
     */
    @NotNull
    public static int getIntegerTag(ItemStack item, String key) {
        return NBTAPI.get(item, (nbt) -> {
            return nbt.getInteger(key);
        });
    }

    /**
     * @param item ItemStack
     * @param key  String
     * @return float
     */
    @NotNull
    public static float getFloatTag(ItemStack item, String key) {
        return NBTAPI.get(item, (nbt) -> {
            return nbt.getFloat(key);
        });
    }

    /**
     * @param item ItemStack
     * @param key  String
     * @return double
     */
    @NotNull
    public static double getDoubleTag(ItemStack item, String key) {
        return NBTAPI.get(item, (nbt) -> {
            return nbt.getDouble(key);
        });
    }

    /**
     * @param item ItemStack
     * @param key  String
     * @return boolean
     */
    public static boolean getBooleanTag(ItemStack item, String key) {
        return NBTAPI.get(item, (nbt) -> {
            return nbt.getBoolean(key);
        });
    }

    /**
     * @param item ItemStack
     * @param key  String
     * @return long
     */
    @NotNull
    public static long getLongTag(ItemStack item, String key) {
        return NBTAPI.get(item, (nbt) -> {
            return nbt.getLong(key);
        });
    }

    /**
     * @param item ItemStack
     * @param key  String
     * @return byte[]
     */
    @NotNull
    public static byte[] getByteArrayTag(ItemStack item, String key) {
        return NBTAPI.get(item, (nbt) -> {
            return nbt.getByteArray(key);
        });
    }

    /**
     * @param item ItemStack
     * @param key  String
     * @return int[]
     */
    @NotNull
    public static int[] getIntArrayTag(ItemStack item, String key) {
        return NBTAPI.get(item, (nbt) -> {
            return nbt.getIntArray(key);
        });
    }

    /**
     * @param item ItemStack
     * @param key  String
     * @return Material
     */
    @Nullable
    public static Material getMaterialTag(ItemStack item, String key) {
        return Material.valueOf(NBTAPI.get(item, (nbt) -> {
            return nbt.getString(key);
        }));
    }

    /**
     * @param item ItemStack
     * @param key  String
     * @return InventoryType
     */
    @Nullable
    public static InventoryType getInventoryTypeTag(ItemStack item, String key) {
        return InventoryType.valueOf(NBTAPI.get(item, (nbt) -> {
            return nbt.getString(key);
        }));
    }

    /**
     * @param item ItemStack
     * @param key  String
     * @return NBTTagList
     */
    @Nullable
    public static EntityType getEntityTypeTag(ItemStack item, String key) {
        return EntityType.valueOf(NBTAPI.get(item, (nbt) -> {
            return nbt.getString(key);
        }));
    }

    /**
     * @param item ItemStack
     * @param key  String
     * @return boolean
     */
    public static boolean hasTagKey(ItemStack item, String key) {
        if (item == null) {
            return false;
        }
        try {
            return NBTAPI.get(item, (nbt) -> {
                return nbt.hasTag(key);
            });
        } catch (Exception ignore) {
            return false;
        }
    }

    @Nullable
    public static Map<String, String> getAllStringTag(ItemStack item) {
        if (item == null) {
            return null;
        }
        Map<String, String> map = new HashMap<>();
        NBTAPI.get(item, (nbt) -> {
            nbt.getKeys().forEach((key) -> {
                map.put(key, nbt.getTag(key).toString());
            });
            return null;
        });
        return map;
    }

    /**
     * @param item  ItemStack
     * @param key   String
     * @param value String
     * @return ItemStack
     */
    @NotNull
    public static ItemStack setStringTag(ItemStack item, String key, String value) {
        NBTAPI.modify(item, (nbt) -> {
            nbt.setString(key, value);
        });
        return item;
    }

    @NotNull
    public static ItemStack setStringTags(ItemStack item, String[] keys, String[] values) {
        for (int i = 0; i < keys.length; i++) {
            item = NBT.setStringTag(item, keys[i], values[i]);
        }
        return item;
    }

    /**
     * @param item  ItemStack
     * @param key   String
     * @param value byte
     * @return ItemStack
     */
    @NotNull
    public static ItemStack setByteTag(ItemStack item, String key, byte value) {
        NBTAPI.modify(item, (nbt) -> {
            nbt.setByte(key, value);
        });
        return item;
    }

    /**
     * @param item  ItemStack
     * @param key   String
     * @param value short
     * @return ItemStack
     */
    @NotNull
    public static ItemStack setShortTag(ItemStack item, String key, short value) {
        NBTAPI.modify(item, (nbt) -> {
            nbt.setShort(key, value);
        });
        return item;
    }

    /**
     * @param item  ItemStack
     * @param key   String
     * @param value int
     * @return ItemStack
     */
    @NotNull
    public static ItemStack setIntTag(ItemStack item, String key, int value) {
        NBTAPI.modify(item, (nbt) -> {
            nbt.setInteger(key, value);
        });
        return item;
    }

    /**
     * @param item  ItemStack
     * @param key   String
     * @param value long
     * @return ItemStack
     */
    @NotNull
    public static ItemStack setLongTag(ItemStack item, String key, long value) {
        NBTAPI.modify(item, (nbt) -> {
            nbt.setLong(key, value);
        });
        return item;
    }

    /**
     * @param item  ItemStack
     * @param key   String
     * @param value float
     * @return ItemStack
     */
    @NotNull
    public static ItemStack setFloatTag(ItemStack item, String key, float value) {
        NBTAPI.modify(item, (nbt) -> {
            nbt.setFloat(key, value);
        });
        return item;
    }

    /**
     * @param item  ItemStack
     * @param key   String
     * @param value double
     * @return ItemStack
     */
    @NotNull
    public static ItemStack setDoubleTag(ItemStack item, String key, double value) {
        NBTAPI.modify(item, (nbt) -> {
            nbt.setDouble(key, value);
        });
        return item;
    }

    @NotNull
    public static ItemStack setBooleanTag(ItemStack item, String key, boolean value) {
        NBTAPI.modify(item, (nbt) -> {
            nbt.setBoolean(key, value);
        });
        return item;
    }

    /**
     * @param item  ItemStack
     * @param key   String
     * @param value byte[]
     * @return ItemStack
     */
    @NotNull
    public static ItemStack setByteArrayTag(ItemStack item, String key, byte[] value) {
        NBTAPI.modify(item, (nbt) -> {
            nbt.setByteArray(key, value);
        });
        return item;
    }

    /**
     * @param item  ItemStack
     * @param key   String
     * @param value int[]
     * @return ItemStack
     */
    @NotNull
    public static ItemStack setIntArrayTag(ItemStack item, String key, int[] value) {
        NBTAPI.modify(item, (nbt) -> {
            nbt.setIntArray(key, value);
        });
        return item;
    }

    /**
     * @param item  ItemStack
     * @param key   String
     * @param value Material
     * @return ItemStack
     */
    @NotNull
    public static ItemStack setMaterialTag(ItemStack item, String key, Material value) {
        NBTAPI.modify(item, (nbt) -> {
            nbt.setString(key, value.toString());
        });
        return item;
    }

    // ItemStackSerializer

    // Single ItemStack

    /**
     * @param item  ItemStack
     * @param key   String
     * @param value ItemStack
     * @return ItemStack
     */
    public static ItemStack setItemStackTag(ItemStack item, String key, ItemStack value) {
        String sitem = ItemStackSerializer.serialize(value);
        String[] sitems = sitem.split("(?<=\\G.{288})");

        for (int i = 0; i < sitems.length; i++) {
            item = NBT.setStringTag(item, key + i, sitems[i]);
        }
        item = NBT.setIntTag(item, key + "_size", sitems.length);
        return item;
    }

    /**
     * @param item ItemStack
     * @param key  String
     * @return ItemStack
     */
    @Nullable
    public static ItemStack getItemStackTag(ItemStack item, String key) {
        int length = NBT.getIntegerTag(item, key + "_size");
        String s = "";
        for (int i = 0; i < length; i++) {
            s += NBT.getStringTag(item, key + i);
        }
        return ItemStackSerializer.deserialize(s);
    }

    // Inventory

    /**
     * @param item ItemStack
     * @param inv  Inventory
     * @param key  String
     * @return ItemStack
     */
    public static ItemStack setInventoryTag(ItemStack item, Inventory inv, String key) {
        for (int i = 0; i < inv.getSize(); i++) {
            item = NBT.setItemStackTag(item, "inv_" + key + "_" + i + "_item", inv.getItem(i));
        }
        item = NBT.setIntTag(item, "inv_" + key + "_size", inv.getSize());
        return item;
    }

    /**
     * @param item ItemStack
     * @param key  String
     * @return Inventory
     */
    @Nullable
    public static Inventory getInventoryTag(ItemStack item, String key) {
        Inventory inv = Bukkit.createInventory(null, NBT.getIntegerTag(item, "inv_" + key + "_size"));
        for (int i = 0; i < inv.getSize(); i++) {
            inv.setItem(i, NBT.getItemStackTag(item, "inv_" + key + "_" + i + "_item"));
        }
        return inv;
    }
}
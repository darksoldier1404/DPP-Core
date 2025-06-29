package com.darksoldier1404.dppc.nbt;

import org.bukkit.inventory.ItemStack;
import org.bukkit.Material;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.NamespacedKey;

import java.util.HashSet;
import java.util.Set;

public class NBTItem {
    private final ItemStack item;
    private final ItemMeta meta;
    private final PersistentDataContainer container;
    private static final String NAMESPACE = "dppc";

    public NBTItem(ItemStack item) {
        if (item == null || item.getType() == Material.AIR)
            throw new IllegalArgumentException("ItemStack cannot be null or AIR");
        this.item = item;
        this.meta = item.getItemMeta();
        if (this.meta == null) throw new IllegalArgumentException("ItemMeta cannot be null");
        this.container = this.meta.getPersistentDataContainer();
    }

    private NamespacedKey key(String key) {
        return new NamespacedKey(NAMESPACE, key);
    }

    public void setString(String key, String value) {
        container.set(key(key), PersistentDataType.STRING, value);
        item.setItemMeta(meta);
    }

    public String getString(String key) {
        return container.get(key(key), PersistentDataType.STRING);
    }

    public void setInteger(String key, int value) {
        container.set(key(key), PersistentDataType.INTEGER, value);
        item.setItemMeta(meta);
    }

    public int getInteger(String key) {
        Integer v = container.get(key(key), PersistentDataType.INTEGER);
        return v == null ? 0 : v;
    }

    public void setIntArray(String key, int[] value) {
        container.set(key(key), PersistentDataType.INTEGER_ARRAY, value);
        item.setItemMeta(meta);
    }

    public int[] getIntArray(String key) {
        int[] v = container.get(key(key), PersistentDataType.INTEGER_ARRAY);
        return v == null ? new int[0] : v;
    }

    public void setByte(String key, byte value) {
        container.set(key(key), PersistentDataType.BYTE, value);
        item.setItemMeta(meta);
    }

    public byte getByte(String key) {
        Byte v = container.get(key(key), PersistentDataType.BYTE);
        return v == null ? 0 : v;
    }

    public void setShort(String key, short value) {
        container.set(key(key), PersistentDataType.SHORT, value);
        item.setItemMeta(meta);
    }

    public short getShort(String key) {
        Short v = container.get(key(key), PersistentDataType.SHORT);
        return v == null ? 0 : v;
    }

    public void setLong(String key, long value) {
        container.set(key(key), PersistentDataType.LONG, value);
        item.setItemMeta(meta);
    }

    public long getLong(String key) {
        Long v = container.get(key(key), PersistentDataType.LONG);
        return v == null ? 0L : v;
    }

    public void setFloat(String key, float value) {
        container.set(key(key), PersistentDataType.FLOAT, value);
        item.setItemMeta(meta);
    }

    public float getFloat(String key) {
        Float v = container.get(key(key), PersistentDataType.FLOAT);
        return v == null ? 0f : v;
    }

    public void setDouble(String key, double value) {
        container.set(key(key), PersistentDataType.DOUBLE, value);
        item.setItemMeta(meta);
    }

    public double getDouble(String key) {
        Double v = container.get(key(key), PersistentDataType.DOUBLE);
        return v == null ? 0d : v;
    }

    public void setBoolean(String key, boolean value) {
        container.set(key(key), PersistentDataType.BYTE, (byte) (value ? 1 : 0));
        item.setItemMeta(meta);
    }

    public boolean getBoolean(String key) {
        Byte v = container.get(key(key), PersistentDataType.BYTE);
        return v != null && v == 1;
    }

    public void setByteArray(String key, byte[] value) {
        container.set(key(key), PersistentDataType.BYTE_ARRAY, value);
        item.setItemMeta(meta);
    }

    public byte[] getByteArray(String key) {
        byte[] v = container.get(key(key), PersistentDataType.BYTE_ARRAY);
        return v == null ? new byte[0] : v;
    }

    public void removeKey(String key) {
        container.remove(key(key));
        item.setItemMeta(meta);
    }

    public boolean hasTag(String key) {
        return container.has(key(key), PersistentDataType.STRING) ||
                container.has(key(key), PersistentDataType.INTEGER) ||
                container.has(key(key), PersistentDataType.INTEGER_ARRAY) ||
                container.has(key(key), PersistentDataType.BYTE) ||
                container.has(key(key), PersistentDataType.SHORT) ||
                container.has(key(key), PersistentDataType.LONG) ||
                container.has(key(key), PersistentDataType.FLOAT) ||
                container.has(key(key), PersistentDataType.DOUBLE) ||
                container.has(key(key), PersistentDataType.BYTE_ARRAY);
    }

    public Object getTag(String key) {
        NamespacedKey namespacedKey = key(key);
        if (!container.getKeys().contains(namespacedKey)) return null;
        if (container.has(namespacedKey, PersistentDataType.STRING))
            return container.get(namespacedKey, PersistentDataType.STRING);
        if (container.has(namespacedKey, PersistentDataType.INTEGER))
            return container.get(namespacedKey, PersistentDataType.INTEGER);
        if (container.has(namespacedKey, PersistentDataType.INTEGER_ARRAY))
            return container.get(namespacedKey, PersistentDataType.INTEGER_ARRAY);
        if (container.has(namespacedKey, PersistentDataType.BYTE))
            return container.get(namespacedKey, PersistentDataType.BYTE);
        if (container.has(namespacedKey, PersistentDataType.SHORT))
            return container.get(namespacedKey, PersistentDataType.SHORT);
        if (container.has(namespacedKey, PersistentDataType.LONG))
            return container.get(namespacedKey, PersistentDataType.LONG);
        if (container.has(namespacedKey, PersistentDataType.FLOAT))
            return container.get(namespacedKey, PersistentDataType.FLOAT);
        if (container.has(namespacedKey, PersistentDataType.DOUBLE))
            return container.get(namespacedKey, PersistentDataType.DOUBLE);
        if (container.has(namespacedKey, PersistentDataType.BYTE_ARRAY))
            return container.get(namespacedKey, PersistentDataType.BYTE_ARRAY);
        return null;
    }

    public Set<String> getKeys() {
        Set<String> keys = new HashSet<>();
        for (NamespacedKey k : container.getKeys()) {
            if (NAMESPACE.equals(k.getNamespace())) {
                keys.add(k.getKey());
            }
        }
        return keys;
    }

    public ItemStack getItem() {
        item.setItemMeta(meta);
        return item;
    }
}

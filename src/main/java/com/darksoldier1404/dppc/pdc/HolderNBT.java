package com.darksoldier1404.dppc.pdc;

import com.darksoldier1404.dppc.utils.NBT;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.EntityType;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataHolder;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Static NBT-style helper for any {@link PersistentDataHolder} (Entity, Chunk, BlockState, ...).
 * <p>
 * Built on top of {@link PDCModifier}, mirroring the {@link NBT} (ItemStack) convenience API.
 * All keys are stored under the {@code dppc} namespace, matching
 * {@link com.darksoldier1404.dppc.nbt.NBTItem}.
 * <p>
 * Unlike {@link NBT} for ItemStacks, changes apply directly to the live holder's
 * {@link PersistentDataContainer}, so setters return the holder for chaining.
 *
 * @see EntityNBT
 * @see ChunkNBT
 */
@SuppressWarnings("all")
public class HolderNBT {
    private static final String NAMESPACE = "dppc";

    private HolderNBT() {
    }

    private static NamespacedKey key(String key) {
        return new NamespacedKey(NAMESPACE, key.toLowerCase());
    }

    // ---------------------------------------------------------------------
    // setters
    // ---------------------------------------------------------------------

    @NotNull
    public static <H extends PersistentDataHolder> H setStringTag(H holder, String key, String value) {
        PDCModifier.setValue(holder, key(key), PersistentDataType.STRING, value);
        return holder;
    }

    @NotNull
    public static <H extends PersistentDataHolder> H setObjectTag(H holder, String key, Object value) {
        PDCModifier.setValue(holder, key(key), PersistentDataType.STRING, value.toString());
        return holder;
    }

    @NotNull
    public static <H extends PersistentDataHolder> H setByteTag(H holder, String key, byte value) {
        PDCModifier.setValue(holder, key(key), PersistentDataType.BYTE, value);
        return holder;
    }

    @NotNull
    public static <H extends PersistentDataHolder> H setShortTag(H holder, String key, short value) {
        PDCModifier.setValue(holder, key(key), PersistentDataType.SHORT, value);
        return holder;
    }

    @NotNull
    public static <H extends PersistentDataHolder> H setIntTag(H holder, String key, int value) {
        PDCModifier.setValue(holder, key(key), PersistentDataType.INTEGER, value);
        return holder;
    }

    @NotNull
    public static <H extends PersistentDataHolder> H setLongTag(H holder, String key, long value) {
        PDCModifier.setValue(holder, key(key), PersistentDataType.LONG, value);
        return holder;
    }

    @NotNull
    public static <H extends PersistentDataHolder> H setFloatTag(H holder, String key, float value) {
        PDCModifier.setValue(holder, key(key), PersistentDataType.FLOAT, value);
        return holder;
    }

    @NotNull
    public static <H extends PersistentDataHolder> H setDoubleTag(H holder, String key, double value) {
        PDCModifier.setValue(holder, key(key), PersistentDataType.DOUBLE, value);
        return holder;
    }

    @NotNull
    public static <H extends PersistentDataHolder> H setBooleanTag(H holder, String key, boolean value) {
        PDCModifier.setValue(holder, key(key), PersistentDataType.BYTE, (byte) (value ? 1 : 0));
        return holder;
    }

    @NotNull
    public static <H extends PersistentDataHolder> H setByteArrayTag(H holder, String key, byte[] value) {
        PDCModifier.setValue(holder, key(key), PersistentDataType.BYTE_ARRAY, value);
        return holder;
    }

    @NotNull
    public static <H extends PersistentDataHolder> H setIntArrayTag(H holder, String key, int[] value) {
        PDCModifier.setValue(holder, key(key), PersistentDataType.INTEGER_ARRAY, value);
        return holder;
    }

    @NotNull
    public static <H extends PersistentDataHolder> H setMaterialTag(H holder, String key, Material value) {
        PDCModifier.setValue(holder, key(key), PersistentDataType.STRING, value.toString());
        return holder;
    }

    @NotNull
    public static <H extends PersistentDataHolder> H setEntityTypeTag(H holder, String key, EntityType value) {
        PDCModifier.setValue(holder, key(key), PersistentDataType.STRING, value.toString());
        return holder;
    }

    /**
     * Stores a {@link Serializable} object as a byte array via {@link PDCModifier#setObject}.
     */
    @NotNull
    public static <H extends PersistentDataHolder> H setSerializableTag(H holder, String key, Serializable value) throws IOException {
        PDCModifier.setObject(holder, key(key), value);
        return holder;
    }

    // ---------------------------------------------------------------------
    // getters
    // ---------------------------------------------------------------------

    @Nullable
    public static String getStringTag(PersistentDataHolder holder, String key) {
        return PDCModifier.getValue(holder, key(key), PersistentDataType.STRING, null);
    }

    public static byte getByteTag(PersistentDataHolder holder, String key) {
        return PDCModifier.getValue(holder, key(key), PersistentDataType.BYTE, (byte) 0);
    }

    public static short getShortTag(PersistentDataHolder holder, String key) {
        return PDCModifier.getValue(holder, key(key), PersistentDataType.SHORT, (short) 0);
    }

    public static int getIntegerTag(PersistentDataHolder holder, String key) {
        return PDCModifier.getValue(holder, key(key), PersistentDataType.INTEGER, 0);
    }

    public static long getLongTag(PersistentDataHolder holder, String key) {
        return PDCModifier.getValue(holder, key(key), PersistentDataType.LONG, 0L);
    }

    public static float getFloatTag(PersistentDataHolder holder, String key) {
        return PDCModifier.getValue(holder, key(key), PersistentDataType.FLOAT, 0f);
    }

    public static double getDoubleTag(PersistentDataHolder holder, String key) {
        return PDCModifier.getValue(holder, key(key), PersistentDataType.DOUBLE, 0d);
    }

    public static boolean getBooleanTag(PersistentDataHolder holder, String key) {
        Byte v = PDCModifier.getValue(holder, key(key), PersistentDataType.BYTE, null);
        return v != null && v == 1;
    }

    @NotNull
    public static byte[] getByteArrayTag(PersistentDataHolder holder, String key) {
        return PDCModifier.getValue(holder, key(key), PersistentDataType.BYTE_ARRAY, new byte[0]);
    }

    @NotNull
    public static int[] getIntArrayTag(PersistentDataHolder holder, String key) {
        return PDCModifier.getValue(holder, key(key), PersistentDataType.INTEGER_ARRAY, new int[0]);
    }

    @Nullable
    public static Material getMaterialTag(PersistentDataHolder holder, String key) {
        String value = getStringTag(holder, key);
        if (value == null) return null;
        try {
            return Material.valueOf(value);
        } catch (IllegalArgumentException ignore) {
            return null;
        }
    }

    @Nullable
    public static EntityType getEntityTypeTag(PersistentDataHolder holder, String key) {
        String value = getStringTag(holder, key);
        if (value == null) return null;
        try {
            return EntityType.valueOf(value);
        } catch (IllegalArgumentException ignore) {
            return null;
        }
    }

    /**
     * Deserializes a {@link Serializable} object previously stored via
     * {@link #setSerializableTag} using {@link PDCModifier#getObject}.
     */
    @NotNull
    public static <T extends Serializable> Optional<T> getSerializableTag(PersistentDataHolder holder, String key, Class<T> clazz) {
        return PDCModifier.getObject(holder, key(key), clazz);
    }

    // ---------------------------------------------------------------------
    // management
    // ---------------------------------------------------------------------

    public static boolean hasTagKey(PersistentDataHolder holder, String key) {
        if (holder == null) return false;
        try {
            return holder.getPersistentDataContainer().getKeys().contains(key(key));
        } catch (Exception ignore) {
            return false;
        }
    }

    @NotNull
    public static <H extends PersistentDataHolder> H removeTag(H holder, String key) {
        PDCModifier.removeData(holder, key(key));
        return holder;
    }

    @NotNull
    public static <H extends PersistentDataHolder> H removeAllTags(H holder) {
        PersistentDataContainer pdc = holder.getPersistentDataContainer();
        for (NamespacedKey k : new HashSet<>(pdc.getKeys())) {
            if (NAMESPACE.equals(k.getNamespace())) {
                pdc.remove(k);
            }
        }
        return holder;
    }

    /**
     * @return the {@code dppc}-namespaced keys present on the holder (without namespace prefix).
     */
    @NotNull
    public static Set<String> getKeys(PersistentDataHolder holder) {
        Set<String> keys = new HashSet<>();
        if (holder == null) return keys;
        for (NamespacedKey k : holder.getPersistentDataContainer().getKeys()) {
            if (NAMESPACE.equals(k.getNamespace())) {
                keys.add(k.getKey());
            }
        }
        return keys;
    }

    /**
     * Reads a tag of unknown type, probing the common {@link PersistentDataType}s.
     *
     * @return the stored value, or {@code null} if the key is absent.
     */
    @Nullable
    public static Object getTag(PersistentDataHolder holder, String key) {
        if (holder == null) return null;
        PersistentDataContainer pdc = holder.getPersistentDataContainer();
        NamespacedKey nk = key(key);
        if (!pdc.getKeys().contains(nk)) return null;
        if (pdc.has(nk, PersistentDataType.STRING)) return pdc.get(nk, PersistentDataType.STRING);
        if (pdc.has(nk, PersistentDataType.INTEGER)) return pdc.get(nk, PersistentDataType.INTEGER);
        if (pdc.has(nk, PersistentDataType.INTEGER_ARRAY)) return pdc.get(nk, PersistentDataType.INTEGER_ARRAY);
        if (pdc.has(nk, PersistentDataType.BYTE)) return pdc.get(nk, PersistentDataType.BYTE);
        if (pdc.has(nk, PersistentDataType.SHORT)) return pdc.get(nk, PersistentDataType.SHORT);
        if (pdc.has(nk, PersistentDataType.LONG)) return pdc.get(nk, PersistentDataType.LONG);
        if (pdc.has(nk, PersistentDataType.FLOAT)) return pdc.get(nk, PersistentDataType.FLOAT);
        if (pdc.has(nk, PersistentDataType.DOUBLE)) return pdc.get(nk, PersistentDataType.DOUBLE);
        if (pdc.has(nk, PersistentDataType.BYTE_ARRAY)) return pdc.get(nk, PersistentDataType.BYTE_ARRAY);
        return null;
    }

    /**
     * @return every {@code dppc} tag on the holder as a {@code key -> value.toString()} map.
     */
    @Nullable
    public static Map<String, String> getAllStringTag(PersistentDataHolder holder) {
        if (holder == null) return null;
        Map<String, String> map = new HashMap<>();
        for (String k : getKeys(holder)) {
            Object value = getTag(holder, k);
            if (value != null) {
                map.put(k, value.toString());
            }
        }
        return map;
    }
}

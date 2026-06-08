package com.darksoldier1404.dppc.pdc;

import com.darksoldier1404.dppc.utils.NBT;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.Serializable;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Static NBT-style helper for {@link Entity} persistent data.
 * <p>
 * Thin, type-safe facade over {@link HolderNBT} (which is built on
 * {@link com.darksoldier1404.dppc.pdc.PDCModifier}). Mirrors the {@link NBT}
 * (ItemStack) API. Tags are stored on the entity's
 * {@link org.bukkit.persistence.PersistentDataContainer} under the {@code dppc} namespace
 * and persist across server restarts (chunk save/load).
 *
 * <pre>{@code
 * EntityNBT.setStringTag(entity, "owner", player.getName());
 * EntityNBT.setIntTag(entity, "level", 5);
 *
 * String owner = EntityNBT.getStringTag(entity, "owner");
 * int level    = EntityNBT.getIntegerTag(entity, "level");
 * boolean has  = EntityNBT.hasTagKey(entity, "owner");
 * }</pre>
 *
 * @see HolderNBT
 * @see ChunkNBT
 */
@SuppressWarnings("all")
public class EntityNBT {

    private EntityNBT() {
    }

    // setters

    @NotNull
    public static Entity setStringTag(Entity entity, String key, String value) {
        return HolderNBT.setStringTag(entity, key, value);
    }

    @NotNull
    public static Entity setObjectTag(Entity entity, String key, Object value) {
        return HolderNBT.setObjectTag(entity, key, value);
    }

    @NotNull
    public static Entity setByteTag(Entity entity, String key, byte value) {
        return HolderNBT.setByteTag(entity, key, value);
    }

    @NotNull
    public static Entity setShortTag(Entity entity, String key, short value) {
        return HolderNBT.setShortTag(entity, key, value);
    }

    @NotNull
    public static Entity setIntTag(Entity entity, String key, int value) {
        return HolderNBT.setIntTag(entity, key, value);
    }

    @NotNull
    public static Entity setLongTag(Entity entity, String key, long value) {
        return HolderNBT.setLongTag(entity, key, value);
    }

    @NotNull
    public static Entity setFloatTag(Entity entity, String key, float value) {
        return HolderNBT.setFloatTag(entity, key, value);
    }

    @NotNull
    public static Entity setDoubleTag(Entity entity, String key, double value) {
        return HolderNBT.setDoubleTag(entity, key, value);
    }

    @NotNull
    public static Entity setBooleanTag(Entity entity, String key, boolean value) {
        return HolderNBT.setBooleanTag(entity, key, value);
    }

    @NotNull
    public static Entity setByteArrayTag(Entity entity, String key, byte[] value) {
        return HolderNBT.setByteArrayTag(entity, key, value);
    }

    @NotNull
    public static Entity setIntArrayTag(Entity entity, String key, int[] value) {
        return HolderNBT.setIntArrayTag(entity, key, value);
    }

    @NotNull
    public static Entity setMaterialTag(Entity entity, String key, Material value) {
        return HolderNBT.setMaterialTag(entity, key, value);
    }

    @NotNull
    public static Entity setEntityTypeTag(Entity entity, String key, EntityType value) {
        return HolderNBT.setEntityTypeTag(entity, key, value);
    }

    @NotNull
    public static Entity setSerializableTag(Entity entity, String key, Serializable value) throws IOException {
        return HolderNBT.setSerializableTag(entity, key, value);
    }

    // getters

    @Nullable
    public static String getStringTag(Entity entity, String key) {
        return HolderNBT.getStringTag(entity, key);
    }

    public static byte getByteTag(Entity entity, String key) {
        return HolderNBT.getByteTag(entity, key);
    }

    public static short getShortTag(Entity entity, String key) {
        return HolderNBT.getShortTag(entity, key);
    }

    public static int getIntegerTag(Entity entity, String key) {
        return HolderNBT.getIntegerTag(entity, key);
    }

    public static long getLongTag(Entity entity, String key) {
        return HolderNBT.getLongTag(entity, key);
    }

    public static float getFloatTag(Entity entity, String key) {
        return HolderNBT.getFloatTag(entity, key);
    }

    public static double getDoubleTag(Entity entity, String key) {
        return HolderNBT.getDoubleTag(entity, key);
    }

    public static boolean getBooleanTag(Entity entity, String key) {
        return HolderNBT.getBooleanTag(entity, key);
    }

    @NotNull
    public static byte[] getByteArrayTag(Entity entity, String key) {
        return HolderNBT.getByteArrayTag(entity, key);
    }

    @NotNull
    public static int[] getIntArrayTag(Entity entity, String key) {
        return HolderNBT.getIntArrayTag(entity, key);
    }

    @Nullable
    public static Material getMaterialTag(Entity entity, String key) {
        return HolderNBT.getMaterialTag(entity, key);
    }

    @Nullable
    public static EntityType getEntityTypeTag(Entity entity, String key) {
        return HolderNBT.getEntityTypeTag(entity, key);
    }

    @NotNull
    public static <T extends Serializable> Optional<T> getSerializableTag(Entity entity, String key, Class<T> clazz) {
        return HolderNBT.getSerializableTag(entity, key, clazz);
    }

    // management

    public static boolean hasTagKey(Entity entity, String key) {
        return HolderNBT.hasTagKey(entity, key);
    }

    @NotNull
    public static Entity removeTag(Entity entity, String key) {
        return HolderNBT.removeTag(entity, key);
    }

    @NotNull
    public static Entity removeAllTags(Entity entity) {
        return HolderNBT.removeAllTags(entity);
    }

    @NotNull
    public static Set<String> getKeys(Entity entity) {
        return HolderNBT.getKeys(entity);
    }

    @Nullable
    public static Object getTag(Entity entity, String key) {
        return HolderNBT.getTag(entity, key);
    }

    @Nullable
    public static Map<String, String> getAllStringTag(Entity entity) {
        return HolderNBT.getAllStringTag(entity);
    }
}

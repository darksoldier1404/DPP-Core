package com.darksoldier1404.dppc.pdc;

import com.darksoldier1404.dppc.utils.NBT;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.Serializable;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Static NBT-style helper for {@link Chunk} persistent data.
 * <p>
 * Thin, type-safe facade over {@link HolderNBT} (which is built on
 * {@link com.darksoldier1404.dppc.pdc.PDCModifier}). Mirrors the {@link NBT}
 * (ItemStack) API. Tags are stored on the chunk's
 * {@link org.bukkit.persistence.PersistentDataContainer} under the {@code dppc} namespace
 * and are saved with the chunk to disk.
 *
 * <pre>{@code
 * Chunk chunk = location.getChunk();
 * ChunkNBT.setStringTag(chunk, "owner", player.getUniqueId().toString());
 * ChunkNBT.setBooleanTag(chunk, "claimed", true);
 *
 * boolean claimed = ChunkNBT.getBooleanTag(chunk, "claimed");
 * String owner    = ChunkNBT.getStringTag(chunk, "owner");
 * }</pre>
 *
 * @see HolderNBT
 * @see EntityNBT
 */
@SuppressWarnings("all")
public class ChunkNBT {

    private ChunkNBT() {
    }

    // setters

    @NotNull
    public static Chunk setStringTag(Chunk chunk, String key, String value) {
        return HolderNBT.setStringTag(chunk, key, value);
    }

    @NotNull
    public static Chunk setObjectTag(Chunk chunk, String key, Object value) {
        return HolderNBT.setObjectTag(chunk, key, value);
    }

    @NotNull
    public static Chunk setByteTag(Chunk chunk, String key, byte value) {
        return HolderNBT.setByteTag(chunk, key, value);
    }

    @NotNull
    public static Chunk setShortTag(Chunk chunk, String key, short value) {
        return HolderNBT.setShortTag(chunk, key, value);
    }

    @NotNull
    public static Chunk setIntTag(Chunk chunk, String key, int value) {
        return HolderNBT.setIntTag(chunk, key, value);
    }

    @NotNull
    public static Chunk setLongTag(Chunk chunk, String key, long value) {
        return HolderNBT.setLongTag(chunk, key, value);
    }

    @NotNull
    public static Chunk setFloatTag(Chunk chunk, String key, float value) {
        return HolderNBT.setFloatTag(chunk, key, value);
    }

    @NotNull
    public static Chunk setDoubleTag(Chunk chunk, String key, double value) {
        return HolderNBT.setDoubleTag(chunk, key, value);
    }

    @NotNull
    public static Chunk setBooleanTag(Chunk chunk, String key, boolean value) {
        return HolderNBT.setBooleanTag(chunk, key, value);
    }

    @NotNull
    public static Chunk setByteArrayTag(Chunk chunk, String key, byte[] value) {
        return HolderNBT.setByteArrayTag(chunk, key, value);
    }

    @NotNull
    public static Chunk setIntArrayTag(Chunk chunk, String key, int[] value) {
        return HolderNBT.setIntArrayTag(chunk, key, value);
    }

    @NotNull
    public static Chunk setMaterialTag(Chunk chunk, String key, Material value) {
        return HolderNBT.setMaterialTag(chunk, key, value);
    }

    @NotNull
    public static Chunk setEntityTypeTag(Chunk chunk, String key, EntityType value) {
        return HolderNBT.setEntityTypeTag(chunk, key, value);
    }

    @NotNull
    public static Chunk setSerializableTag(Chunk chunk, String key, Serializable value) throws IOException {
        return HolderNBT.setSerializableTag(chunk, key, value);
    }

    // getters

    @Nullable
    public static String getStringTag(Chunk chunk, String key) {
        return HolderNBT.getStringTag(chunk, key);
    }

    public static byte getByteTag(Chunk chunk, String key) {
        return HolderNBT.getByteTag(chunk, key);
    }

    public static short getShortTag(Chunk chunk, String key) {
        return HolderNBT.getShortTag(chunk, key);
    }

    public static int getIntegerTag(Chunk chunk, String key) {
        return HolderNBT.getIntegerTag(chunk, key);
    }

    public static long getLongTag(Chunk chunk, String key) {
        return HolderNBT.getLongTag(chunk, key);
    }

    public static float getFloatTag(Chunk chunk, String key) {
        return HolderNBT.getFloatTag(chunk, key);
    }

    public static double getDoubleTag(Chunk chunk, String key) {
        return HolderNBT.getDoubleTag(chunk, key);
    }

    public static boolean getBooleanTag(Chunk chunk, String key) {
        return HolderNBT.getBooleanTag(chunk, key);
    }

    @NotNull
    public static byte[] getByteArrayTag(Chunk chunk, String key) {
        return HolderNBT.getByteArrayTag(chunk, key);
    }

    @NotNull
    public static int[] getIntArrayTag(Chunk chunk, String key) {
        return HolderNBT.getIntArrayTag(chunk, key);
    }

    @Nullable
    public static Material getMaterialTag(Chunk chunk, String key) {
        return HolderNBT.getMaterialTag(chunk, key);
    }

    @Nullable
    public static EntityType getEntityTypeTag(Chunk chunk, String key) {
        return HolderNBT.getEntityTypeTag(chunk, key);
    }

    @NotNull
    public static <T extends Serializable> Optional<T> getSerializableTag(Chunk chunk, String key, Class<T> clazz) {
        return HolderNBT.getSerializableTag(chunk, key, clazz);
    }

    // management

    public static boolean hasTagKey(Chunk chunk, String key) {
        return HolderNBT.hasTagKey(chunk, key);
    }

    @NotNull
    public static Chunk removeTag(Chunk chunk, String key) {
        return HolderNBT.removeTag(chunk, key);
    }

    @NotNull
    public static Chunk removeAllTags(Chunk chunk) {
        return HolderNBT.removeAllTags(chunk);
    }

    @NotNull
    public static Set<String> getKeys(Chunk chunk) {
        return HolderNBT.getKeys(chunk);
    }

    @Nullable
    public static Object getTag(Chunk chunk, String key) {
        return HolderNBT.getTag(chunk, key);
    }

    @Nullable
    public static Map<String, String> getAllStringTag(Chunk chunk) {
        return HolderNBT.getAllStringTag(chunk);
    }
}

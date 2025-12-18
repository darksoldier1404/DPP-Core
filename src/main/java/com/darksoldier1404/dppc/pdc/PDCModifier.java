package com.darksoldier1404.dppc.pdc;

import com.google.common.annotations.Beta;
import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataHolder;
import org.bukkit.persistence.PersistentDataType;

import java.io.*;
import java.util.Optional;

@Beta
public class PDCModifier {

    /**
     * Stores a basic type value (int, double, String, etc.)
     *
     * @param holder The target to store data (Entity, ItemStack, etc.)
     * @param key    NamespacedKey
     * @param type   PersistentDataType
     * @param value  The value to store
     */
    public static <T, Z> void setValue(PersistentDataHolder holder, NamespacedKey key, PersistentDataType<T, Z> type, Z value) {
        PersistentDataContainer pdc = holder.getPersistentDataContainer();
        pdc.set(key, type, value);
    }

    /**
     * Loads a basic type value (returns default if not found)
     *
     * @param holder       The target to load data from
     * @param key          NamespacedKey
     * @param type         PersistentDataType
     * @param defaultValue Default value (can be null)
     * @return The loaded value or default value
     */
    public static <T, Z> Z getValue(PersistentDataHolder holder, NamespacedKey key, PersistentDataType<T, Z> type, Z defaultValue) {
        PersistentDataContainer pdc = holder.getPersistentDataContainer();
        Z value = pdc.get(key, type);
        return value != null ? value : defaultValue;
    }

    /**
     * Serializes an object using Java Serializable and stores as byte array
     *
     * @param holder The target to store data
     * @param key    NamespacedKey
     * @param obj    The object to store (must implement Serializable)
     * @throws IOException If serialization fails
     */
    public static void setObject(PersistentDataHolder holder, NamespacedKey key, Serializable obj) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ObjectOutputStream oos = new ObjectOutputStream(baos)) {
            oos.writeObject(obj);
        }
        byte[] bytes = baos.toByteArray();
        setValue(holder, key, PersistentDataType.BYTE_ARRAY, bytes);
    }

    /**
     * Deserializes byte array to an object (specify class type)
     *
     * @param holder The target to load data from
     * @param key    NamespacedKey
     * @param clazz  The class type to deserialize to (must implement Serializable)
     * @return Optional-wrapped object (empty if not found or invalid)
     */
    public static <T extends Serializable> Optional<T> getObject(PersistentDataHolder holder, NamespacedKey key, Class<T> clazz) {
        byte[] bytes = getValue(holder, key, PersistentDataType.BYTE_ARRAY, null);
        if (bytes == null || bytes.length == 0) {
            return Optional.empty();
        }
        try (ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
             ObjectInputStream ois = new ObjectInputStream(bais)) {
            @SuppressWarnings("unchecked")
            T obj = (T) ois.readObject();
            return Optional.ofNullable(obj);
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("PDC object deserialization failed: " + e.getMessage());
            return Optional.empty();
        }
    }

    /**
     * Checks if data exists for the key
     *
     * @param holder The target
     * @param key    NamespacedKey
     * @return true if data exists
     */
    public static boolean hasData(PersistentDataHolder holder, NamespacedKey key) {
        return holder.getPersistentDataContainer().has(key, PersistentDataType.BYTE_ARRAY);  // Check with BYTE_ARRAY (common for objects)
    }

    /**
     * Removes data for the key
     *
     * @param holder The target
     * @param key    NamespacedKey
     */
    public static void removeData(PersistentDataHolder holder, NamespacedKey key) {
        holder.getPersistentDataContainer().remove(key);
    }
}
package com.darksoldier1404.dppc.utils;

import com.darksoldier1404.dppc.api.inventory.DInventory;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DInventoryManager {
    private static final Map<Plugin, Map<UUID, DInventory>> inventoryContainer = new HashMap<>();

    public static void addInventory(Plugin plugin, DInventory inventory) {
        if (!inventoryContainer.containsKey(plugin)) {
            inventoryContainer.put(plugin, new HashMap<>());
        }
        inventoryContainer.get(plugin).put(inventory.getUniqueId(), inventory);
    }

    public static void removeInventory(Plugin plugin, DInventory inventory) {
        if (inventoryContainer.containsKey(plugin)) {
            inventoryContainer.get(plugin).remove(inventory.getUniqueId());
        }
    }

    @Nullable
    public static DInventory getInventory(Plugin plugin, UUID uuid) {
        if (inventoryContainer.containsKey(plugin)) {
            return inventoryContainer.get(plugin).get(uuid);
        }
        return null;
    }

    public static boolean hasInventory(Plugin plugin, UUID uuid) {
        return inventoryContainer.containsKey(plugin) && inventoryContainer.get(plugin).containsKey(uuid);
    }

    public static boolean hasInventory(Plugin plugin, DInventory inventory) {
        return inventoryContainer.containsKey(plugin) && inventoryContainer.get(plugin).containsValue(inventory);
    }
}

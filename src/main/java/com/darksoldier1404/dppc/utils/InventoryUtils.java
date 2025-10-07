package com.darksoldier1404.dppc.utils;

import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.HashMap;
import java.util.List;

@SuppressWarnings("unused")
public class InventoryUtils {

    public static boolean mergeItem(Inventory inv, ItemStack item) {
        if (inv instanceof PlayerInventory) {
            Inventory vi = Bukkit.createInventory(null, 36);
            vi.setContents(inv.getStorageContents());
            if (vi.addItem(item).size() == 0) {
                inv.addItem(item);
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    public static boolean hasEnoughSpace(ItemStack[] content, ItemStack item) {
        if (item == null) return false;
        Inventory inv = Bukkit.createInventory(null, 36);
        inv.setContents(content);
        HashMap<Integer, ItemStack> leftover = new HashMap<>();
        leftover.putAll(inv.addItem(item));
        return leftover.isEmpty();
    }

    public static boolean hasEnoughSpace(ItemStack[] content, ItemStack... items) {
        if (items == null || items.length == 0) return false;
        for (ItemStack item : items) {
            if (item == null) return false;
        }
        Inventory inv = Bukkit.createInventory(null, 36);
        inv.setContents(content);
        HashMap<Integer, ItemStack> leftover = new HashMap<>();
        leftover.putAll(inv.addItem(items));
        return leftover.isEmpty();
    }

    public static boolean hasEnoughSpace(ItemStack[] content, List<ItemStack> items) {
        if (items == null || items.size() == 0) return false;
        for (ItemStack item : items) {
            if (item == null) return false;
        }
        Inventory inv = Bukkit.createInventory(null, 36);
        inv.setContents(content);
        HashMap<Integer, ItemStack> leftover = new HashMap<>();
        leftover.putAll(inv.addItem(items.toArray(new ItemStack[0])));
        return leftover.isEmpty();
    }

    public static int getSimlarItemCount(ItemStack[] content, ItemStack item) {
        Inventory inv = Bukkit.createInventory(null, 54);
        inv.setContents(content);
        int count = 0;
        for (ItemStack is : inv.getContents()) {
            if (is != null && is.isSimilar(item)) {
                count += is.getAmount();
            }
        }
        return count;
    }
}

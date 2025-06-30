package com.darksoldier1404.dppc.nbt;

import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;
import java.util.function.Function;

public class NBTAPI {
    private NBTAPI() {
    }

    public static ItemStack modify(ItemStack item, Consumer<NBTItem> consumer) {
        if (item == null) {
            return null;
        }
        NBTItem nbt = new NBTItem(item);
        consumer.accept(nbt);
        return nbt.getItem();
    }

    public static <T> T get(ItemStack item, Function<NBTItem, T> getter) {
        if (item == null) {
            return null;
        }
        NBTItem nbt = new NBTItem(item);
        return getter.apply(nbt);
    }
}

package com.darksoldier1404.dppc.utils.nbtapi.handler;

import com.darksoldier1404.dppc.utils.nbtapi.NBTInternal;
import org.bukkit.inventory.ItemStack;

import com.darksoldier1404.dppc.utils.nbtapi.iface.NBTHandler;
import com.darksoldier1404.dppc.utils.nbtapi.iface.ReadWriteNBT;
import com.darksoldier1404.dppc.utils.nbtapi.iface.ReadableNBT;

public class NBTHandlers {

    public static final NBTHandler<ItemStack> ITEM_STACK = new NBTHandler<ItemStack>() {

        @Override
        public boolean fuzzyMatch(Object obj) {
            return obj instanceof ItemStack;
        }

        @Override
        public void set(ReadWriteNBT nbt, String key, ItemStack value) {
            nbt.removeKey(key);
            ReadWriteNBT tag = nbt.getOrCreateCompound(key);
            tag.mergeCompound(NBTInternal.itemStackToNBT(value));
        }

        @Override
        public ItemStack get(ReadableNBT nbt, String key) {
            ReadableNBT tag = nbt.getCompound(key);
            if (tag != null) {
                return NBTInternal.itemStackFromNBT(tag);
            }
            return null;
        }

    };

    public static final NBTHandler<ReadableNBT> STORE_READABLE_TAG = new NBTHandler<ReadableNBT>() {

        @Override
        public boolean fuzzyMatch(Object obj) {
            return obj instanceof ReadableNBT;
        }

        @Override
        public void set(ReadWriteNBT nbt, String key, ReadableNBT value) {
            nbt.removeKey(key);
            nbt.getOrCreateCompound(key).mergeCompound(value);
        }

        @Override
        public ReadableNBT get(ReadableNBT nbt, String key) {
            ReadableNBT tag = nbt.getCompound(key);
            if (tag != null) {
                ReadWriteNBT value = NBTInternal.createNBTObject();
                value.mergeCompound(tag);
                return value;
            }
            return null;
        }

    };

    public static final NBTHandler<ReadWriteNBT> STORE_READWRITE_TAG = new NBTHandler<ReadWriteNBT>() {

        @Override
        public boolean fuzzyMatch(Object obj) {
            return obj instanceof ReadWriteNBT;
        }

        @Override
        public void set(ReadWriteNBT nbt, String key, ReadWriteNBT value) {
            nbt.removeKey(key);
            nbt.getOrCreateCompound(key).mergeCompound(value);
        }

        @Override
        public ReadWriteNBT get(ReadableNBT nbt, String key) {
            ReadableNBT tag = nbt.getCompound(key);
            if (tag != null) {
                ReadWriteNBT value = NBTInternal.createNBTObject();
                value.mergeCompound(tag);
                return value;
            }
            return null;
        }

    };

}

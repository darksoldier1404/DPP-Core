package com.darksoldier1404.dppc.utils.nbtapi;

import org.bukkit.Chunk;

import com.darksoldier1404.dppc.utils.nbtapi.utils.CheckUtil;
import com.darksoldier1404.dppc.utils.nbtapi.utils.MinecraftVersion;

public class NBTChunk {

    private final Chunk chunk;

    public NBTChunk(Chunk chunk) {
        this.chunk = chunk;
    }

    /**
     * Gets the NBTCompound used by spigots PersistentDataAPI. This method is only
     * available for 1.16.4+!
     * 
     * @return NBTCompound containing the data of the PersistentDataAPI
     */
    public NBTCompound getPersistentDataContainer() {
        CheckUtil.assertAvailable(MinecraftVersion.MC1_16_R3);
        return new NBTPersistentDataContainer(chunk.getPersistentDataContainer());
    }

}

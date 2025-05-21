package com.darksoldier1404.dppc.utils.nbtapi;

import com.mojang.authlib.GameProfile;

import com.darksoldier1404.dppc.utils.nbtapi.utils.GameprofileUtil;
import com.darksoldier1404.dppc.utils.nbtapi.utils.MinecraftVersion;
import com.darksoldier1404.dppc.utils.nbtapi.utils.nmsmappings.ObjectCreator;
import com.darksoldier1404.dppc.utils.nbtapi.utils.nmsmappings.ReflectionMethod;

public class NBTGameProfile {

    /**
     * Convert a GameProfile to NBT. The NBT then can be modified or be stored
     * 
     * @param profile
     * @return A NBTContainer with all the GameProfile data
     */
    @Deprecated
    public static NBTCompound toNBT(GameProfile profile) {
        if(MinecraftVersion.isAtLeastVersion(MinecraftVersion.MC1_20_R4)) {
            return (NBTCompound) GameprofileUtil.writeGameProfile(NBTInternal.createNBTObject(), profile);
        }
        return new NBTContainer(ReflectionMethod.GAMEPROFILE_SERIALIZE.run(null,
                ObjectCreator.NMS_NBTTAGCOMPOUND.getInstance(), profile));
    }

    /**
     * Reconstructs a GameProfile from a NBTCompound
     * 
     * @param compound Has to contain GameProfile data
     * @return The reconstructed GameProfile
     */
    @Deprecated
    public static GameProfile fromNBT(NBTCompound compound) {
        if(MinecraftVersion.isAtLeastVersion(MinecraftVersion.MC1_20_R4)) {
            return GameprofileUtil.readGameProfile(compound);
        }
        return (GameProfile) ReflectionMethod.GAMEPROFILE_DESERIALIZE.run(null,
                NBTReflectionUtil.getToCompount(compound.getCompound(), compound));
    }

}

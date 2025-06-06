package com.darksoldier1404.dppc.utils.nbtapi;

import com.darksoldier1404.dppc.utils.nbtapi.iface.ReadWriteNBT;
import com.darksoldier1404.dppc.utils.nbtapi.iface.ReadWriteNBTCompoundList;
import com.darksoldier1404.dppc.utils.nbtapi.iface.ReadableNBT;
import com.darksoldier1404.dppc.utils.nbtapi.utils.MinecraftVersion;
import com.darksoldier1404.dppc.utils.nbtapi.utils.nmsmappings.ClassWrapper;
import com.darksoldier1404.dppc.utils.nbtapi.utils.nmsmappings.ReflectionMethod;

/**
 * {@link NBTListCompound} implementation for NBTLists
 * 
 * @author tr7zw
 *
 */
public class NBTCompoundList extends NBTList<ReadWriteNBT> implements ReadWriteNBTCompoundList {

    protected NBTCompoundList(NBTCompound owner, String name, NBTType type, Object list) {
        super(owner, name, type, list);
    }

    /**
     * Adds a new Compound to the end of the List and returns it.
     * 
     * @return The added {@link NBTListCompound}
     */
    public NBTListCompound addCompound() {
        return (NBTListCompound) addCompound(null);
    }

    /**
     * Adds a copy of the Compound to the end of the List and returns it. When null
     * is given, a new Compound will be created
     * 
     * @param comp
     * @return
     */
    public NBTCompound addCompound(NBTCompound comp) {
        if (getParent().isReadOnly()) {
            throw new NbtApiException("Tried setting data in read only mode!");
        }
        try {
            Object compound = ClassWrapper.NMS_NBTTAGCOMPOUND.getClazz().newInstance();
            if (MinecraftVersion.getVersion().getVersionId() >= MinecraftVersion.MC1_14_R1.getVersionId()) {
                ReflectionMethod.LIST_ADD.run(listObject, size(), compound);
            } else {
                ReflectionMethod.LEGACY_LIST_ADD.run(listObject, compound);
            }
            getParent().saveCompound();
            NBTListCompound listcomp = new NBTListCompound(this, compound);
            if (comp != null) {
                listcomp.mergeCompound(comp);
            }
            return listcomp;
        } catch (Exception ex) {
            throw new NbtApiException(ex);
        }
    }

    @Override
    public ReadWriteNBT addCompound(ReadableNBT comp) {
        if (comp instanceof NBTCompound) {
            return addCompound((NBTCompound) comp);
        }
        return null;
    }

    /**
     * Adds a new Compound to the end of the List.
     * 
     * 
     * @deprecated Please use addCompound!
     * @param empty
     * @return True, if compound was added
     */
    @Override
    @Deprecated
    public boolean add(ReadWriteNBT empty) {
        return addCompound(empty) != null;
    }

    @Override
    public void add(int index, ReadWriteNBT element) {
        if (element != null) {
            throw new NbtApiException("You need to pass null! ListCompounds from other lists won't work.");
        }
        if (getParent().isReadOnly()) {
            throw new NbtApiException("Tried setting data in read only mode!");
        }
        try {
            Object compound = ClassWrapper.NMS_NBTTAGCOMPOUND.getClazz().newInstance();
            if (MinecraftVersion.getVersion().getVersionId() >= MinecraftVersion.MC1_14_R1.getVersionId()) {
                ReflectionMethod.LIST_ADD.run(listObject, index, compound);
            } else {
                ReflectionMethod.LEGACY_LIST_ADD.run(listObject, compound);
            }
            super.getParent().saveCompound();
        } catch (Exception ex) {
            throw new NbtApiException(ex);
        }
    }

    @Override
    public NBTListCompound get(int index) {
        try {
            Object compound = ReflectionMethod.LIST_GET_COMPOUND.run(listObject, index);
            return new NBTListCompound(this, compound);
        } catch (Exception ex) {
            throw new NbtApiException(ex);
        }
    }

    @Override
    public NBTListCompound set(int index, ReadWriteNBT element) {
        throw new NbtApiException("This method doesn't work in the ListCompound context.");
    }

    @Override
    protected Object asTag(ReadWriteNBT object) {
        return null;
    }

}

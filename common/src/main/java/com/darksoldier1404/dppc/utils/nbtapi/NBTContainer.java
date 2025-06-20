package com.darksoldier1404.dppc.utils.nbtapi;

import com.darksoldier1404.dppc.utils.nbtapi.utils.nmsmappings.ClassWrapper;
import com.darksoldier1404.dppc.utils.nbtapi.utils.nmsmappings.ObjectCreator;
import com.darksoldier1404.dppc.utils.nbtapi.utils.nmsmappings.ReflectionMethod;

import java.io.InputStream;

/**
 * A Standalone {@link NBTCompound} implementation. All data is just kept inside
 * this Object.
 * 
 * @author tr7zw
 *
 */
public class NBTContainer extends NBTCompound {

    private Object nbt;
    private boolean closed;
    private boolean readOnly;

    @Deprecated
    public NBTContainer() {
        super(null, null);
        nbt = ObjectCreator.NMS_NBTTAGCOMPOUND.getInstance();
    }

    /**
     * Takes in any NMS Compound to wrap it
     * 
     * @param nbt
     * @deprecated Use NBT.wrapNMSTag
     */
    @Deprecated
    public NBTContainer(Object nbt) {
        super(null, null);
        if (nbt == null) {
            nbt = ObjectCreator.NMS_NBTTAGCOMPOUND.getInstance();
        }
        if (!ClassWrapper.NMS_NBTTAGCOMPOUND.getClazz().isAssignableFrom(nbt.getClass())) {
            throw new NbtApiException("The object '" + nbt.getClass() + "' is not a valid NBT-Object!");
        }
        this.nbt = nbt;
    }

    /**
     * Reads in a NBT InputStream
     * 
     * @param inputsteam
     * @deprecated Use NBT.readNBT
     */
    @Deprecated
    public NBTContainer(InputStream inputsteam) {
        super(null, null);
        this.nbt = NBTReflectionUtil.readNBT(inputsteam);
    }

    /**
     * Parses in a NBT String to a standalone {@link NBTCompound}. Can throw a
     * {@link NbtApiException} in case something goes wrong.
     * 
     * @param nbtString
     * @deprecated Use NBT.parseNBT
     */
    @Deprecated
    public NBTContainer(String nbtString) {
        super(null, null);
        if (nbtString == null) {
            throw new NullPointerException("The String can't be null!");
        }
        try {
            nbt = ReflectionMethod.PARSE_NBT.run(null, nbtString);
        } catch (Exception ex) {
            throw new NbtApiException("Unable to parse Malformed Json!", ex);
        }
    }

    @Override
    public Object getCompound() {
        return nbt;
    }

    @Override
    public void setCompound(Object tag) {
        nbt = tag;
    }

    @Override
    protected void setClosed() {
        this.closed = true;
    }

    @Override
    protected boolean isClosed() {
        return closed;
    }
    
    protected boolean isReadOnly() {
        return readOnly;
    }

    protected NBTContainer setReadOnly(boolean readOnly) {
        this.readOnly = true;
        return this;
    }

}

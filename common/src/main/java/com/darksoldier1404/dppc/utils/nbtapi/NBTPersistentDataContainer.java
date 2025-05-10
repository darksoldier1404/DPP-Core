package com.darksoldier1404.dppc.utils.nbtapi;

import com.darksoldier1404.dppc.utils.nbtapi.utils.nmsmappings.ReflectionMethod;
import org.bukkit.persistence.PersistentDataContainer;

import java.util.Map;

public class NBTPersistentDataContainer extends NBTCompound {

    private final PersistentDataContainer container;

    public NBTPersistentDataContainer(PersistentDataContainer container) {
        super(null, null);
        this.container = container;
    }

    @Override
    public Object getCompound() {
        return ReflectionMethod.CRAFT_PERSISTENT_DATA_CONTAINER_TO_TAG.run(container);
    }

    @Override
    protected void setCompound(Object compound) {
        @SuppressWarnings("unchecked")
        Map<Object, Object> map = (Map<Object, Object>) ReflectionMethod.CRAFT_PERSISTENT_DATA_CONTAINER_GET_MAP
                .run(container);
        map.clear();
        ReflectionMethod.CRAFT_PERSISTENT_DATA_CONTAINER_PUT_ALL.run(container, compound);
    }

}

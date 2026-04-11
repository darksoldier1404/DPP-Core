package com.darksoldier1404.dppc.data;

import com.darksoldier1404.dppc.annotation.DPPCoreVersion;
import org.jetbrains.annotations.NotNull;

@DPPCoreVersion(since = "5.4.0")
public interface Creatable<K, T> {
    T create(@NotNull K key, @NotNull Class<T> clazz);

    T createAndSave(@NotNull K key, @NotNull Class<T> clazz);
}

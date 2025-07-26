package com.darksoldier1404.dppc.data;

import org.bukkit.configuration.file.YamlConfiguration;

public interface DataCargo {
    void save();

    Object load(YamlConfiguration data);
}

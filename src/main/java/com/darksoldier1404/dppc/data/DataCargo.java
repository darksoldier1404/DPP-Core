package com.darksoldier1404.dppc.data;

import org.bukkit.configuration.file.YamlConfiguration;

public interface DataCargo {
    Object serialize();

    Object deserialize(YamlConfiguration data);
}

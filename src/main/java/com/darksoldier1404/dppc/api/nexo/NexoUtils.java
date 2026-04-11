package com.darksoldier1404.dppc.api.nexo;

import com.darksoldier1404.dppc.annotation.DPPCoreVersion;
import com.darksoldier1404.dppc.annotation.RequirePaper;
import com.google.common.annotations.Beta;
import com.nexomc.nexo.api.NexoFurniture;
import com.nexomc.nexo.api.NexoItems;
import com.nexomc.nexo.items.ItemBuilder;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;

@Beta
@DPPCoreVersion(since = "5.4.0")
@RequirePaper
public class NexoUtils {
    public static boolean isNexoItem(ItemStack item) {
        return NexoItems.exists(item);
    }

    public static String getNexoItemID(ItemStack item) {
        return NexoItems.idFromItem(item);
    }

    public static String getDisplayName(ItemStack item) {
        String name = item.getItemMeta() != null && item.getItemMeta().hasDisplayName() ? item.getItemMeta().getDisplayName() : "";
        ItemBuilder ib = NexoItems.builderFromItem(item);
        if (ib == null) {
            return name;
        }
        TextComponent tc = (TextComponent) ib.getItemName();
        if (tc == null) {
            return name;
        }
        name = tc.content();
        return name;
    }

    public static boolean isNexoFurniture(Entity entity) {
        return NexoFurniture.isFurniture(entity);
    }

    public static boolean isNexoFurniture(Location location) {
        return NexoFurniture.isFurniture(location);
    }

    public static boolean isNexoFurnitureItem(ItemStack item) {
        return NexoFurniture.isFurniture(item);
    }
}

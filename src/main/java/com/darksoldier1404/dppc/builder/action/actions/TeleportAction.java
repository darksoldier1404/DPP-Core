package com.darksoldier1404.dppc.builder.action.actions;

import com.darksoldier1404.dppc.builder.action.obj.Action;
import com.darksoldier1404.dppc.builder.action.obj.ActionContext;
import com.darksoldier1404.dppc.builder.action.obj.ActionType;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

public class TeleportAction implements Action {
    private final String worldName;
    private final double x;
    private final double y;
    private final double z;

    public TeleportAction(String worldName, double x, double y, double z) {
        this.worldName = worldName;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public void execute(ActionContext context) {
        World world = Bukkit.getWorld(context.applyVariables(worldName));
        if (world != null && context.getPlayer().isOnline()) {
            context.getPlayer().teleport(new Location(world, x, y, z));
        }
    }

    @Override
    public ActionType getActionType() {
        return ActionType.TELEPORT;
    }

    @Override
    public String serialize() {
        return String.format("teleport %s %.2f,%.2f,%.2f", worldName, x, y, z);
    }

    public static TeleportAction parse(String line) {
        String[] parts = line.split("\\s+");
        if (parts.length != 3 || !parts[0].equalsIgnoreCase("teleport")) return null;
        try {
            String[] coords = parts[2].split(",");
            if (coords.length != 3) return null;
            return new TeleportAction(parts[1],
                    Double.parseDouble(coords[0]),
                    Double.parseDouble(coords[1]),
                    Double.parseDouble(coords[2]));
        } catch (NumberFormatException e) {
            return null;
        }
    }
}

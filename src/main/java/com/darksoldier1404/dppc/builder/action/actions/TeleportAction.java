package com.darksoldier1404.dppc.builder.action.actions;

import com.darksoldier1404.dppc.builder.action.obj.Action;
import com.darksoldier1404.dppc.builder.action.obj.ActionType;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

/**
 * The type Teleport action.
 */
public class TeleportAction implements Action {
    private final String worldName;
    private final double x;
    private final double y;
    private final double z;

    /**
     * Instantiates a new Teleport action.
     *
     * @param worldName the world name
     * @param x         the x
     * @param y         the y
     * @param z         the z
     */
    public TeleportAction(String worldName, double x, double y, double z) {
        this.worldName = worldName;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public void execute(Player player) {
        World world = Bukkit.getWorld(worldName);
        if (world != null && player.isOnline()) {
            Location loc = new Location(world, x, y, z);
            player.teleport(loc);
        }
    }

    @Override
    public ActionType getActionTypeName() {
        return ActionType.TELEPORT_ACTION;
    }

    @Override
    public String serialize() {
        return String.format("teleport %s %.1f,%.1f,%.1f", worldName, x, y, z);
    }

    /**
     * Parse teleport action.
     *
     * @param line the line
     * @return the teleport action
     */
    public static TeleportAction parse(String line) {
        String[] parts = line.split("\\s+");
        if (parts.length != 3 || !parts[0].equalsIgnoreCase("teleport")) {
            return null;
        }
        try {
            String worldName = parts[1];
            String[] coords = parts[2].split(",");
            if (coords.length != 3) {
                return null;
            }
            double x = Double.parseDouble(coords[0]);
            double y = Double.parseDouble(coords[1]);
            double z = Double.parseDouble(coords[2]);
            return new TeleportAction(worldName, x, y, z);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
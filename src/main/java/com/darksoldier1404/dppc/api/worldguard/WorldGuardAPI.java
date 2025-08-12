package com.darksoldier1404.dppc.api.worldguard;

import com.darksoldier1404.dppc.DPPCore;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.managers.storage.StorageException;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
public class WorldGuardAPI {
    private static final DPPCore plugin = DPPCore.getInstance();
    private static final WorldGuard wg = WorldGuard.getInstance();
    private static final RegionContainer rc = wg.getPlatform().getRegionContainer();

    @Nullable
    public static RegionManager getRegionManager(World world) {
        return rc.get(new BukkitWorld(world));
    }

    @Nullable
    public static RegionManager getRegionManager(String worldName) {
        World world = Bukkit.getWorld(worldName);
        return (world != null) ? getRegionManager(world) : null;
    }

    public static boolean isRegion(World world, String regionName) {
        return isRegion(getRegionManager(world), regionName);
    }

    public static boolean isRegion(String worldName, String regionName) {
        return isRegion(getRegionManager(worldName), regionName);
    }

    private static boolean isRegion(@Nullable RegionManager rm, String regionName) {
        return rm != null && rm.hasRegion(regionName);
    }

    public static boolean isPlayerInRegion(Player player, String regionName) {
        World world = player.getWorld();
        RegionManager rm = getRegionManager(world);
        if (rm == null) return false;

        ApplicableRegionSet regions = rm.getApplicableRegions(BukkitAdapter.asBlockVector(player.getLocation()));
        return regions.getRegions().stream().anyMatch(region -> region.getId().equalsIgnoreCase(regionName));
    }

    public static boolean isPlayerRegionOwner(World world, String regionName, Player player) {
        RegionManager rm = getRegionManager(world);
        if (rm == null || !rm.hasRegion(regionName)) return false;

        ProtectedRegion region = rm.getRegion(regionName);
        return region != null && region.getOwners().contains(player.getUniqueId());
    }

    public static boolean isPlayerRegionMember(World world, String regionName, Player player) {
        RegionManager rm = getRegionManager(world);
        if (rm == null || !rm.hasRegion(regionName)) return false;

        ProtectedRegion region = rm.getRegion(regionName);
        return region != null && region.getMembers().contains(player.getUniqueId());
    }

    public static boolean addRegionOwner(World world, String regionName, Player player) {
        RegionManager rm = getRegionManager(world);
        if (rm == null || !rm.hasRegion(regionName)) return false;

        ProtectedRegion region = rm.getRegion(regionName);
        if (region == null) return false;

        region.getOwners().addPlayer(player.getUniqueId());
        try {
            rm.save();
            return true;
        } catch (StorageException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean removeRegionOwner(World world, String regionName, Player player) {
        RegionManager rm = getRegionManager(world);
        if (rm == null || !rm.hasRegion(regionName)) return false;

        ProtectedRegion region = rm.getRegion(regionName);
        if (region == null) return false;

        region.getOwners().removePlayer(player.getUniqueId());
        try {
            rm.save();
            return true;
        } catch (StorageException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static List<String> getAllRegions(World world) {
        RegionManager rm = getRegionManager(world);
        if (rm == null) return Collections.emptyList();

        return new ArrayList<>(rm.getRegions().keySet());
    }

    public static List<String> getChildRegions(World world, String regionName) {
        RegionManager rm = getRegionManager(world);
        if (rm == null || !rm.hasRegion(regionName)) return Collections.emptyList();

        return rm.getRegions().values().stream()
                .filter(region -> regionName.equals(region.getParent() != null ? region.getParent().getId() : null))
                .map(ProtectedRegion::getId)
                .collect(Collectors.toList());
    }

    public static int getRegionAreaVolume(World world, String regionName) {
        RegionManager rm = getRegionManager(world);
        if (rm == null || !rm.hasRegion(regionName)) return 0;

        ProtectedRegion region = rm.getRegion(regionName);
        if (region == null) return 0;

        BlockVector3 min = region.getMinimumPoint();
        BlockVector3 max = region.getMaximumPoint();

        int xSize = max.getX() - min.getX() + 1;
        int ySize = max.getY() - min.getY() + 1;
        int zSize = max.getZ() - min.getZ() + 1;

        return xSize * ySize * zSize;
    }

    public static boolean isBlockInRegion(World world, String regionName, Location location) {
        RegionManager rm = getRegionManager(world);
        if (rm == null || !rm.hasRegion(regionName)) return false;

        ProtectedRegion region = rm.getRegion(regionName);
        return region != null && region.contains(location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }

    public static List<Player> getPlayersInRegion(World world, String regionName) {
        RegionManager rm = getRegionManager(world);
        if (rm == null || !rm.hasRegion(regionName)) return Collections.emptyList();

        ProtectedRegion region = rm.getRegion(regionName);
        if (region == null) return Collections.emptyList();

        return Bukkit.getOnlinePlayers().stream()
                .filter(player -> region.contains(player.getLocation().getBlockX(),
                        player.getLocation().getBlockY(),
                        player.getLocation().getBlockZ()))
                .collect(Collectors.toList());
    }
}
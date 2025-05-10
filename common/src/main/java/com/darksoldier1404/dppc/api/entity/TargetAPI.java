package com.darksoldier1404.dppc.api.entity;

import com.darksoldier1404.dppc.DPPCore;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@SuppressWarnings("all")
public class TargetAPI {
    private static final DPPCore plugin = DPPCore.getInstance();

    @Nullable
    public static Entity getTargetedEntity(Entity from) {
        if(from instanceof Player){
            Player p = (Player) from;
            Vector v = p.getEyeLocation().getDirection();
            return p.getWorld().getEntities().stream().filter(e -> e.getLocation().distance(p.getEyeLocation()) <= v.length()).findFirst().orElse(null);
        }
        return null;
    }

    @Nullable
    public static Entity getTargetedEntity(Entity from, double maxDistance) {
        if(from instanceof Player){
            Player p = (Player) from;
            Vector v = p.getEyeLocation().getDirection();
            return p.getWorld().getEntities().stream().filter(e -> e.getLocation().distance(p.getEyeLocation()) <= v.length() && e.getLocation().distance(p.getEyeLocation()) <= maxDistance).findFirst().orElse(null);
        }
        return null;
    }

    @Nullable
    public static Entity getNearestTargetFromList(Entity center, double maxDistance) {
        if (center == null) return null;
        List<Entity> targets = center.getNearbyEntities(maxDistance, maxDistance, maxDistance);
        if (targets.isEmpty()) return null;
        Entity nearestTarget = null;
        double nearestDistance = maxDistance;
        for (Entity target : targets) {
            double distance = center.getLocation().distance(target.getLocation());
            if (distance < nearestDistance) {
                nearestDistance = distance;
                nearestTarget = target;
            }
        }
        return nearestTarget;
    }

    @Nullable
    public static Entity getNearestTargetFromList(Entity center, double maxX, double maxY, double maxZ, double maxDistance) {
        if (center == null) return null;
        List<Entity> targets = center.getNearbyEntities(maxX, maxY, maxZ);
        if (targets.isEmpty()) return null;
        Entity nearestTarget = null;
        double nearestDistance = maxDistance;
        for (Entity target : targets) {
            double distance = center.getLocation().distance(target.getLocation());
            if (distance < nearestDistance) {
                nearestDistance = distance;
                nearestTarget = target;
            }
        }
        return nearestTarget;
    }

    @Nullable
    public static Entity getNearestTargetFromList(Entity center, List<Entity> targets, double maxDistance) {
        if (center == null) return null;
        if (targets.isEmpty()) return null;
        Entity nearestTarget = null;
        double nearestDistance = maxDistance;
        for (Entity target : targets) {
            double distance = center.getLocation().distance(target.getLocation());
            if (distance < nearestDistance) {
                nearestDistance = distance;
                nearestTarget = target;
            }
        }
        return nearestTarget;
    }

    public static double getDistanceBTAC(Entity center, Entity target) {
        if (center == null || target == null) return 0;
        return center.getLocation().distance(target.getLocation());
    }


}

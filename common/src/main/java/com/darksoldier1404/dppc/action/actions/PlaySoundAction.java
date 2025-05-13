package com.darksoldier1404.dppc.action.actions;

import com.darksoldier1404.dppc.action.obj.Action;
import com.darksoldier1404.dppc.action.obj.ActionName;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class PlaySoundAction implements Action {
    private final String soundName;
    private final float volume;
    private final float pitch;
    private final String worldName;
    private final Object target;

    public PlaySoundAction(String soundName, float volume, float pitch, String worldName, Object target) {
        this.soundName = soundName;
        this.volume = volume;
        this.pitch = pitch;
        this.worldName = worldName;
        this.target = target;
    }

    @Override
    public void execute(Player player) {
        try {
            Sound sound = Sound.valueOf(soundName.toUpperCase());
            World world = Bukkit.getWorld(worldName);
            if (world == null) {
                return;
            }
            if (target instanceof double[]) {
                double[] coords = (double[]) target;
                Location loc = new Location(world, coords[0], coords[1], coords[2]);
                world.playSound(loc, sound, volume, pitch);
            } else {
                String playerName = target.toString().replace("{player}", player.getName());
                Player targetPlayer = Bukkit.getPlayerExact(playerName);
                if (targetPlayer != null && targetPlayer.isOnline()) {
                    targetPlayer.playSound(targetPlayer.getLocation(), sound, volume, pitch);
                }
            }
        } catch (IllegalArgumentException e) {
        }
    }

    @Override
    public ActionName getActionName() {
        return ActionName.PLAY_SOUND_ACTION;
    }

    @Override
    public String serialize() {
        String targetString;
        if (target instanceof double[]) {
            double[] coords = (double[]) target;
            targetString = String.format("%.1f,%.1f,%.1f", coords[0], coords[1], coords[2]);
        } else {
            targetString = target.toString();
        }
        return String.format("playsound %s %s %s %s %s", soundName, volume, pitch, worldName, targetString);
    }

    public static PlaySoundAction parse(String line) {
        String[] parts = line.split("\\s+");
        if (parts.length != 6 || !parts[0].equalsIgnoreCase("playsound")) {
            return null;
        }
        try {
            String soundName = parts[1];
            float volume = Float.parseFloat(parts[2]);
            float pitch = Float.parseFloat(parts[3]);
            String worldName = parts[4];
            String target = parts[5];
            Object parsedTarget = parseTarget(target);
            if (parsedTarget == null) {
                return null;
            }
            return new PlaySoundAction(soundName, volume, pitch, worldName, parsedTarget);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private static Object parseTarget(String target) {
        if (target.matches("-?\\d+(\\.\\d+)?,-?\\d+(\\.\\d+)?,-?\\d+(\\.\\d+)?")) {
            String[] coords = target.split(",");
            try {
                double x = Double.parseDouble(coords[0]);
                double y = Double.parseDouble(coords[1]);
                double z = Double.parseDouble(coords[2]);
                return new double[]{x, y, z};
            } catch (NumberFormatException e) {
                return null;
            }
        } else {
            return target;
        }
    }
}

package com.darksoldier1404.dppc.builder.action.actions;

import com.darksoldier1404.dppc.builder.action.obj.Action;
import com.darksoldier1404.dppc.builder.action.obj.ActionContext;
import com.darksoldier1404.dppc.builder.action.obj.ActionType;
import org.bukkit.Particle;

public class PlayParticleAction implements Action {
    private final String particleName;
    private final int count;
    private final double offsetX;
    private final double offsetY;
    private final double offsetZ;

    public PlayParticleAction(String particleName, int count, double offsetX, double offsetY, double offsetZ) {
        this.particleName = particleName.toUpperCase();
        this.count = count;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.offsetZ = offsetZ;
    }

    @Override
    public void execute(ActionContext context) {
        try {
            Particle particle = Particle.valueOf(particleName);
            context.getPlayer().getWorld().spawnParticle(
                    particle,
                    context.getPlayer().getLocation(),
                    count,
                    offsetX, offsetY, offsetZ);
        } catch (IllegalArgumentException ignored) {
        }
    }

    @Override
    public ActionType getActionType() {
        return ActionType.PLAY_PARTICLE;
    }

    @Override
    public String serialize() {
        return String.format("play_particle %s %d %.2f %.2f %.2f", particleName, count, offsetX, offsetY, offsetZ);
    }

    /**
     * Format: play_particle <particle> <count> [offsetX] [offsetY] [offsetZ]
     */
    public static PlayParticleAction parse(String line) {
        String[] parts = line.split("\\s+");
        if (parts.length < 3 || !parts[0].equalsIgnoreCase("play_particle")) return null;
        try {
            String name = parts[1];
            int count = Integer.parseInt(parts[2]);
            double ox = parts.length > 3 ? Double.parseDouble(parts[3]) : 0.5;
            double oy = parts.length > 4 ? Double.parseDouble(parts[4]) : 0.5;
            double oz = parts.length > 5 ? Double.parseDouble(parts[5]) : 0.5;
            return new PlayParticleAction(name, count, ox, oy, oz);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}

package com.darksoldier1404.dppc.api.vfx;

import com.darksoldier1404.dppc.modbridge.ModBridge;
import com.darksoldier1404.dppc.modbridge.ServerChannel;
import com.darksoldier1404.dppmc.vfx.effect.BeamSpec;
import com.darksoldier1404.dppmc.vfx.effect.EffectSpec;
import com.darksoldier1404.dppmc.vfx.effect.EmitterSpec;
import com.darksoldier1404.dppmc.vfx.protocol.VfxProtocol;
import java.util.Objects;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Particle effects on DPP-ModCore clients (DPP-ModCore 1.13.0): emitters, beams and layered effects, drawn by the
 * player's client from the spec sent here. DPP-Core owns the {@code dppmc_vfx} channel, so every plugin sends through
 * this class.
 *
 * <p>An effect at a location goes to the players in its world within {@link #DEFAULT_RADIUS} blocks whose client draws
 * effects; the others — vanilla clients, a core before 1.13.0 — are skipped, so calling it costs nothing for them.
 * Main thread, like the rest of Bukkit.
 *
 * <pre>{@code
 * VfxAPI.emitter()
 *         .shape(ShapeSpec.sphere(0.5f)).count(200).lifetime(40).speed(0.2f)
 *         .gradient(Easings.LINEAR, 0xFFCC33, 0xFF3300)
 *         .play(location);
 * VfxAPI.beam().color(0x88CCFF).play(from, to);
 * }</pre>
 */
public final class VfxAPI {
    /** How far from an effect a player still gets it: the client draws to 96 blocks, plus a margin for walking in. */
    public static final double DEFAULT_RADIUS = 112.0;

    private static ServerChannel channel;

    private VfxAPI() {
    }

    /** Called by DPP-Core's onEnable, after the mod bridge. */
    public static synchronized void enable(JavaPlugin core) {
        if (channel == null) {
            channel = ModBridge.bind(core, VfxProtocol.SPEC);
        }
    }

    /** Called by DPP-Core's onDisable, before the mod bridge. */
    public static synchronized void disable() {
        channel = null;
    }

    public static boolean isEnabled() {
        return channel != null;
    }

    /** Whether this player's client draws the effects sent to it. */
    public static boolean canSee(Player player) {
        ServerChannel current = channel;
        return current != null && player != null && current.isReady(player);
    }

    /** An emitter starting from the engine's default burst. */
    public static VfxEmitter emitter() {
        return new VfxEmitter();
    }

    /** A beam starting from the engine's default bolt. */
    public static VfxBeam beam() {
        return new VfxBeam();
    }

    /** @return how many players it was sent to */
    public static int emit(Location at, EmitterSpec spec) {
        return emit(at, spec, DEFAULT_RADIUS);
    }

    public static int emit(Location at, EmitterSpec spec, double radius) {
        Objects.requireNonNull(spec, "spec");
        return broadcast(at, radius, new VfxProtocol.Emit(at.getX(), at.getY(), at.getZ(), spec));
    }

    /** @return false when the player's client does not draw effects */
    public static boolean emit(Player player, Location at, EmitterSpec spec) {
        return send(player, new VfxProtocol.Emit(at.getX(), at.getY(), at.getZ(), Objects.requireNonNull(spec, "spec")));
    }

    public static int effect(Location at, EffectSpec spec) {
        return effect(at, spec, DEFAULT_RADIUS);
    }

    public static int effect(Location at, EffectSpec spec, double radius) {
        Objects.requireNonNull(spec, "spec");
        return broadcast(at, radius, new VfxProtocol.Effect(at.getX(), at.getY(), at.getZ(), spec));
    }

    public static boolean effect(Player player, Location at, EffectSpec spec) {
        return send(player, new VfxProtocol.Effect(at.getX(), at.getY(), at.getZ(), Objects.requireNonNull(spec, "spec")));
    }

    /** A beam, sent to the players near its start. Both ends must be in the same world. */
    public static int beam(Location from, Location to, BeamSpec spec) {
        return beam(from, to, spec, DEFAULT_RADIUS);
    }

    public static int beam(Location from, Location to, BeamSpec spec, double radius) {
        return broadcast(from, radius, beamPacket(from, to, spec));
    }

    public static boolean beam(Player player, Location from, Location to, BeamSpec spec) {
        return send(player, beamPacket(from, to, spec));
    }

    /** Removes every effect this server started on the player's client; the client's own mods keep theirs. */
    public static boolean clear(Player player) {
        return send(player, new VfxProtocol.Clear());
    }

    private static VfxProtocol.Beam beamPacket(Location from, Location to, BeamSpec spec) {
        Objects.requireNonNull(spec, "spec");
        if (!Objects.equals(from.getWorld(), to.getWorld())) {
            throw new IllegalArgumentException("a beam's ends must be in the same world");
        }
        return new VfxProtocol.Beam(from.getX(), from.getY(), from.getZ(), to.getX(), to.getY(), to.getZ(), spec);
    }

    private static boolean send(Player player, Object packet) {
        ServerChannel current = channel;
        return current != null && player != null && current.send(player, packet);
    }

    private static int broadcast(Location at, double radius, Object packet) {
        ServerChannel current = channel;
        World world = at.getWorld();
        if (current == null || world == null) {
            return 0;
        }
        double radiusSq = radius * radius;
        int sent = 0;
        for (Player player : world.getPlayers()) {
            if (player.getLocation().distanceSquared(at) <= radiusSq && current.send(player, packet)) {
                sent++;
            }
        }
        return sent;
    }
}

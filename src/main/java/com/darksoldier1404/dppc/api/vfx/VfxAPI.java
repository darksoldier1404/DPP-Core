package com.darksoldier1404.dppc.api.vfx;

import com.darksoldier1404.dppc.modbridge.ModBridge;
import com.darksoldier1404.dppc.modbridge.ServerChannel;
import com.darksoldier1404.dppmc.vfx.effect.BeamSpec;
import com.darksoldier1404.dppmc.vfx.effect.EffectSpec;
import com.darksoldier1404.dppmc.vfx.effect.EmitterSpec;
import com.darksoldier1404.dppmc.vfx.protocol.VfxProtocol;
import com.darksoldier1404.dppmc.vfx.effect.SdfSpec;
import com.darksoldier1404.dppmc.vfx.protocol.EffectJson;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;
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

    /** How far from a shake's centre a player still feels it. */
    public static final double SHAKE_RADIUS = 48.0;

    private static ServerChannel channel;
    private static JavaPlugin core;
    private static final Map<String, EffectSpec> EFFECTS = new ConcurrentHashMap<>();

    private VfxAPI() {
    }

    /** Called by DPP-Core's onEnable, after the mod bridge. */
    public static synchronized void enable(JavaPlugin core) {
        VfxAPI.core = core;
        if (channel == null) {
            channel = ModBridge.bind(core, VfxProtocol.SPEC);
            // the DPP-ModCore editor uploads effects (1.14.0, protocol 3); the answer goes back to the uploader
            channel.on(VfxProtocol.Upload.class, (player, upload) -> send(player, handleUpload(player, upload)));
        }
    }

    /** The permission an editor upload needs; default op. */
    public static final String EDIT_PERMISSION = "dppc.vfx.edit";

    /**
     * Stores one editor upload under {@code plugins/DPP-Core/vfx} and, when stored, registers it so {@link #play}
     * finds it at once. Logs who uploaded what and how it went.
     */
    static VfxProtocol.UploadResult handleUpload(Player player, VfxProtocol.Upload upload) {
        JavaPlugin plugin = core;
        if (plugin == null) {
            return new VfxProtocol.UploadResult(upload.id(), VfxProtocol.UploadResult.Status.UNSUPPORTED, "");
        }
        VfxProtocol.UploadResult result = VfxUploads.handle(new File(plugin.getDataFolder(), "vfx"),
                player.hasPermission(EDIT_PERMISSION), upload);
        if (result.status() == VfxProtocol.UploadResult.Status.OK) {
            registerEffect(upload.id(), upload.json());
        }
        plugin.getLogger().info("vfx upload by " + player.getName() + ": " + upload.id() + " -> " + result.status());
        return result;
    }

    /** Called by DPP-Core's onDisable, before the mod bridge. */
    public static synchronized void disable() {
        channel = null;
        core = null;
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

    /**
     * A translucent energy dome standing on the location, {@code radius} blocks across, for {@code duration} ticks.
     *
     * @param rgb the colour, {@code 0xRRGGBB}
     * @return how many players it was sent to
     * @throws IllegalArgumentException for a radius that is not positive or a duration under one tick
     */
    public static int dome(Location at, float radius, int rgb, int duration) {
        return broadcast(at, DEFAULT_RADIUS, domePacket(at, radius, rgb, duration));
    }

    public static boolean dome(Player player, Location at, float radius, int rgb, int duration) {
        return send(player, domePacket(at, radius, rgb, duration));
    }

    /**
     * A pool of light on the ground under the location (found within 8 blocks below it), fading in and out over
     * {@code duration} ticks. It does not light the world.
     *
     * @throws IllegalArgumentException for a radius that is not positive or a duration under one tick
     */
    public static int light(Location at, int rgb, float radius, int duration) {
        return broadcast(at, DEFAULT_RADIUS, lightPacket(at, rgb, radius, duration));
    }

    public static boolean light(Player player, Location at, int rgb, float radius, int duration) {
        return send(player, lightPacket(at, rgb, radius, duration));
    }

    /**
     * Shakes the cameras of the players within {@link #SHAKE_RADIUS} blocks of the location: up to {@code magnitude}
     * degrees of yaw and pitch, fading over {@code duration} ticks.
     *
     * @throws IllegalArgumentException for a magnitude that is not positive or a duration under one tick
     */
    public static int shake(Location center, float magnitude, int duration) {
        return broadcast(center, SHAKE_RADIUS, shakePacket(magnitude, duration));
    }

    public static boolean shake(Player player, float magnitude, int duration) {
        return send(player, shakePacket(magnitude, duration));
    }

    /** A raymarched signed-distance volume at the location; needs a client with {@code Capability.VFX_SDF}. */
    public static int sdf(Location at, SdfSpec spec) {
        return broadcast(at, DEFAULT_RADIUS, sdfPacket(at, spec));
    }

    public static boolean sdf(Player player, Location at, SdfSpec spec) {
        return send(player, sdfPacket(at, spec));
    }

    /**
     * Registers an effect written as JSON (the format of {@link EffectJson}) under an id, replacing one of the same id.
     *
     * @throws IllegalArgumentException when the JSON does not parse; the message names the field
     */
    public static void registerEffect(String id, String json) {
        Objects.requireNonNull(id, "id");
        EFFECTS.put(id, EffectJson.parse(Objects.requireNonNull(json, "json")));
    }

    /** The ids of the registered JSON effects. */
    public static Set<String> effectIds() {
        return new TreeSet<>(EFFECTS.keySet());
    }

    /** Forgets every registered JSON effect. */
    public static void clearEffects() {
        EFFECTS.clear();
    }

    /**
     * Replaces the registered JSON effects with the ones in a folder: {@code <folder>/a/b.json} is the effect
     * {@code a/b}. A file that does not parse is logged and skipped. A folder that does not exist loads nothing.
     *
     * @return how many effects loaded
     */
    public static int loadEffects(File folder) {
        EFFECTS.clear();
        if (folder == null || !folder.isDirectory()) {
            return 0;
        }
        int loaded = 0;
        Path root = folder.toPath();
        try (Stream<Path> files = Files.walk(root)) {
            for (Path file : (Iterable<Path>) files.filter(p -> p.toString().endsWith(".json")).sorted()::iterator) {
                String id = root.relativize(file).toString().replace(File.separatorChar, '/');
                id = id.substring(0, id.length() - ".json".length());
                try {
                    EFFECTS.put(id, EffectJson.parse(Files.readString(file, StandardCharsets.UTF_8)));
                    loaded++;
                } catch (IllegalArgumentException | IOException e) {
                    warn("vfx effect " + id + " skipped: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            warn("could not read the vfx folder " + folder + ": " + e.getMessage());
        }
        return loaded;
    }

    /**
     * Plays a registered JSON effect at the location.
     *
     * @return how many players it was sent to; 0 for an id that is not registered
     */
    public static int play(Location at, String id) {
        EffectSpec spec = EFFECTS.get(id);
        return spec == null ? 0 : effect(at, spec);
    }

    /** @return false for an id that is not registered, or when the player's client does not draw effects */
    public static boolean play(Player player, Location at, String id) {
        EffectSpec spec = EFFECTS.get(id);
        return spec != null && effect(player, at, spec);
    }

    /** Removes every effect this server started on the player's client; the client's own mods keep theirs. */
    public static boolean clear(Player player) {
        return send(player, new VfxProtocol.Clear());
    }

    private static VfxProtocol.Dome domePacket(Location at, float radius, int rgb, int duration) {
        requireEffect(radius, duration);
        return new VfxProtocol.Dome(at.getX(), at.getY(), at.getZ(), radius, rgb, duration);
    }

    private static VfxProtocol.Light lightPacket(Location at, int rgb, float radius, int duration) {
        requireEffect(radius, duration);
        return new VfxProtocol.Light(at.getX(), at.getY(), at.getZ(), rgb, radius, duration);
    }

    private static VfxProtocol.Shake shakePacket(float magnitude, int duration) {
        requireEffect(magnitude, duration);
        return new VfxProtocol.Shake(magnitude, duration);
    }

    private static VfxProtocol.Sdf sdfPacket(Location at, SdfSpec spec) {
        return new VfxProtocol.Sdf(at.getX(), at.getY(), at.getZ(), Objects.requireNonNull(spec, "spec"));
    }

    private static void requireEffect(float size, int duration) {
        if (!(size > 0f)) {
            throw new IllegalArgumentException("radius or magnitude must be > 0: " + size);
        }
        if (duration < 1) {
            throw new IllegalArgumentException("duration must be >= 1: " + duration);
        }
    }

    private static void warn(String message) {
        JavaPlugin plugin = core;
        if (plugin != null) {
            plugin.getLogger().warning(message);
        }
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

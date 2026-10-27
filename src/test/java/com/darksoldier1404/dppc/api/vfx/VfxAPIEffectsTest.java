package com.darksoldier1404.dppc.api.vfx;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import be.seeseemelk.mockbukkit.WorldMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import com.darksoldier1404.dppc.support.PluginTest;
import com.darksoldier1404.dppmc.protocol.Direction;
import com.darksoldier1404.dppmc.protocol.core.CoreProtocol;
import com.darksoldier1404.dppmc.protocol.core.Hello;
import com.darksoldier1404.dppmc.protocol.core.ModOffer;
import com.darksoldier1404.dppmc.protocol.frame.FrameEncoder;
import com.darksoldier1404.dppmc.protocol.frame.FrameLimits;
import com.darksoldier1404.dppmc.vfx.effect.SdfSpec;
import com.darksoldier1404.dppmc.vfx.math.Vec3f;
import com.darksoldier1404.dppmc.vfx.protocol.VfxProtocol;
import com.darksoldier1404.dppmc.vfx.tween.ColorSpec;
import com.darksoldier1404.dppmc.vfx.tween.Easings;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import org.bukkit.Location;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** The dome, light, shake, SDF and JSON effects of VfxAPI (DPP-ModCore 1.13.0, protocol 2). */
class VfxAPIEffectsTest extends PluginTest {
    private static final String GOOD = "{\"shape\":{\"kind\":\"POINT\"},\"count\":3,\"lifetime\":10,\"speed\":0.1,"
            + "\"size\":{\"start\":1,\"end\":0,\"ease\":\"LINEAR\"},\"alpha\":{\"start\":1,\"end\":0,\"ease\":\"LINEAR\"},"
            + "\"color\":{\"stops\":[16777215]}}";

    private WorldMock world;
    private PlayerMock modded;
    private PlayerMock vanilla;
    private PlayerMock far;

    @BeforeEach
    void players() {
        world = server.addSimpleWorld("vfx2");
        modded = player(new Location(world, 0, 64, 0));
        vanilla = player(new Location(world, 5, 64, 0));
        far = player(new Location(world, VfxAPI.DEFAULT_RADIUS + 50, 64, 0));
        hello(modded);
        hello(far);
        VfxAPI.clearEffects();
    }

    private PlayerMock player(Location at) {
        PlayerMock player = server.addPlayer();
        player.teleport(at);
        return player;
    }

    private void hello(PlayerMock player) {
        byte[] hello = CoreProtocol.SPEC.encode(Direction.C2S, new Hello(CoreProtocol.VERSION, "1.13.0", "26.3",
                List.of(new ModOffer(VfxProtocol.OWNER, "1.13.0", VfxProtocol.SPEC.version(),
                        VfxProtocol.SPEC.minCompatible()))));
        for (byte[] frame : new FrameEncoder(FrameLimits.SERVERBOUND).encode(hello)) {
            server.getMessenger().dispatchIncomingMessage(player, CoreProtocol.SPEC.channel(), frame);
        }
    }

    private static SdfSpec blob() {
        return new SdfSpec(List.of(new SdfSpec.SdfShape(SdfSpec.Type.SPHERE, new Vec3f(0f, 0f, 0f), new Vec3f(1f, 0f, 0f))),
                0.3f, ColorSpec.of(0xFF0000, 0x0000FF, Easings.LINEAR), 0.8f, 60, 0.05f);
    }

    @Test
    void domeLightAndSdfGoOnlyToNearbyClientsThatDrawThem() {
        Location at = new Location(world, 1, 65, 1);
        assertEquals(1, VfxAPI.dome(at, 3f, 0x66CCFF, 100));
        assertEquals(1, VfxAPI.light(at, 0xFFEEAA, 2f, 100));
        assertEquals(1, VfxAPI.sdf(at, blob()));
    }

    @Test
    void shakeGoesToThePlayersNearTheCentre() {
        Location at = new Location(world, 1, 65, 1);
        assertEquals(1, VfxAPI.shake(at, 2f, 20));
        assertEquals(0, VfxAPI.shake(new Location(world, 1000, 65, 0), 2f, 20));
    }

    @Test
    void sendingToOnePlayerSaysWhetherItCouldDrawIt() {
        Location at = new Location(world, 0, 65, 0);
        assertTrue(VfxAPI.dome(modded, at, 3f, 0x66CCFF, 100));
        assertFalse(VfxAPI.dome(vanilla, at, 3f, 0x66CCFF, 100));
        assertTrue(VfxAPI.light(modded, at, 0xFFEEAA, 2f, 100));
        assertTrue(VfxAPI.sdf(modded, at, blob()));
        assertTrue(VfxAPI.shake(modded, 2f, 20));
        assertFalse(VfxAPI.shake(vanilla, 2f, 20));
    }

    @Test
    void invalidDomeLightAndShakeAreRefusedBeforeAnythingIsSent() {
        Location at = new Location(world, 0, 65, 0);
        assertThrows(IllegalArgumentException.class, () -> VfxAPI.dome(at, 0f, 0xFFFFFF, 10));
        assertThrows(IllegalArgumentException.class, () -> VfxAPI.dome(at, 1f, 0xFFFFFF, 0));
        assertThrows(IllegalArgumentException.class, () -> VfxAPI.light(at, 0xFFFFFF, -1f, 10));
        assertThrows(IllegalArgumentException.class, () -> VfxAPI.light(at, 0xFFFFFF, 1f, 0));
        assertThrows(IllegalArgumentException.class, () -> VfxAPI.shake(modded, 0f, 10));
        assertThrows(IllegalArgumentException.class, () -> VfxAPI.shake(modded, 1f, 0));
    }

    @Test
    void aRegisteredJsonEffectPlaysByIdAndAnUnknownOneSendsNothing() {
        Location at = new Location(world, 1, 65, 1);
        VfxAPI.registerEffect("test/boom", GOOD);
        assertEquals(1, VfxAPI.play(at, "test/boom"));
        assertTrue(VfxAPI.play(modded, at, "test/boom"));
        assertFalse(VfxAPI.play(vanilla, at, "test/boom"));
        assertEquals(0, VfxAPI.play(at, "test/nope"));
        assertFalse(VfxAPI.play(modded, at, "test/nope"));
    }

    @Test
    void aBadJsonEffectIsRefusedWithTheFieldInTheMessage() {
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
                () -> VfxAPI.registerEffect("bad", GOOD.replace("POINT", "CUBEY")));
        assertTrue(e.getMessage().contains("shape.kind"), e.getMessage());
        assertFalse(VfxAPI.effectIds().contains("bad"));
    }

    @Test
    void loadingAFolderTakesEveryGoodFileAndSkipsTheBadOnes(@TempDir File folder) throws IOException {
        Files.writeString(new File(folder, "good.json").toPath(), GOOD, StandardCharsets.UTF_8);
        File sub = new File(folder, "sub");
        assertTrue(sub.mkdir());
        Files.writeString(new File(sub, "also.json").toPath(), GOOD, StandardCharsets.UTF_8);
        Files.writeString(new File(folder, "broken.json").toPath(), "{\"shape\": 1}", StandardCharsets.UTF_8);
        Files.writeString(new File(folder, "notes.txt").toPath(), "not an effect", StandardCharsets.UTF_8);
        assertEquals(2, VfxAPI.loadEffects(folder));
        assertEquals(java.util.Set.of("good", "sub/also"), VfxAPI.effectIds());
        // a reload replaces what was there
        assertTrue(new File(folder, "good.json").delete());
        assertEquals(1, VfxAPI.loadEffects(folder));
        assertEquals(java.util.Set.of("sub/also"), VfxAPI.effectIds());
    }

    @Test
    void aMissingFolderLoadsNothing(@TempDir File folder) {
        assertEquals(0, VfxAPI.loadEffects(new File(folder, "nope")));
        assertTrue(VfxAPI.effectIds().isEmpty());
    }

    private static String uploadedJson() {
        return com.darksoldier1404.dppmc.vfx.protocol.EffectJson.write(
                com.darksoldier1404.dppmc.vfx.effect.EffectSpec.of(
                        com.darksoldier1404.dppmc.vfx.effect.EmitterSpec.defaultBurst()));
    }

    @Test
    void aPermittedUploadRegistersTheEffectSoPlayWorks() {
        modded.addAttachment(plugin, VfxAPI.EDIT_PERMISSION, true);
        VfxProtocol.UploadResult r = VfxAPI.handleUpload(modded,
                new VfxProtocol.Upload("editor/boom", true, uploadedJson()));
        assertEquals(VfxProtocol.UploadResult.Status.OK, r.status());
        assertTrue(VfxAPI.effectIds().contains("editor/boom"));
        assertTrue(new File(plugin.getDataFolder(), "vfx/editor/boom.json").isFile());
        assertEquals(1, VfxAPI.play(new Location(world, 1, 65, 1), "editor/boom"));
    }

    @Test
    void aPlayerWithoutThePermissionGetsNoPermissionAndNoEffect() {
        VfxProtocol.UploadResult r = VfxAPI.handleUpload(modded,
                new VfxProtocol.Upload("editor/nope", true, uploadedJson()));
        assertEquals(VfxProtocol.UploadResult.Status.NO_PERMISSION, r.status());
        assertFalse(VfxAPI.effectIds().contains("editor/nope"));
        assertFalse(new File(plugin.getDataFolder(), "vfx/editor/nope.json").exists());
    }

    @Test
    void anUploadSentOnTheChannelReachesTheHandler() {
        modded.addAttachment(plugin, VfxAPI.EDIT_PERMISSION, true);
        byte[] message = VfxProtocol.SPEC.encode(Direction.C2S,
                new VfxProtocol.Upload("editor/wire", true, uploadedJson()));
        for (byte[] frame : new FrameEncoder(FrameLimits.SERVERBOUND).encode(message)) {
            server.getMessenger().dispatchIncomingMessage(modded, VfxProtocol.SPEC.channel(), frame);
        }
        assertTrue(VfxAPI.effectIds().contains("editor/wire"));
    }
}

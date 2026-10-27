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
import com.darksoldier1404.dppmc.vfx.effect.EffectSpec;
import com.darksoldier1404.dppmc.vfx.effect.EmitterSpec;
import com.darksoldier1404.dppmc.vfx.protocol.VfxProtocol;
import java.util.List;
import org.bukkit.Location;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** VfxAPI on a mocked server: one player whose DPP-ModCore draws effects, one vanilla client, one far away. */
class VfxAPITest extends PluginTest {
    private WorldMock world;
    private PlayerMock modded;
    private PlayerMock vanilla;
    private PlayerMock far;

    @BeforeEach
    void players() {
        world = server.addSimpleWorld("vfx");
        modded = player(new Location(world, 0, 64, 0));
        vanilla = player(new Location(world, 5, 64, 0));
        far = player(new Location(world, VfxAPI.DEFAULT_RADIUS + 50, 64, 0));
        hello(modded);
        hello(far);
    }

    private PlayerMock player(Location at) {
        PlayerMock player = server.addPlayer();
        player.teleport(at);
        return player;
    }

    /** What a 1.13.0 core says on join: it offers the effect channel alongside its mods. */
    private void hello(PlayerMock player) {
        byte[] hello = CoreProtocol.SPEC.encode(Direction.C2S, new Hello(CoreProtocol.VERSION, "1.13.0", "26.3",
                List.of(new ModOffer(VfxProtocol.OWNER, "1.13.0", VfxProtocol.SPEC.version(),
                        VfxProtocol.SPEC.minCompatible()))));
        for (byte[] frame : new FrameEncoder(FrameLimits.SERVERBOUND).encode(hello)) {
            server.getMessenger().dispatchIncomingMessage(player, CoreProtocol.SPEC.channel(), frame);
        }
    }

    @Test
    void theChannelIsBoundWithDppCore() {
        assertTrue(VfxAPI.isEnabled());
        assertTrue(VfxAPI.canSee(modded));
        assertFalse(VfxAPI.canSee(vanilla));
    }

    @Test
    void anEffectGoesOnlyToNearbyClientsThatDrawIt() {
        Location at = new Location(world, 1, 65, 1);
        assertEquals(1, VfxAPI.emitter().count(50).lifetime(20).play(at));
        assertEquals(1, VfxAPI.effect(at, EffectSpec.of(EmitterSpec.defaultBurst())));
        assertEquals(1, VfxAPI.beam().color(0x88CCFF).play(at, at.clone().add(0, 5, 0)));
        assertEquals(2, VfxAPI.emit(at, EmitterSpec.defaultBurst(), VfxAPI.DEFAULT_RADIUS * 2));
    }

    @Test
    void sendingToOnePlayerSaysWhetherItCouldDrawIt() {
        Location at = new Location(world, 0, 65, 0);
        assertTrue(VfxAPI.emitter().play(modded, at));
        assertFalse(VfxAPI.emitter().play(vanilla, at));
        assertTrue(VfxAPI.clear(modded));
        assertFalse(VfxAPI.clear(vanilla));
    }

    @Test
    void aBeamAcrossWorldsIsRefused() {
        WorldMock other = server.addSimpleWorld("other");
        assertThrows(IllegalArgumentException.class, () -> VfxAPI.beam().play(new Location(world, 0, 64, 0),
                new Location(other, 0, 64, 0)));
    }
}

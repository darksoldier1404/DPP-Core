package com.darksoldier1404.dppc.modbridge;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import com.darksoldier1404.dppc.support.PluginTest;
import com.darksoldier1404.dppmc.protocol.PacketCodec;
import com.darksoldier1404.dppmc.protocol.ProtocolSpec;
import java.time.Duration;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** The grace-period kick of a mandatory {@link ModRequirement}, counted per connection. */
class ModRequirementKickTest extends PluginTest {
    record Ping(int nonce) {
    }

    private static final ProtocolSpec SPEC = ProtocolSpec.builder("dp_test", 1)
            .c2s(0, Ping.class, PacketCodec.of((buf, ping) -> buf.writeInt(ping.nonce()), buf -> new Ping(buf.readInt())))
            .build();
    private static final int GRACE_TICKS = 100;

    @BeforeEach
    void requireTheMod() {
        ModBridge.bind(MockBukkit.createMockPlugin(), SPEC)
                .require(ModRequirement.mandatory().withGrace(Duration.ofMillis(GRACE_TICKS * 50L)));
    }

    @Test
    void aPlayerWithoutHelloIsKickedOnceTheGraceEnds() {
        PlayerMock player = server.addPlayer();
        server.getScheduler().performTicks(GRACE_TICKS - 1);
        assertTrue(player.isOnline());
        server.getScheduler().performTicks(1);
        assertFalse(player.isOnline());
    }

    @Test
    void anEarlierConnectionsGraceDoesNotKickARejoin() {
        UUID id = UUID.randomUUID();
        PlayerMock first = new PlayerMock(server, "Rejoiner", id);
        server.addPlayer(first);
        server.getScheduler().performTicks(40);
        first.disconnect();

        PlayerMock second = new PlayerMock(server, "Rejoiner", id);
        server.addPlayer(second);
        // the first connection's grace ends here; the second one's has 40 ticks to go
        server.getScheduler().performTicks(GRACE_TICKS - 40);
        assertTrue(second.isOnline());
        server.getScheduler().performTicks(40);
        assertFalse(second.isOnline());
    }
}

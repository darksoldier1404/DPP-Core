package com.darksoldier1404.dppc.modbridge;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.darksoldier1404.dppmc.protocol.frame.FrameDecoder;
import com.darksoldier1404.dppmc.protocol.frame.FrameEncoder;
import com.darksoldier1404.dppmc.protocol.frame.FrameLimits;
import java.util.List;
import java.util.Random;
import org.bukkit.plugin.messaging.Messenger;
import org.junit.jupiter.api.Test;

class ClientboundFramesTest {
    /** What Bukkit allows one plugin message to be on Minecraft 1.20.1 through 1.20.4. */
    private static final int OLD_SERVER_LIMIT = 32766;
    /** And from 1.20.5 on. */
    private static final int NEW_SERVER_LIMIT = 1048576;

    /** Bytes that do not compress, as a PNG does not. */
    private static byte[] noise(int length) {
        byte[] bytes = new byte[length];
        new Random(7).nextBytes(bytes);
        return bytes;
    }

    @Test
    void anOldServerGetsFramesItsMessengerAccepts() {
        FrameLimits limits = ClientboundFrames.limits(OLD_SERVER_LIMIT);

        assertEquals(OLD_SERVER_LIMIT, limits.maxFrameBytes());
        List<byte[]> frames = new FrameEncoder(limits).encode(noise(100_000));
        assertTrue(frames.size() > 1);
        for (byte[] frame : frames) {
            assertTrue(frame.length <= OLD_SERVER_LIMIT, "a frame of " + frame.length + " bytes");
        }
    }

    @Test
    void aNewServerKeepsTheProtocolsOwnFrameSize() {
        FrameLimits limits = ClientboundFrames.limits(NEW_SERVER_LIMIT);

        assertEquals(FrameLimits.CLIENTBOUND.maxFrameBytes(), limits.maxFrameBytes());
        assertEquals(1, new FrameEncoder(limits).encode(noise(100_000)).size());
    }

    @Test
    void everythingButTheFrameSizeIsTheProtocols() {
        FrameLimits limits = ClientboundFrames.limits(OLD_SERVER_LIMIT);

        assertEquals(FrameLimits.CLIENTBOUND.maxMessageBytes(), limits.maxMessageBytes());
        assertEquals(FrameLimits.CLIENTBOUND.compressionThreshold(), limits.compressionThreshold());
        assertEquals(FrameLimits.CLIENTBOUND.maxConcurrentTransfers(), limits.maxConcurrentTransfers());
        assertEquals(FrameLimits.CLIENTBOUND.transferTimeoutMillis(), limits.transferTimeoutMillis());
    }

    @Test
    void whatAnOldServerSendsIsReassembledByAClientThatWasToldItsFrameSize() {
        FrameLimits limits = ClientboundFrames.limits(OLD_SERVER_LIMIT);
        byte[] image = noise(256 * 1024);

        // The client builds its decoder from what the handshake advertised, never above the protocol's own.
        FrameDecoder<String> client = new FrameDecoder<>(new FrameLimits(
                Math.min(limits.maxFrameBytes(), FrameLimits.CLIENTBOUND.maxFrameBytes()),
                FrameLimits.CLIENTBOUND.maxMessageBytes(), FrameLimits.CLIENTBOUND.compressionThreshold(),
                FrameLimits.CLIENTBOUND.maxConcurrentTransfers(), FrameLimits.CLIENTBOUND.transferTimeoutMillis()));
        byte[] received = null;
        for (byte[] frame : new FrameEncoder(limits).encode(image)) {
            received = client.accept("server", frame, 0);
        }

        assertArrayEquals(image, received);
    }

    @Test
    void aLimitTooSmallToFrameAnythingFallsBackToTheSmallestFrame() {
        assertEquals(FrameLimits.MIN_FRAME_BYTES, ClientboundFrames.limits(10).maxFrameBytes());
        assertEquals(FrameLimits.MIN_FRAME_BYTES, ClientboundFrames.limits(-1).maxFrameBytes());
    }

    @Test
    void theServersLimitIsReadFromTheMessengerThatIsRunning() {
        // Read through reflection in the class under test: a plain reference would be the constant of whatever
        // API version DPP-Core was compiled against, not the server's.
        assertEquals(Messenger.MAX_MESSAGE_SIZE, ClientboundFrames.serverMessageLimit());
        assertNotNull(ClientboundFrames.limits());
        assertTrue(ClientboundFrames.limits().maxFrameBytes() <= Messenger.MAX_MESSAGE_SIZE);
    }
}

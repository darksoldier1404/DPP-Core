package com.darksoldier1404.dppc.modbridge;

import com.darksoldier1404.dppmc.protocol.frame.FrameLimits;
import org.bukkit.plugin.messaging.Messenger;

/**
 * How large a frame this server may send to a client. The protocol allows 256 KB, but Bukkit refuses a plugin
 * message over {@link Messenger#MAX_MESSAGE_SIZE} with a {@code MessageTooLargeException}, and that limit is
 * 32766 bytes up to Minecraft 1.20.4 and 1 MB from 1.20.5. So a frame is never larger than the smaller of the
 * two, and the handshake tells the client the size that is actually used.
 */
final class ClientboundFrames {
    /** What every Bukkit server accepts: the limit of the oldest version DPP-Core runs on. */
    private static final int SAFE_MESSAGE_LIMIT = 32766;

    private static final FrameLimits LIMITS = limits(serverMessageLimit());

    private ClientboundFrames() {
    }

    /** The limits for frames sent by the server that is running. */
    static FrameLimits limits() {
        return LIMITS;
    }

    /**
     * {@link FrameLimits#CLIENTBOUND} with its frame size held to what the server's messenger accepts.
     *
     * @param serverMessageLimit the largest plugin message the server sends, in bytes
     */
    static FrameLimits limits(int serverMessageLimit) {
        FrameLimits protocol = FrameLimits.CLIENTBOUND;
        int frame = Math.max(FrameLimits.MIN_FRAME_BYTES, Math.min(protocol.maxFrameBytes(), serverMessageLimit));
        return new FrameLimits(frame, protocol.maxMessageBytes(), protocol.compressionThreshold(),
                protocol.maxConcurrentTransfers(), protocol.transferTimeoutMillis());
    }

    /**
     * The largest plugin message the running server sends. Read through reflection on purpose:
     * {@code Messenger.MAX_MESSAGE_SIZE} is a compile-time constant, so a plain reference would carry the value
     * of the API DPP-Core was compiled against into the jar, whatever server loads it.
     */
    static int serverMessageLimit() {
        try {
            return Messenger.class.getField("MAX_MESSAGE_SIZE").getInt(null);
        } catch (ReflectiveOperationException | RuntimeException e) {
            return SAFE_MESSAGE_LIMIT;
        }
    }
}

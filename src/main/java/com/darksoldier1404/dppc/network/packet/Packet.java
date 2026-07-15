package com.darksoldier1404.dppc.network.packet;

import com.darksoldier1404.dppc.annotation.DPPCoreVersion;

/**
 * A single, self-describing message that travels over a plugin-message channel.
 *
 * <p>A packet carries <em>data only</em> — it knows how to serialize itself and what
 * id to travel under, but it holds no processing logic. Reacting to a received packet
 * is the job of a {@link PacketHandler}. This separation is what lets the exact same
 * {@code network.packet} classes be copied verbatim into a Fabric client mod: nothing
 * here references a platform type.
 *
 * <p>A concrete packet also needs a static factory usable as a {@code Function<PacketReader, T>}
 * (conventionally {@code static MyPacket read(PacketReader)}) so it can be registered in a
 * {@link PacketRegistry}. {@link #write(PacketWriter)} and that {@code read} factory must use
 * the exact same field order — the reader has no framing to recover from a mismatch.
 */
@DPPCoreVersion(since = "5.5.0")
public interface Packet {

    /**
     * The registry id this packet travels under. Must be stable and unique within a
     * {@link PacketRegistry}, and identical on both ends of the channel.
     */
    String id();

    /** Serializes this packet's payload. Called after the id has already been written. */
    void write(PacketWriter writer);
}

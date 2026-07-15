package com.darksoldier1404.dppc.network.packet;

import com.darksoldier1404.dppc.annotation.DPPCoreVersion;

/**
 * Processing logic for one packet type.
 *
 * <p>Packets carry data; handlers act on it. Keeping the two apart means a packet can be sent
 * from either side while the handling logic lives only where it makes sense, and it keeps the
 * data classes free of server- or client-only calls.
 *
 * @param <T> the packet type this handler consumes
 */
@DPPCoreVersion(since = "5.5.0")
@FunctionalInterface
public interface PacketHandler<T extends Packet> {

    /**
     * Reacts to a received {@code packet}.
     *
     * @param context the platform environment it arrived in (cast to the platform type as needed)
     * @param packet  the already-decoded packet
     */
    void handle(PacketContext context, T packet);
}

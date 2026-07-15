package com.darksoldier1404.dppc.network.packet;

import com.darksoldier1404.dppc.annotation.DPPCoreVersion;

/**
 * The platform-specific environment a packet is handled in.
 *
 * <p>This interface stays deliberately free of platform types so it can live in the shared
 * {@code network.packet} package on both server and client. Each platform provides its own
 * implementation that exposes the objects a handler actually needs:
 * <ul>
 *   <li>Server: {@code com.darksoldier1404.dppc.network.bukkit.BukkitPacketContext} → {@code Player}, {@code Plugin}</li>
 *   <li>Client: a Fabric context → {@code MinecraftClient}, {@code ClientPlayerEntity}</li>
 * </ul>
 * A {@link PacketHandler} obtains those by casting this context to the concrete type for the
 * side it runs on; {@link #side()} lets a handler shared between both sides branch first.
 */
@DPPCoreVersion(since = "5.5.0")
public interface PacketContext {

    /** Which end of the connection this context represents. */
    NetworkSide side();
}

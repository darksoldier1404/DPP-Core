package com.darksoldier1404.dppc.network.packet;

import com.darksoldier1404.dppc.annotation.DPPCoreVersion;

/**
 * Which end of the connection a {@link PacketContext} belongs to.
 *
 * <p>The concrete platform object (a Bukkit {@code Player}, a Fabric {@code MinecraftClient}, …)
 * is reached by casting the context to its platform type. This enum gives a handler that is
 * shared between both platforms a way to branch without that cast.
 */
@DPPCoreVersion(since = "5.5.0")
public enum NetworkSide {
    /** Running inside the Bukkit/Paper server. */
    SERVER,
    /** Running inside the client (e.g. a Fabric mod). */
    CLIENT
}

package com.darksoldier1404.dppc.network.bukkit;

import com.darksoldier1404.dppc.annotation.DPPCoreVersion;
import com.darksoldier1404.dppc.network.packet.NetworkSide;
import com.darksoldier1404.dppc.network.packet.PacketContext;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.Objects;

/**
 * Server-side {@link PacketContext}: the {@link Player} a packet arrived from and the owning
 * {@link Plugin}. A {@code PacketHandler} running on the server casts its context to this type to
 * reach them.
 */
@DPPCoreVersion(since = "5.5.0")
public final class BukkitPacketContext implements PacketContext {

    private final Plugin plugin;
    private final Player player;

    public BukkitPacketContext(Plugin plugin, Player player) {
        this.plugin = Objects.requireNonNull(plugin, "plugin");
        this.player = Objects.requireNonNull(player, "player");
    }

    /** The player this packet was received from. */
    public Player player() {
        return player;
    }

    /** The plugin that owns the channel. */
    public Plugin plugin() {
        return plugin;
    }

    @Override
    public NetworkSide side() {
        return NetworkSide.SERVER;
    }
}

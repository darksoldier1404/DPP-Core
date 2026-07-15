package com.darksoldier1404.dppc.network.bukkit;

import com.darksoldier1404.dppc.annotation.DPPCoreVersion;
import com.darksoldier1404.dppc.network.packet.Packet;
import com.darksoldier1404.dppc.network.packet.PacketContext;
import com.darksoldier1404.dppc.network.packet.PacketHandler;
import com.darksoldier1404.dppc.network.packet.PacketReader;
import com.darksoldier1404.dppc.network.packet.PacketRegistry;
import com.darksoldier1404.dppc.network.packet.PacketWriter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.messaging.Messenger;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.function.Function;

/**
 * The Bukkit/Paper side of the packet framework: it binds one plugin-message channel to a
 * {@link PacketRegistry}. Its only job is transport — register the channel, encode outgoing
 * packets, and feed incoming bytes to the registry. All routing and processing live in the
 * platform-neutral {@code network.packet} classes.
 *
 * <p>Typical use:
 * <pre>{@code
 * BukkitNetwork network = new BukkitNetwork(plugin, "dppcore:main");
 * network.register("status", StatusPacket::read, new StatusHandler());
 * network.start();               // in onEnable, after registering packets
 * network.send(player, packet);  // server -> client
 * network.stop();                // in onDisable
 * }</pre>
 *
 * <p>The channel name must be a lowercase namespaced key ({@code namespace:path}) and must match
 * exactly what the client registers. Adding a packet type never touches this class — it is one
 * {@link #register} call.
 */
@DPPCoreVersion(since = "5.5.0")
public final class BukkitNetwork implements PluginMessageListener {

    private final Plugin plugin;
    private final String channel;
    private final PacketRegistry registry = new PacketRegistry();
    private volatile boolean started;

    public BukkitNetwork(Plugin plugin, String channel) {
        this.plugin = Objects.requireNonNull(plugin, "plugin");
        this.channel = Objects.requireNonNull(channel, "channel");
    }

    /** The registry backing this channel; register packets here or via {@link #register}. */
    public PacketRegistry registry() {
        return registry;
    }

    /** The plugin-message channel this network is bound to. */
    public String channel() {
        return channel;
    }

    /** Convenience passthrough to {@link PacketRegistry#register}; returns {@code this} for chaining. */
    public <T extends Packet> BukkitNetwork register(String id, Function<PacketReader, T> decoder, PacketHandler<T> handler) {
        registry.register(id, decoder, handler);
        return this;
    }

    /** Registers the incoming and outgoing channel with the server. Idempotent; call after packets are registered. */
    public void start() {
        if (started) {
            return;
        }
        Messenger messenger = plugin.getServer().getMessenger();
        messenger.registerOutgoingPluginChannel(plugin, channel);
        messenger.registerIncomingPluginChannel(plugin, channel, this);
        started = true;
    }

    /** Unregisters the channel. Idempotent; call from {@code onDisable}. */
    public void stop() {
        if (!started) {
            return;
        }
        Messenger messenger = plugin.getServer().getMessenger();
        messenger.unregisterIncomingPluginChannel(plugin, channel, this);
        messenger.unregisterOutgoingPluginChannel(plugin, channel);
        started = false;
    }

    /**
     * Encodes {@code packet} and sends it to {@code player}. Requires {@link #start()} to have run,
     * and the payload must not exceed {@link Messenger#MAX_MESSAGE_SIZE} bytes.
     */
    public void send(Player player, Packet packet) {
        Objects.requireNonNull(player, "player");
        player.sendPluginMessage(plugin, channel, encode(packet));
    }

    /** Serializes a packet to its on-wire form: the id followed by the packet's own payload. */
    static byte[] encode(Packet packet) {
        Objects.requireNonNull(packet, "packet");
        PacketWriter writer = new PacketWriter();
        writer.writeString(packet.id());
        packet.write(writer);
        return writer.toByteArray();
    }

    @Override
    public void onPluginMessageReceived(@NotNull String channel, @NotNull Player player, @NotNull byte[] message) {
        if (!this.channel.equals(channel)) {
            return;
        }
        PacketContext context = new BukkitPacketContext(plugin, player);
        registry.handle(context, new PacketReader(message));
    }
}

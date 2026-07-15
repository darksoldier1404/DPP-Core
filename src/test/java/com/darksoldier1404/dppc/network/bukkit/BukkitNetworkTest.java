package com.darksoldier1404.dppc.network.bukkit;

import be.seeseemelk.mockbukkit.entity.PlayerMock;
import com.darksoldier1404.dppc.network.packet.NetworkSide;
import com.darksoldier1404.dppc.network.packet.Packet;
import com.darksoldier1404.dppc.network.packet.PacketContext;
import com.darksoldier1404.dppc.network.packet.PacketReader;
import com.darksoldier1404.dppc.network.packet.PacketWriter;
import com.darksoldier1404.dppc.support.PluginTest;
import org.bukkit.plugin.messaging.Messenger;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests the Bukkit transport adapter: channel registration on {@code start()}/{@code stop()},
 * the received-bytes → registry dispatch path, context wiring, and the on-wire encode format.
 */
class BukkitNetworkTest extends PluginTest {

    private static final String CHANNEL = "dppcore:main";

    @Test
    void startRegistersChannelsAndStopUnregistersThem() {
        BukkitNetwork network = new BukkitNetwork(plugin, CHANNEL);
        Messenger messenger = server.getMessenger();

        network.start();
        assertTrue(messenger.isOutgoingChannelRegistered(plugin, CHANNEL));
        assertTrue(messenger.isIncomingChannelRegistered(plugin, CHANNEL));

        network.stop();
        assertFalse(messenger.isOutgoingChannelRegistered(plugin, CHANNEL));
        assertFalse(messenger.isIncomingChannelRegistered(plugin, CHANNEL));
    }

    @Test
    void receivedBytesAreDecodedAndDispatchedWithServerContext() {
        BukkitNetwork network = new BukkitNetwork(plugin, CHANNEL);
        AtomicReference<StatusPacket> packet = new AtomicReference<>();
        AtomicReference<PacketContext> context = new AtomicReference<>();
        network.register("status", StatusPacket::read, (ctx, pkt) -> {
            context.set(ctx);
            packet.set(pkt);
        });

        PlayerMock player = server.addPlayer("Tester");
        network.onPluginMessageReceived(CHANNEL, player, BukkitNetwork.encode(new StatusPacket("hp", 5f, 20f)));

        assertNotNull(packet.get());
        assertEquals("hp", packet.get().type);
        assertEquals(5f, packet.get().current);
        assertEquals(20f, packet.get().max);

        assertInstanceOf(BukkitPacketContext.class, context.get());
        BukkitPacketContext bukkitContext = (BukkitPacketContext) context.get();
        assertSame(player, bukkitContext.player());
        assertSame(plugin, bukkitContext.plugin());
        assertEquals(NetworkSide.SERVER, bukkitContext.side());
    }

    @Test
    void messagesOnOtherChannelsAreIgnored() {
        BukkitNetwork network = new BukkitNetwork(plugin, CHANNEL);
        AtomicInteger calls = new AtomicInteger();
        network.register("status", StatusPacket::read, (ctx, pkt) -> calls.incrementAndGet());

        PlayerMock player = server.addPlayer("Tester");
        network.onPluginMessageReceived("some:other", player, BukkitNetwork.encode(new StatusPacket("hp", 5f, 20f)));

        assertEquals(0, calls.get());
    }

    @Test
    void encodeWritesIdBeforePayload() {
        byte[] bytes = BukkitNetwork.encode(new StatusPacket("hp", 5f, 20f));

        PacketReader reader = new PacketReader(bytes);
        assertEquals("status", reader.readString()); // id first
        assertEquals("hp", reader.readString());
        assertEquals(5f, reader.readFloat());
        assertEquals(20f, reader.readFloat());
        assertFalse(reader.hasRemaining());
    }

    @Test
    void unknownPacketIdIsSilentlyIgnored() {
        BukkitNetwork network = new BukkitNetwork(plugin, CHANNEL);
        AtomicInteger calls = new AtomicInteger();
        network.register("status", StatusPacket::read, (ctx, pkt) -> calls.incrementAndGet());

        PlayerMock player = server.addPlayer("Tester");
        byte[] bytes = new PacketWriter().writeString("unregistered").toByteArray();
        network.onPluginMessageReceived(CHANNEL, player, bytes); // must not throw

        assertEquals(0, calls.get());
    }

    /** Minimal stand-in packet used to exercise the transport. */
    private static final class StatusPacket implements Packet {
        final String type;
        final float current;
        final float max;

        StatusPacket(String type, float current, float max) {
            this.type = type;
            this.current = current;
            this.max = max;
        }

        static StatusPacket read(PacketReader reader) {
            return new StatusPacket(reader.readString(), reader.readFloat(), reader.readFloat());
        }

        @Override
        public String id() {
            return "status";
        }

        @Override
        public void write(PacketWriter writer) {
            writer.writeString(type).writeFloat(current).writeFloat(max);
        }
    }
}

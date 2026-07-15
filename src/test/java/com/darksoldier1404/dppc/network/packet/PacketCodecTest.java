package com.darksoldier1404.dppc.network.packet;

import org.junit.jupiter.api.Test;

import java.io.UncheckedIOException;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for the platform-neutral packet core: {@link PacketWriter}/{@link PacketReader}
 * serialization and {@link PacketRegistry} dispatch. No Bukkit involved.
 */
class PacketCodecTest {

    private enum Rarity { COMMON, RARE, LEGENDARY }

    private static final PacketContext SERVER_CONTEXT = () -> NetworkSide.SERVER;

    // --- round trips ---------------------------------------------------------

    @Test
    void everyPrimitiveTypeRoundTrips() {
        byte[] bytes = new PacketWriter()
                .writeString("héllo")   // multi-byte UTF-8 to prove length is in bytes, not chars
                .writeInt(-42)
                .writeLong(9_000_000_000L)
                .writeFloat(3.5f)
                .writeDouble(2.718281828)
                .writeBoolean(true)
                .writeBoolean(false)
                .toByteArray();

        PacketReader reader = new PacketReader(bytes);
        assertEquals("héllo", reader.readString());
        assertEquals(-42, reader.readInt());
        assertEquals(9_000_000_000L, reader.readLong());
        assertEquals(3.5f, reader.readFloat());
        assertEquals(2.718281828, reader.readDouble());
        assertTrue(reader.readBoolean());
        assertFalse(reader.readBoolean());
        assertFalse(reader.hasRemaining());
    }

    @Test
    void uuidRoundTrips() {
        UUID id = UUID.randomUUID();
        byte[] bytes = new PacketWriter().writeUUID(id).toByteArray();
        assertEquals(id, new PacketReader(bytes).readUUID());
    }

    @Test
    void enumRoundTripsByName() {
        byte[] bytes = new PacketWriter().writeEnum(Rarity.LEGENDARY).toByteArray();
        assertEquals(Rarity.LEGENDARY, new PacketReader(bytes).readEnum(Rarity.class));
    }

    @Test
    void emptyStringRoundTrips() {
        byte[] bytes = new PacketWriter().writeString("").toByteArray();
        PacketReader reader = new PacketReader(bytes);
        assertEquals("", reader.readString());
        assertFalse(reader.hasRemaining());
    }

    // --- exact wire layout ---------------------------------------------------

    @Test
    void wireLayoutIsBigEndianLengthPrefixedUtf8() {
        byte[] bytes = new PacketWriter()
                .writeString("hi")   // int length 2, then 'h','i'
                .writeInt(1)
                .writeBoolean(true)
                .toByteArray();

        assertArrayEquals(new byte[]{
                0, 0, 0, 2, 'h', 'i',   // string
                0, 0, 0, 1,             // int 1, big-endian
                1                       // boolean true
        }, bytes);
    }

    // --- error handling ------------------------------------------------------

    @Test
    void readingPastEndThrows() {
        byte[] onlyThreeBytes = {0, 0, 0}; // an int needs four
        assertThrows(UncheckedIOException.class, () -> new PacketReader(onlyThreeBytes).readInt());
    }

    @Test
    void negativeStringLengthThrows() {
        byte[] bytes = new PacketWriter().writeInt(-1).toByteArray(); // -1 read as a string length
        assertThrows(UncheckedIOException.class, () -> new PacketReader(bytes).readString());
    }

    @Test
    void unknownEnumConstantThrows() {
        byte[] bytes = new PacketWriter().writeString("MYTHIC").toByteArray();
        assertThrows(IllegalArgumentException.class, () -> new PacketReader(bytes).readEnum(Rarity.class));
    }

    // --- registry dispatch ---------------------------------------------------

    @Test
    void registryDecodesAndDispatchesToHandler() {
        PacketRegistry registry = new PacketRegistry();
        AtomicReference<StatusPacket> received = new AtomicReference<>();
        AtomicReference<PacketContext> receivedContext = new AtomicReference<>();
        registry.register("status", StatusPacket::read, (context, packet) -> {
            receivedContext.set(context);
            received.set(packet);
        });

        StatusPacket sent = new StatusPacket("hp", 12.5f, 20f);
        registry.handle(SERVER_CONTEXT, new PacketReader(encode(sent)));

        assertNotNull(received.get());
        assertEquals("hp", received.get().type);
        assertEquals(12.5f, received.get().current);
        assertEquals(20f, received.get().max);
        assertSame(SERVER_CONTEXT, receivedContext.get());
    }

    @Test
    void registryReturnsFalseForUnknownId() {
        PacketRegistry registry = new PacketRegistry();
        AtomicReference<StatusPacket> received = new AtomicReference<>();
        registry.register("status", StatusPacket::read, (context, packet) -> received.set(packet));

        byte[] bytes = new PacketWriter().writeString("does_not_exist").toByteArray();
        assertFalse(registry.handle(SERVER_CONTEXT, new PacketReader(bytes)));
        assertNull(received.get());
    }

    @Test
    void registeringDuplicateIdThrows() {
        PacketRegistry registry = new PacketRegistry();
        registry.register("status", StatusPacket::read, (c, p) -> { });
        assertThrows(IllegalStateException.class,
                () -> registry.register("status", StatusPacket::read, (c, p) -> { }));
    }

    @Test
    void isRegisteredReflectsRegistration() {
        PacketRegistry registry = new PacketRegistry();
        assertFalse(registry.isRegistered("status"));
        registry.register("status", StatusPacket::read, (c, p) -> { });
        assertTrue(registry.isRegistered("status"));
    }

    // --- test fixtures -------------------------------------------------------

    /** Encodes a packet the same way the transport does: id first, then payload. */
    private static byte[] encode(Packet packet) {
        PacketWriter writer = new PacketWriter();
        writer.writeString(packet.id());
        packet.write(writer);
        return writer.toByteArray();
    }

    /** A minimal packet, standing in for a real one, to exercise the write/read/register flow. */
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

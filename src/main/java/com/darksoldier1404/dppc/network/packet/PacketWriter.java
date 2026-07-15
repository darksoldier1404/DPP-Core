package com.darksoldier1404.dppc.network.packet;

import com.darksoldier1404.dppc.annotation.DPPCoreVersion;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.UUID;

/**
 * Write-only serializer that turns a packet's fields into a {@code byte[]} payload.
 *
 * <p>The wire format is fixed and shared with {@link PacketReader} — the reader must read the
 * exact same types in the exact same order the writer wrote them, because the stream carries no
 * per-field framing. Numbers are big-endian (the same order Netty/Minecraft's buffers use), so a
 * client reading these bytes with a standard {@code DataInputStream} or {@code ByteBuf} agrees.
 *
 * <p>Wire layout:
 * <ul>
 *   <li><b>String</b>: {@code int} byte-length, then that many UTF-8 bytes</li>
 *   <li><b>int / long / float / double</b>: 4 / 8 / 4 / 8 bytes, big-endian</li>
 *   <li><b>boolean</b>: 1 byte (0 or 1)</li>
 *   <li><b>UUID</b>: two {@code long}s (most, then least significant bits)</li>
 *   <li><b>Enum</b>: its {@link Enum#name()} as a String (order-independent, unlike the ordinal)</li>
 * </ul>
 * Collections follow the pattern "write a count, then each element"; optional values follow
 * "write a boolean, then the value only if present" — both composed from the methods here.
 */
@DPPCoreVersion(since = "5.5.0")
public final class PacketWriter {

    private final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
    private final DataOutputStream out = new DataOutputStream(buffer);

    /** Writes an {@code int}-length-prefixed UTF-8 string. */
    public PacketWriter writeString(String value) {
        Objects.requireNonNull(value, "value");
        byte[] bytes = value.getBytes(StandardCharsets.UTF_8);
        try {
            out.writeInt(bytes.length);
            out.write(bytes);
        } catch (IOException e) {
            throw new UncheckedIOException(e); // unreachable for an in-memory buffer
        }
        return this;
    }

    public PacketWriter writeInt(int value) {
        try {
            out.writeInt(value);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        return this;
    }

    public PacketWriter writeLong(long value) {
        try {
            out.writeLong(value);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        return this;
    }

    public PacketWriter writeFloat(float value) {
        try {
            out.writeFloat(value);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        return this;
    }

    public PacketWriter writeDouble(double value) {
        try {
            out.writeDouble(value);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        return this;
    }

    public PacketWriter writeBoolean(boolean value) {
        try {
            out.writeBoolean(value);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        return this;
    }

    /** Writes a UUID as two longs: most-significant bits first, then least-significant. */
    public PacketWriter writeUUID(UUID value) {
        Objects.requireNonNull(value, "value");
        return writeLong(value.getMostSignificantBits()).writeLong(value.getLeastSignificantBits());
    }

    /** Writes an enum constant by {@link Enum#name()} — stable across constant reordering. */
    public PacketWriter writeEnum(Enum<?> value) {
        Objects.requireNonNull(value, "value");
        return writeString(value.name());
    }

    /** Returns a copy of everything written so far, ready to hand to the transport. */
    public byte[] toByteArray() {
        return buffer.toByteArray();
    }
}

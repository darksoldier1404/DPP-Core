package com.darksoldier1404.dppc.network.packet;

import com.darksoldier1404.dppc.annotation.DPPCoreVersion;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.UUID;

/**
 * Read-only deserializer, the exact mirror of {@link PacketWriter}.
 *
 * <p>Reads must happen in the same order and of the same types as the corresponding writes; the
 * payload has no framing, so a mismatch surfaces as garbage values or an {@link UncheckedIOException}
 * (wrapping {@code EOFException}) when the stream runs out. See {@link PacketWriter} for the layout.
 */
@DPPCoreVersion(since = "5.5.0")
public final class PacketReader {

    private final DataInputStream in;

    /** Wraps a payload produced by {@link PacketWriter#toByteArray()}. */
    public PacketReader(byte[] data) {
        Objects.requireNonNull(data, "data");
        this.in = new DataInputStream(new ByteArrayInputStream(data));
    }

    /** Reads an {@code int}-length-prefixed UTF-8 string. */
    public String readString() {
        try {
            int length = in.readInt();
            if (length < 0) {
                throw new UncheckedIOException(new IOException("Negative string length: " + length));
            }
            byte[] bytes = new byte[length];
            in.readFully(bytes);
            return new String(bytes, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public int readInt() {
        try {
            return in.readInt();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public long readLong() {
        try {
            return in.readLong();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public float readFloat() {
        try {
            return in.readFloat();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public double readDouble() {
        try {
            return in.readDouble();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public boolean readBoolean() {
        try {
            return in.readBoolean();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /** Reads a UUID as two longs: most-significant bits first, then least-significant. */
    public UUID readUUID() {
        return new UUID(readLong(), readLong());
    }

    /**
     * Reads an enum constant written by {@link PacketWriter#writeEnum(Enum)}.
     *
     * @throws IllegalArgumentException if the name is not a constant of {@code type}
     */
    public <T extends Enum<T>> T readEnum(Class<T> type) {
        Objects.requireNonNull(type, "type");
        return Enum.valueOf(type, readString());
    }

    /**
     * Whether any bytes remain to be read. Useful for the "write a count, then loop" collection
     * pattern and for length-driven decoding.
     */
    public boolean hasRemaining() {
        try {
            return in.available() > 0;
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}

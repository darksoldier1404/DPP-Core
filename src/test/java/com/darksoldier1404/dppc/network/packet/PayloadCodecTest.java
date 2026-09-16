package com.darksoldier1404.dppc.network.packet;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.zip.Deflater;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Tests the transfer envelope (compression + size guard) and chunk splitting. */
class PayloadCodecTest {

    @Test
    void uncompressedEnvelopeRoundTrips() {
        byte[] original = "some encoded packet payload".getBytes();
        byte[] envelope = PayloadCodec.encodeEnvelope(original, false, Deflater.DEFAULT_COMPRESSION);

        assertEquals(0, envelope[0], "flag byte should mark uncompressed");
        assertArrayEquals(original, PayloadCodec.decodeEnvelope(envelope, 1 << 20));
    }

    @Test
    void compressibleDataIsCompressedAndRoundTrips() {
        byte[] original = new byte[5000]; // all zeros — highly compressible
        byte[] envelope = PayloadCodec.encodeEnvelope(original, true, Deflater.DEFAULT_COMPRESSION);

        assertEquals(1, envelope[0], "flag byte should mark compressed");
        assertTrue(envelope.length < original.length, "envelope should be smaller than the input");
        assertArrayEquals(original, PayloadCodec.decodeEnvelope(envelope, 1 << 20));
    }

    @Test
    void incompressibleDataFallsBackToUncompressed() {
        // A few bytes that deflate cannot shrink below the original — must not inflate the payload.
        byte[] original = {0x11, 0x42, (byte) 0x9c, 0x03};
        byte[] envelope = PayloadCodec.encodeEnvelope(original, true, Deflater.DEFAULT_COMPRESSION);

        assertEquals(0, envelope[0], "should fall back to uncompressed when compression doesn't help");
        assertArrayEquals(original, PayloadCodec.decodeEnvelope(envelope, 1 << 20));
    }

    @Test
    void decodeRejectsPayloadLargerThanMaxSize() {
        byte[] original = new byte[5000];
        byte[] envelope = PayloadCodec.encodeEnvelope(original, true, Deflater.DEFAULT_COMPRESSION);

        // declared original length (5000) exceeds the cap — must refuse before allocating
        assertThrows(IllegalStateException.class, () -> PayloadCodec.decodeEnvelope(envelope, 4999));
    }

    @Test
    void splitProducesCeilingChunkCountAndReassembles() {
        byte[] data = new byte[2500];
        for (int i = 0; i < data.length; i++) {
            data[i] = (byte) i;
        }

        List<ChunkPacket> chunks = PayloadCodec.split(data, 7, 1000);

        assertEquals(3, chunks.size(), "2500 / 1000 rounds up to 3");
        assertEquals(1000, chunks.get(0).bytes().length);
        assertEquals(1000, chunks.get(1).bytes().length);
        assertEquals(500, chunks.get(2).bytes().length);
        for (int seq = 0; seq < chunks.size(); seq++) {
            assertEquals(7, chunks.get(seq).transferId());
            assertEquals(3, chunks.get(seq).total());
            assertEquals(seq, chunks.get(seq).seq());
        }
        assertArrayEquals(data, concat(chunks));
    }

    @Test
    void emptyDataStillProducesOneChunk() {
        List<ChunkPacket> chunks = PayloadCodec.split(new byte[0], 1, 1000);
        assertEquals(1, chunks.size());
        assertEquals(0, chunks.get(0).bytes().length);
    }

    @Test
    void fullEncodeSplitReassembleDecodeRoundTrips() {
        byte[] original = new byte[40_000];
        for (int i = 0; i < original.length; i++) {
            original[i] = (byte) (i * 31);
        }

        byte[] envelope = PayloadCodec.encodeEnvelope(original, true, Deflater.DEFAULT_COMPRESSION);
        List<ChunkPacket> chunks = PayloadCodec.split(envelope, 3, 30_000);
        byte[] reassembled = concat(chunks);

        assertArrayEquals(original, PayloadCodec.decodeEnvelope(reassembled, 1 << 20));
    }

    private static byte[] concat(List<ChunkPacket> chunks) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        for (ChunkPacket chunk : chunks) {
            out.writeBytes(chunk.bytes());
        }
        return out.toByteArray();
    }
}

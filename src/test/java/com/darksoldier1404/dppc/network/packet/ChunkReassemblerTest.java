package com.darksoldier1404.dppc.network.packet;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/** Tests reassembly ordering, isolation between concurrent transfers, and every resource guard. */
class ChunkReassemblerTest {

    private static final UUID SOURCE = UUID.randomUUID();
    private static final UUID OTHER = UUID.randomUUID();

    private static ChunkOptions options() {
        return ChunkOptions.builder()
                .splitThreshold(50)
                .chunkPayloadSize(100)
                .maxReassembledSize(150)
                .transferTimeoutMillis(1000)
                .maxConcurrentTransfersPerSource(2)
                .inbound(true)
                .build();
    }

    private static ChunkPacket chunk(int transferId, int total, int seq, int len) {
        byte[] bytes = new byte[len];
        Arrays.fill(bytes, (byte) seq);
        return new ChunkPacket(transferId, total, seq, bytes);
    }

    @Test
    void inOrderChunksReassembleOnLastChunk() {
        ChunkReassembler<UUID> reassembler = new ChunkReassembler<>(options());

        assertNull(reassembler.accept(SOURCE, chunk(1, 2, 0, 10), 0));
        byte[] result = reassembler.accept(SOURCE, chunk(1, 2, 1, 10), 0);

        byte[] expected = new byte[20];
        Arrays.fill(expected, 10, 20, (byte) 1); // first 10 = seq 0 (zeros), next 10 = seq 1 (ones)
        assertArrayEquals(expected, result);
        assertEquals(0, reassembler.activeTransfers(SOURCE), "completed transfer is freed");
    }

    @Test
    void concurrentTransfersDoNotMix() {
        ChunkReassembler<UUID> reassembler = new ChunkReassembler<>(options());

        assertNull(reassembler.accept(SOURCE, chunk(1, 2, 0, 5), 0));
        assertNull(reassembler.accept(SOURCE, chunk(2, 2, 0, 5), 0)); // interleave a second transfer
        byte[] first = reassembler.accept(SOURCE, chunk(1, 2, 1, 5), 0);
        byte[] second = reassembler.accept(SOURCE, chunk(2, 2, 1, 5), 0);

        byte[] expectedFirst = new byte[10];
        Arrays.fill(expectedFirst, 5, 10, (byte) 1);
        assertArrayEquals(expectedFirst, first);
        assertArrayEquals(expectedFirst, second); // same shape, independent buffer
    }

    @Test
    void differentSourcesAreIsolated() {
        ChunkReassembler<UUID> reassembler = new ChunkReassembler<>(options());

        assertNull(reassembler.accept(SOURCE, chunk(1, 2, 0, 5), 0));
        assertNull(reassembler.accept(OTHER, chunk(1, 2, 0, 5), 0)); // same transferId, other source
        assertEquals(1, reassembler.activeTransfers(SOURCE));
        assertEquals(1, reassembler.activeTransfers(OTHER));
    }

    @Test
    void seqGapDiscardsTheTransfer() {
        ChunkReassembler<UUID> reassembler = new ChunkReassembler<>(options());

        assertNull(reassembler.accept(SOURCE, chunk(1, 3, 0, 5), 0));
        assertNull(reassembler.accept(SOURCE, chunk(1, 3, 2, 5), 0), "skipping seq 1 discards the transfer");
        assertEquals(0, reassembler.activeTransfers(SOURCE));
        // the now-orphaned seq 1 has no buffer and is ignored
        assertNull(reassembler.accept(SOURCE, chunk(1, 3, 1, 5), 0));
        assertEquals(0, reassembler.activeTransfers(SOURCE));
    }

    @Test
    void strayNonZeroFirstSeqIsIgnored() {
        ChunkReassembler<UUID> reassembler = new ChunkReassembler<>(options());
        assertNull(reassembler.accept(SOURCE, chunk(1, 3, 1, 5), 0));
        assertEquals(0, reassembler.activeTransfers(SOURCE));
    }

    @Test
    void exceedingMaxReassembledSizeDiscards() {
        ChunkReassembler<UUID> reassembler = new ChunkReassembler<>(options()); // cap 150

        assertNull(reassembler.accept(SOURCE, chunk(1, 2, 0, 100), 0)); // 100 <= 150
        assertNull(reassembler.accept(SOURCE, chunk(1, 2, 1, 100), 0), "100+100 > 150 discards");
        assertEquals(0, reassembler.activeTransfers(SOURCE));
    }

    @Test
    void concurrencyLimitDropsExtraTransfers() {
        ChunkReassembler<UUID> reassembler = new ChunkReassembler<>(options()); // limit 2

        assertNull(reassembler.accept(SOURCE, chunk(1, 2, 0, 5), 0));
        assertNull(reassembler.accept(SOURCE, chunk(2, 2, 0, 5), 0));
        assertNull(reassembler.accept(SOURCE, chunk(3, 2, 0, 5), 0)); // third is dropped
        assertEquals(2, reassembler.activeTransfers(SOURCE));
    }

    @Test
    void sweepDiscardsExpiredTransfers() {
        ChunkReassembler<UUID> reassembler = new ChunkReassembler<>(options()); // timeout 1000

        assertNull(reassembler.accept(SOURCE, chunk(1, 2, 0, 5), 0));
        reassembler.sweep(999);
        assertEquals(1, reassembler.activeTransfers(SOURCE), "not yet expired");
        reassembler.sweep(1000);
        assertEquals(0, reassembler.activeTransfers(SOURCE), "expired at the timeout boundary");
    }

    @Test
    void startingANewTransferExpiresStaleOnesForThatSource() {
        ChunkReassembler<UUID> reassembler = new ChunkReassembler<>(options());

        assertNull(reassembler.accept(SOURCE, chunk(1, 2, 0, 5), 0));       // created at t=0
        assertNull(reassembler.accept(SOURCE, chunk(2, 2, 0, 5), 2000));    // t=2000, stale #1 swept first
        assertEquals(1, reassembler.activeTransfers(SOURCE));
    }

    @Test
    void clearDropsSourceBuffers() {
        ChunkReassembler<UUID> reassembler = new ChunkReassembler<>(options());

        assertNull(reassembler.accept(SOURCE, chunk(1, 2, 0, 5), 0));
        reassembler.clear(SOURCE);
        assertEquals(0, reassembler.activeTransfers(SOURCE));
    }
}

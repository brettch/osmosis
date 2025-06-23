// This software is released into the Public Domain.  See copying.txt for details.
package org.openstreetmap.osmosis.apidb.v0_6.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import org.junit.jupiter.api.Test;

/**
 * Tests the transaction snapshot class.
 */
public class TransactionSnapshotTest {
    /**
     * Tests the database snapshot string parsing.
     */
    @Test
    public void testParseSnapshot() {
        TransactionSnapshot snapshot;

        snapshot = new TransactionSnapshot("1234:5678:101112,131415,161718");

        assertEquals(1234, snapshot.getXMin(), "xMin is incorrect.");
        assertEquals(5678, snapshot.getXMax(), "xMax is incorrect.");
        assertEquals(
                Arrays.asList(new Long[] {Long.valueOf(101112), Long.valueOf(131415), Long.valueOf(161718)}),
                snapshot.getXIpList(),
                "xIpList is incorrect.");
    }
}

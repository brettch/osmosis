// This software is released into the Public Domain.  See copying.txt for details.
package org.openstreetmap.osmosis.apidb.v0_6.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.openstreetmap.osmosis.replication.common.ReplicationSequenceFormatter;

/**
 * Tests the replication file sequence formatter.
 */
public class ReplicationSequenceFormatterTest {
    /**
     * Tests that a sequence is formatted correctly when no format is defined.
     */
    @Test
    public void testMinimalFormat() {
        final int minimumLength = 0;
        final int groupingLength = 0;
        final long sequenceNumber = 100;
        String formattedSequenceNumber;

        formattedSequenceNumber = new ReplicationSequenceFormatter(minimumLength, groupingLength)
                .getFormattedName(sequenceNumber, ".osc.gz");
        assertEquals("100.osc.gz", formattedSequenceNumber, "The formatted sequence number is incorrect.");
    }

    /**
     * Tests that a number can be formatted correctly when using a simple 0 padded format.
     */
    @Test
    public void testFixedFormat() {
        final int minimumLength = 9;
        final int groupingLength = 0;
        final long sequenceNumber = 100;
        String formattedSequenceNumber;

        formattedSequenceNumber = new ReplicationSequenceFormatter(minimumLength, groupingLength)
                .getFormattedName(sequenceNumber, ".osc.gz");
        assertEquals("000000100.osc.gz", formattedSequenceNumber, "The formatted sequence number is incorrect.");
    }

    /**
     * Tests that a number can be formatted correctly when including path separators with no fixed width.
     */
    @Test
    public void testMinimalPathFormat() {
        final int minimumLength = 0;
        final int groupingLength = 3;
        final long sequenceNumber = 1000;
        String formattedSequenceNumber;

        formattedSequenceNumber = new ReplicationSequenceFormatter(minimumLength, groupingLength)
                .getFormattedName(sequenceNumber, ".osc.gz");
        assertEquals("1/000.osc.gz", formattedSequenceNumber, "The formatted sequence number is incorrect.");
    }

    /**
     * Tests that a number can be formatted correctly when including path separators and a fixed width.
     */
    @Test
    public void testFixedPathFormat() {
        final int minimumLength = 9;
        final int groupingLength = 3;
        final long sequenceNumber = 100;
        String formattedSequenceNumber;

        formattedSequenceNumber = new ReplicationSequenceFormatter(minimumLength, groupingLength)
                .getFormattedName(sequenceNumber, ".osc.gz");
        assertEquals("000/000/100.osc.gz", formattedSequenceNumber, "The formatted sequence number is incorrect.");
    }
}

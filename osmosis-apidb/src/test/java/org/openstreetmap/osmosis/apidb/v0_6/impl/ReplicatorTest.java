// This software is released into the Public Domain.  See copying.txt for details.
package org.openstreetmap.osmosis.apidb.v0_6.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import org.junit.jupiter.api.Test;
import org.openstreetmap.osmosis.core.OsmosisRuntimeException;

/**
 * Tests for the Replicator class.
 */
public class ReplicatorTest {

    private Date buildDate(String rawDate) {
        try {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(rawDate);
        } catch (ParseException e) {
            throw new OsmosisRuntimeException("The date could not be parsed.", e);
        }
    }

    /**
     * Tests replication behaviour during initialisation. Initialisation occurs the first time
     * replication is run.
     */
    @Test
    public void testInitialization() {
        Replicator replicator;
        MockReplicationSource source;
        MockReplicationDestination destination;
        MockTransactionSnapshotLoader snapshotLoader;
        MockSystemTimeLoader timeLoader;
        ReplicationState state;

        // Build the mocks.
        source = new MockReplicationSource();
        destination = new MockReplicationDestination();
        snapshotLoader = new MockTransactionSnapshotLoader();
        timeLoader = new MockSystemTimeLoader();

        // Instantiate the new replicator.
        replicator = new Replicator(source, destination, snapshotLoader, timeLoader, 1, 0, 0);

        // Provide initialisation data.
        timeLoader.getTimes().add(buildDate("2009-10-11 12:13:14"));
        timeLoader.getTimes().add(buildDate("2009-10-11 12:13:14"));
        snapshotLoader.getSnapshots().add(new TransactionSnapshot("100:200:110,112"));

        // Launch the replication process.
        replicator.replicate();

        // Verify the final state.
        state = destination.getCurrentState();
        assertEquals(
                new ReplicationState(
                        200,
                        200,
                        Arrays.asList(new Long[] {110L, 112L}),
                        Arrays.asList(new Long[] {}),
                        buildDate("2009-10-11 12:13:14"),
                        0),
                state,
                "Incorrect final state.");
    }

    /**
     * Tests replication behaviour when no replication is required.
     */
    @Test
    public void testNoAction() {
        Replicator replicator;
        MockReplicationSource source;
        MockReplicationDestination destination;
        MockTransactionSnapshotLoader snapshotLoader;
        MockSystemTimeLoader timeLoader;
        ReplicationState initialState;
        ReplicationState finalState;

        // Build initial replication state.
        initialState = new ReplicationState(
                200,
                200,
                Arrays.asList(new Long[] {110L, 112L}),
                Arrays.asList(new Long[] {}),
                buildDate("2009-10-11 12:13:14"),
                0);

        // Build the mocks.
        source = new MockReplicationSource();
        destination = new MockReplicationDestination(initialState);
        snapshotLoader = new MockTransactionSnapshotLoader();
        timeLoader = new MockSystemTimeLoader();

        // Instantiate the new replicator.
        replicator = new Replicator(source, destination, snapshotLoader, timeLoader, 1, 0, 0);

        // We want the snapshot loader to return the same snapshot to simulate no database changes.
        snapshotLoader.getSnapshots().add(new TransactionSnapshot("100:200:110,112"));
        // But we want the clock time to have progressed.
        timeLoader.getTimes().add(buildDate("2009-10-11 12:13:15"));
        timeLoader.getTimes().add(buildDate("2009-10-11 12:13:15"));
        timeLoader.getTimes().add(buildDate("2009-10-11 12:13:15"));

        // Launch the replication process.
        replicator.replicate();

        // Verify that the final state does not match the initial state, but that the only
        // difference is the time and increment sequence number.
        finalState = destination.getCurrentState();
        assertFalse(finalState.equals(initialState), "Final state should not match initial state.");
        finalState.setTimestamp(initialState.getTimestamp());
        finalState.setSequenceNumber(finalState.getSequenceNumber() - 1);
        assertTrue(finalState.equals(initialState), "Final state should match initial state after updating timestamp.");

        // Verify that no changes were replicated.
        assertTrue(source.getPredicatesList().size() == 0, "No changes should have been replicated.");
    }

    /**
     * Tests replication behaviour when a simple replication interval is required.
     */
    @Test
    public void testSimpleIncrement() {
        Replicator replicator;
        MockReplicationSource source;
        MockReplicationDestination destination;
        MockTransactionSnapshotLoader snapshotLoader;
        MockSystemTimeLoader timeLoader;
        ReplicationState state;
        ReplicationQueryPredicates predicates;

        // Build initial replication state.
        state = new ReplicationState(
                200,
                200,
                Arrays.asList(new Long[] {}),
                Arrays.asList(new Long[] {}),
                buildDate("2009-10-11 12:13:14"),
                0);

        // Build the mocks.
        source = new MockReplicationSource();
        destination = new MockReplicationDestination(state);
        snapshotLoader = new MockTransactionSnapshotLoader();
        timeLoader = new MockSystemTimeLoader();

        // Instantiate the new replicator.
        replicator = new Replicator(source, destination, snapshotLoader, timeLoader, 1, 0, 0);

        // Set the snapshot loader to return a snapshot with higher xMax.
        snapshotLoader.getSnapshots().add(new TransactionSnapshot("100:220"));
        // We also want the clock time to have progressed.
        timeLoader.getTimes().add(buildDate("2009-10-11 12:13:15"));
        timeLoader.getTimes().add(buildDate("2009-10-11 12:13:15"));

        // Launch the replication process.
        replicator.replicate();

        // Verify that the final state is correct.
        state = destination.getCurrentState();
        assertEquals(
                new ReplicationState(
                        220,
                        220,
                        Arrays.asList(new Long[] {}),
                        Arrays.asList(new Long[] {}),
                        buildDate("2009-10-11 12:13:15"),
                        1),
                state,
                "Incorrect final state.");

        // Verify that the correct changes were replicated.
        assertTrue(source.getPredicatesList().size() == 1, "A single interval should have been replicated.");
        predicates = source.getPredicatesList().get(0);
        assertEquals(Collections.emptyList(), predicates.getActiveList(), "Incorrect active list.");
        assertEquals(Collections.emptyList(), predicates.getReadyList(), "Incorrect ready list.");
        assertEquals(200, predicates.getBottomTransactionId(), "Incorrect bottom transaction id.");
        assertEquals(220, predicates.getTopTransactionId(), "Incorrect top transaction id.");
    }

    /**
     * Tests replication behaviour when active list manipulation is required.
     */
    @Test
    public void testInFlightTxnIncrement() {
        Replicator replicator;
        MockReplicationSource source;
        MockReplicationDestination destination;
        MockTransactionSnapshotLoader snapshotLoader;
        MockSystemTimeLoader timeLoader;
        ReplicationState state;
        ReplicationQueryPredicates predicates;

        // Build initial replication state.
        state = new ReplicationState(
                200,
                200,
                Arrays.asList(new Long[] {180L, 185L}),
                Arrays.asList(new Long[] {}),
                buildDate("2009-10-11 12:13:14"),
                0);

        // Build the mocks.
        source = new MockReplicationSource();
        destination = new MockReplicationDestination(state);
        snapshotLoader = new MockTransactionSnapshotLoader();
        timeLoader = new MockSystemTimeLoader();

        // Instantiate the new replicator.
        replicator = new Replicator(source, destination, snapshotLoader, timeLoader, 1, 0, 0);

        // Set the snapshot loader to return a snapshot with higher xMax.
        snapshotLoader.getSnapshots().add(new TransactionSnapshot("100:220:185"));
        // We also want the clock time to have progressed.
        timeLoader.getTimes().add(buildDate("2009-10-11 12:13:15"));
        timeLoader.getTimes().add(buildDate("2009-10-11 12:13:15"));

        // Launch the replication process.
        replicator.replicate();

        // Verify that the final state is correct.
        state = destination.getCurrentState();
        assertEquals(
                new ReplicationState(
                        220,
                        220,
                        Arrays.asList(new Long[] {185L}),
                        Arrays.asList(new Long[] {}),
                        buildDate("2009-10-11 12:13:15"),
                        1),
                state,
                "Incorrect final state.");

        // Verify that the correct changes were replicated.
        assertTrue(source.getPredicatesList().size() == 1, "A single interval should have been replicated.");
        predicates = source.getPredicatesList().get(0);
        assertEquals(Arrays.asList(new Long[] {185L}), predicates.getActiveList(), "Incorrect active list.");
        assertEquals(Arrays.asList(new Long[] {180L}), predicates.getReadyList(), "Incorrect ready list.");
        assertEquals(200, predicates.getBottomTransactionId(), "Incorrect bottom transaction id.");
        assertEquals(220, predicates.getTopTransactionId(), "Incorrect top transaction id.");
    }

    /**
     * Tests replication behaviour when catching up from outage and some active transactions are overtaken.
     */
    @Test
    public void testOutageCatchupWithActiveTxns() {
        Replicator replicator;
        MockReplicationSource source;
        MockReplicationDestination destination;
        MockTransactionSnapshotLoader snapshotLoader;
        MockSystemTimeLoader timeLoader;
        ReplicationState state;
        ReplicationQueryPredicates predicates;

        // Build initial replication state.
        state = new ReplicationState(
                5,
                5,
                Arrays.asList(new Long[] {24000L, 26000L}),
                Arrays.asList(new Long[] {}),
                buildDate("2009-10-11 12:13:14"),
                0);

        // Build the mocks.
        source = new MockReplicationSource();
        destination = new MockReplicationDestination(state);
        snapshotLoader = new MockTransactionSnapshotLoader();
        timeLoader = new MockSystemTimeLoader();

        // Instantiate the new replicator.
        replicator = new Replicator(source, destination, snapshotLoader, timeLoader, 1, 0, 0);

        // Set the snapshot loader to return a snapshot with higher xMax.
        snapshotLoader.getSnapshots().add(new TransactionSnapshot("20000:30000:26000"));
        // We also want the clock time to have progressed.
        timeLoader.getTimes().add(buildDate("2009-10-11 12:13:15"));
        timeLoader.getTimes().add(buildDate("2009-10-11 12:13:15"));

        // Launch the replication process.
        replicator.replicate();

        // Verify that the final state is correct.
        state = destination.getCurrentState();
        assertEquals(
                new ReplicationState(
                        30000,
                        25005,
                        Arrays.asList(new Long[] {26000L}),
                        Arrays.asList(new Long[] {}),
                        buildDate("2009-10-11 12:13:14"),
                        1),
                state,
                "Incorrect final state.");

        // Verify that the correct changes were replicated.
        assertTrue(source.getPredicatesList().size() == 1, "A single interval should have been replicated.");
        predicates = source.getPredicatesList().get(0);
        assertEquals(Arrays.asList(new Long[] {26000L}), predicates.getActiveList(), "Incorrect active list.");
        assertEquals(Arrays.asList(new Long[] {}), predicates.getReadyList(), "Incorrect ready list.");
        assertEquals(5, predicates.getBottomTransactionId(), "Incorrect bottom transaction id.");
        assertEquals(25005, predicates.getTopTransactionId(), "Incorrect top transaction id.");
    }
}

// This software is released into the Public Domain.  See copying.txt for details.
package org.openstreetmap.osmosis.pgsimple.v0_6.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.openstreetmap.osmosis.core.util.FixedPrecisionCoordinateConvertor;
import org.openstreetmap.osmosis.pgsimple.common.InMemoryNodeLocationStore;
import org.openstreetmap.osmosis.pgsimple.common.NodeLocation;
import org.openstreetmap.osmosis.pgsimple.common.NodeLocationStore;
import org.openstreetmap.osmosis.pgsimple.common.PersistentNodeLocationStore;

/**
 * Tests the node location store implementations.
 *
 * @author Brett Hendersons
 */
public class NodeLocationStoreTest {

    private void testStoreImplementation(NodeLocationStore store) {
        // Add a large number of locations to the store.
        for (int i = 0; i < 100000; i++) {
            double longitude;
            double latitude;

            // Stores typically use fixed precision storage therefore ensure we
            // have a good spread of values.
            // The longitude and latitude must be different values to ensure they don't get mixed up.
            longitude = FixedPrecisionCoordinateConvertor.convertToDouble(1 << (i % 32));
            latitude = FixedPrecisionCoordinateConvertor.convertToDouble(1 << ((i + 1) % 32));

            // Add the location to the store but leave every node invalid.
            store.addLocation(i * 2, new NodeLocation(longitude, latitude));
        }

        // Verify that the data from the store matches.
        for (int i = 0; i < 100000; i++) {
            double longitude;
            double latitude;
            NodeLocation location;

            // Stores typically use fixed precision storage therefore ensure we
            // have a good spread of values.
            // The longitude and latitude must be different values to ensure they don't get mixed up.
            longitude = FixedPrecisionCoordinateConvertor.convertToDouble(1 << (i % 32));
            latitude = FixedPrecisionCoordinateConvertor.convertToDouble(1 << ((i + 1) % 32));

            location = store.getNodeLocation(i * 2);
            assertTrue(location.isValid(), "The node location should be valid.");
            assertEquals(longitude, location.getLongitude(), 0, "The longitude is incorrect.");
            assertEquals(latitude, location.getLatitude(), 0, "The latitude is incorrect.");

            location = store.getNodeLocation((i * 2) + 1);
            assertFalse(location.isValid(), "The node location should be invalid.");
        }

        store.close();
    }

    /**
     * Tests the temporary file implementation.
     */
    @Test
    public void testTempFile() {
        testStoreImplementation(new PersistentNodeLocationStore());
    }

    /**
     * Tests the in-memory implementation.
     */
    @Test
    public void testInMemory() {
        testStoreImplementation(new InMemoryNodeLocationStore());
    }
}

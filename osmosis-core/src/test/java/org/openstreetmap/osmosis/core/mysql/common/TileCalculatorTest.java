// This software is released into the Public Domain.  See copying.txt for details.
package org.openstreetmap.osmosis.core.mysql.common;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.openstreetmap.osmosis.core.util.TileCalculator;

/**
 * Tests the quad tile calculator.
 *
 * @author Brett Henderson
 */
public class TileCalculatorTest {
    /**
     * Basic test.
     */
    @Test
    public void test() {
        assertEquals(
                2062265654L,
                new TileCalculator().calculateTile(51.4781325, -0.1474929),
                "Incorrect tile value generated.");
    }
}

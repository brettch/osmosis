// This software is released into the Public Domain.  See copying.txt for details.
package org.openstreetmap.osmosis.testutil;

import java.nio.file.Path;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.io.TempDir;

/**
 * Convenience base class providing facilities for test data creation and
 * lifecycle management.
 *
 * @author Brett Henderson
 */
public class AbstractDataTest {
    @TempDir
    private Path tempDir;

    /**
     * Manages creation and lifecycle of test data files.
     */
    protected TestDataUtilities dataUtils;

    /**
     * Initialise data utilities.
     */
    @BeforeEach
    private void setUpDataUtils() {
        dataUtils = new TestDataUtilities(tempDir);
    }
}

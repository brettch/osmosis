// This software is released into the Public Domain.  See copying.txt for details.
package org.openstreetmap.osmosis.set.v0_6;

import java.io.File;
import org.junit.jupiter.api.Test;
import org.openstreetmap.osmosis.core.Osmosis;
import org.openstreetmap.osmosis.testutil.AbstractDataTest;

/**
 * Tests the flatten filter.
 *
 * @author Igor Podolskiy
 */
public class FlattenFilterTest extends AbstractDataTest {
    /**
     * Tests that a set of changes is simplified correctly.
     */
    @Test
    public void commonCase() {
        File sourceFile;
        File expectedOutputFile;
        File actualOutputFile;

        // Generate files.
        sourceFile = dataUtils.createDataFile("v0_6/flatten-in.osm");
        expectedOutputFile = dataUtils.createDataFile("v0_6/flatten-out.osm");
        actualOutputFile = dataUtils.newFile();

        Osmosis.run(new String[] {
            "-q", "--read-xml-0.6", sourceFile.getPath(), "--flatten-0.6", "--write-xml-0.6", actualOutputFile.getPath()
        });

        // Validate that the output file matches the expected result.
        dataUtils.compareFiles(expectedOutputFile, actualOutputFile);
    }

    /**
     * Tests that simplifying an already simple change successfully
     * yields the same change.
     */
    @Test
    public void alreadyFlattened() {
        File sourceFile;
        File expectedOutputFile;
        File actualOutputFile;

        sourceFile = dataUtils.createDataFile("v0_6/flatten-out.osm");
        expectedOutputFile = dataUtils.createDataFile("v0_6/flatten-out.osm");
        actualOutputFile = dataUtils.newFile();

        Osmosis.run(new String[] {
            "-q", "--read-xml-0.6", sourceFile.getPath(), "--flatten-0.6", "--write-xml-0.6", actualOutputFile.getPath()
        });

        dataUtils.compareFiles(expectedOutputFile, actualOutputFile);
    }

    /**
     * Tests that simplifying an empty change successfully
     * yields an empty change.
     */
    @Test
    public void empty() {
        File expectedOutputFile;
        File actualOutputFile;

        expectedOutputFile = dataUtils.createDataFile("v0_6/empty-entity.osm");
        actualOutputFile = dataUtils.newFile();

        Osmosis.run(
                new String[] {"-q", "--read-empty-0.6", "--flatten-0.6", "--write-xml-0.6", actualOutputFile.getPath()
                });

        dataUtils.compareFiles(expectedOutputFile, actualOutputFile);
    }
}

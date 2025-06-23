// This software is released into the Public Domain.  See copying.txt for details.
package org.openstreetmap.osmosis.tagfilter.v0_6;

import java.io.File;
import org.junit.jupiter.api.Test;
import org.openstreetmap.osmosis.core.Osmosis;
import org.openstreetmap.osmosis.testutil.AbstractDataTest;

/**
 * Tests for the WayKeyValueFilter class.
 *
 * @author Raluca Martinescu
 */
public class WayKeyValueFilterTest extends AbstractDataTest {

    /**
     * Tests the way key-value filter when allowed value pairs are read from
     * comma separated list of values.
     */
    @Test
    public final void testWayKeyValueFilterFromList() {
        testWayKeyValueFilter("keyValueList=box_type.lamp_box,box_type.wall");
    }

    /**
     * Tests the way key-value filter when allowed value pairs are read from
     * file.
     */
    @Test
    public final void testWayKeyValueFilterFromFile() {
        File allowedPairs = dataUtils.createDataFile("v0_6/allowed-key-values.txt");
        testWayKeyValueFilter("keyValueListFile=" + allowedPairs.getPath());
    }

    private void testWayKeyValueFilter(String keyValueListOption) {

        File inputFile = dataUtils.createDataFile("v0_6/way-key-value-filter-snapshot.osm");
        File expectedResultFile = dataUtils.createDataFile("v0_6/way-key-value-filter-expected.osm");
        File outputFile = dataUtils.newFile();

        // filter by key-value pairs
        Osmosis.run(new String[] {
            "-q",
            "--read-xml-0.6",
            inputFile.getPath(),
            "--way-key-value",
            keyValueListOption,
            "--write-xml-0.6",
            outputFile.getPath()
        });

        // Validate that the output file matches the expected file
        dataUtils.compareFiles(expectedResultFile, outputFile);
    }
}

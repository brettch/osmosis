// This software is released into the Public Domain.  See copying.txt for details.
package org.openstreetmap.osmosis.areafilter.v0_6;

import java.io.File;
import org.junit.jupiter.api.Test;
import org.openstreetmap.osmosis.core.Osmosis;
import org.openstreetmap.osmosis.testutil.AbstractDataTest;

/**
 * Tests the area filter implementation.
 */
public class AreaFilterTest extends AbstractDataTest {

    /**
     * A basic test verifying that the area filter includes all data when the complete planet is selected.
     */
    @Test
    public void testEntirePlanet() {
        File inputFile;
        File expectedOutputFile;
        File actualOutputFile;

        // Generate input files.
        inputFile = dataUtils.createDataFile("v0_6/areafilter-in.osm");
        expectedOutputFile = dataUtils.createDataFile("v0_6/areafilter-out-whole.osm");
        actualOutputFile = dataUtils.newFile();

        // Load the database with a dataset.
        Osmosis.run(new String[] {
            "-q",
            "--read-xml-0.6",
            inputFile.getPath(),
            "--bounding-box",
            "--tag-sort-0.6",
            "--write-xml-0.6",
            actualOutputFile.getPath()
        });

        // Validate that the output file matches the input file.
        dataUtils.compareFiles(expectedOutputFile, actualOutputFile);
    }

    /**
     * Performs a standard bounding box filter.
     */
    @Test
    public void testBboxFilterStandard() {
        File inputFile;
        File expectedOutputFile;
        File actualOutputFile;

        // Generate input files.
        inputFile = dataUtils.createDataFile("v0_6/areafilter-in.osm");
        expectedOutputFile = dataUtils.createDataFile("v0_6/areafilter-out-standard.osm");
        actualOutputFile = dataUtils.newFile();

        // Load the database with a dataset.
        Osmosis.run(new String[] {
            "-q",
            "--read-xml-0.6",
            inputFile.getPath(),
            "--bounding-box",
            "left=-10",
            "top=10",
            "right=10",
            "bottom=-10",
            "--tag-sort-0.6",
            "--write-xml-0.6",
            actualOutputFile.getPath()
        });

        // Validate that the output file matches the input file.
        dataUtils.compareFiles(expectedOutputFile, actualOutputFile);
    }

    /**
     * Performs a standard bounding box filter with the cascadingRelations feature added.
     */
    @Test
    public void testBboxFilterCascadingRelations() {
        File inputFile;
        File expectedOutputFile;
        File actualOutputFile;

        // Generate input files.
        inputFile = dataUtils.createDataFile("v0_6/areafilter-in.osm");
        expectedOutputFile = dataUtils.createDataFile("v0_6/areafilter-out-cascadingrelations.osm");
        actualOutputFile = dataUtils.newFile();

        // Load the database with a dataset.
        Osmosis.run(new String[] {
            "-q",
            "--read-xml-0.6",
            inputFile.getPath(),
            "--bounding-box",
            "cascadingRelations=yes",
            "left=-10",
            "top=10",
            "right=10",
            "bottom=-10",
            "--tag-sort-0.6",
            "--write-xml-0.6",
            actualOutputFile.getPath()
        });

        // Validate that the output file matches the input file.
        dataUtils.compareFiles(expectedOutputFile, actualOutputFile);
    }

    /**
     * Performs a bounding box filter with the completeWays option enabled.
     */
    @Test
    public void testBboxFilterCompleteWays() {
        File inputFile;
        File expectedOutputFile;
        File actualOutputFile;

        // Generate input files.
        inputFile = dataUtils.createDataFile("v0_6/areafilter-in.osm");
        expectedOutputFile = dataUtils.createDataFile("v0_6/areafilter-out-completeways.osm");
        actualOutputFile = dataUtils.newFile();

        // Load the database with a dataset.
        Osmosis.run(new String[] {
            "-q",
            "--read-xml-0.6",
            inputFile.getPath(),
            "--bounding-box",
            "completeWays=yes",
            "left=-10",
            "top=10",
            "right=10",
            "bottom=-10",
            "--tag-sort-0.6",
            "--write-xml-0.6",
            actualOutputFile.getPath()
        });

        // Validate that the output file matches the input file.
        dataUtils.compareFiles(expectedOutputFile, actualOutputFile);
    }

    /**
     * Performs a bounding box filter with the completeRelations option enabled.
     */
    @Test
    public void testBboxFilterCompleteRelations() {
        File inputFile;
        File expectedOutputFile;
        File actualOutputFile;

        // Generate input files.
        inputFile = dataUtils.createDataFile("v0_6/areafilter-in.osm");
        expectedOutputFile = dataUtils.createDataFile("v0_6/areafilter-out-completerelations.osm");
        actualOutputFile = dataUtils.newFile();

        // Load the database with a dataset.
        Osmosis.run(new String[] {
            "-q",
            "--read-xml-0.6",
            inputFile.getPath(),
            "--bounding-box",
            "completeRelations=yes",
            "left=-10",
            "top=10",
            "right=10",
            "bottom=-10",
            "--tag-sort-0.6",
            "--write-xml-0.6",
            actualOutputFile.getPath()
        });

        // Validate that the output file matches the input file.
        dataUtils.compareFiles(expectedOutputFile, actualOutputFile);
    }

    /**
     * Performs a bounding box filter with the clipIncompleteEntities option enabled.
     */
    @Test
    public void testBboxFilterClipIncompleteEntities() {
        File inputFile;
        File expectedOutputFile;
        File actualOutputFile;

        // Generate input files.
        inputFile = dataUtils.createDataFile("v0_6/areafilter-in.osm");
        expectedOutputFile = dataUtils.createDataFile("v0_6/areafilter-out-clipincompleteentities.osm");
        actualOutputFile = dataUtils.newFile();

        // Load the database with a dataset.
        Osmosis.run(new String[] {
            "-q",
            "--read-xml-0.6",
            inputFile.getPath(),
            "--bounding-box",
            "clipIncompleteEntities=yes",
            "left=-10",
            "top=10",
            "right=10",
            "bottom=-10",
            "--tag-sort-0.6",
            "--write-xml-0.6",
            actualOutputFile.getPath()
        });

        // Validate that the output file matches the input file.
        dataUtils.compareFiles(expectedOutputFile, actualOutputFile);
    }
}

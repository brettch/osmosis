// This software is released into the Public Domain.  See copying.txt for details.
package org.openstreetmap.osmosis.pgsimple.v0_6;

import java.io.File;
import org.junit.jupiter.api.Test;
import org.openstreetmap.osmosis.core.Osmosis;
import org.openstreetmap.osmosis.testutil.AbstractDataTest;

/**
 * Tests for PostgreSQL tasks.
 *
 * @author Brett Henderson
 */
public class PostgreSqlTest extends AbstractDataTest {

    private File getAuthFile() {
        return dataUtils.createDataFile("db.pgsql.authfile", "v0_6/pgsql-authfile.txt");
    }

    /**
     * A basic test loading an osm file into a pgsql database, then dumping it
     * again and verifying that it is identical.
     */
    @Test
    public void testLoadAndDump() {
        File authFile;
        File inputFile;
        File outputFile;

        // Generate input files.
        authFile = getAuthFile();
        inputFile = dataUtils.createDataFile("v0_6/db-snapshot.osm");
        outputFile = dataUtils.newFile();

        // Remove all existing data from the database.
        Osmosis.run(new String[] {"-q", "--truncate-pgsimp-0.6", "authFile=" + authFile.getPath()});

        // Load the database with a dataset.
        Osmosis.run(new String[] {
            "-q", "--read-xml-0.6", inputFile.getPath(), "--write-pgsimp-0.6", "authFile=" + authFile.getPath()
        });

        // Dump the database to an osm file.
        Osmosis.run(new String[] {
            "-q",
            "--read-pgsimp-0.6",
            "authFile=" + authFile.getPath(),
            "--dataset-dump-0.6",
            "--tag-sort-0.6",
            "--write-xml-0.6",
            outputFile.getPath()
        });

        // Validate that the output file matches the input file.
        dataUtils.compareFiles(inputFile, outputFile);
    }

    /**
     * A basic test loading an osm file into a pgsql database using the COPY
     * file approach, then dumping it again and verifying that it is identical.
     */
    @Test
    public void testFastLoadAndDump() {
        File authFile;
        File inputFile;
        File outputFile;

        // Generate input files.
        authFile = getAuthFile();
        inputFile = dataUtils.createDataFile("v0_6/db-snapshot.osm");
        outputFile = dataUtils.newFile();

        // Remove all existing data from the database.
        Osmosis.run(new String[] {"-q", "--truncate-pgsimp-0.6", "authFile=" + authFile.getPath()});

        // Load the database with a dataset.
        Osmosis.run(new String[] {
            "-q", "--read-xml-0.6", inputFile.getPath(), "--fast-write-pgsimp-0.6", "authFile=" + authFile.getPath()
        });

        // Dump the database to an osm file.
        Osmosis.run(new String[] {
            "-q",
            "--read-pgsimp-0.6",
            "authFile=" + authFile.getPath(),
            "--dataset-dump-0.6",
            "--tag-sort-0.6",
            "--write-xml-0.6",
            outputFile.getPath()
        });

        // Validate that the output file matches the input file.
        dataUtils.compareFiles(inputFile, outputFile);
    }

    /**
     * A test loading an osm file into a pgsql database, then applying a
     * changeset, then dumping it again and verifying the output is as expected.
     */
    @Test
    public void testChangeset() {
        File authFile;
        File snapshotFile;
        File changesetFile;
        File expectedResultFile;
        File actualResultFile;

        // Generate input files.
        authFile = getAuthFile();
        snapshotFile = dataUtils.createDataFile("v0_6/db-snapshot.osm");
        changesetFile = dataUtils.createDataFile("v0_6/db-changeset.osc");
        expectedResultFile = dataUtils.createDataFile("v0_6/db-changeset-expected.osm");
        actualResultFile = dataUtils.newFile();

        // Remove all existing data from the database.
        Osmosis.run(new String[] {"-q", "--truncate-pgsimp-0.6", "authFile=" + authFile.getPath()});

        // Load the database with the snapshot file.
        Osmosis.run(new String[] {
            "-q", "--read-xml-0.6", snapshotFile.getPath(), "--write-pgsimp-0.6", "authFile=" + authFile.getPath()
        });

        // Apply the changeset file to the database.
        Osmosis.run(new String[] {
            "-q",
            "--read-xml-change-0.6",
            changesetFile.getPath(),
            "--write-pgsimp-change-0.6",
            "authFile=" + authFile.getPath()
        });

        // Dump the database to an osm file.
        Osmosis.run(new String[] {
            "-q",
            "--read-pgsimp-0.6",
            "authFile=" + authFile.getPath(),
            "--dataset-dump-0.6",
            "--tag-sort-0.6",
            "--write-xml-0.6",
            actualResultFile.getPath()
        });

        // Validate that the dumped file matches the expected result.
        dataUtils.compareFiles(expectedResultFile, actualResultFile);
    }

    /**
     * A test loading an osm file into a pgsql database, then making some modifications via the
     * dataset api, then dumping it again and verifying the output is as expected.
     */
    @Test
    public void testDataset() {
        File authFile;
        File snapshotFile;
        File expectedResultFile;
        File actualResultFile;

        // Generate input files.
        authFile = getAuthFile();
        snapshotFile = dataUtils.createDataFile("v0_6/db-snapshot.osm");
        expectedResultFile = dataUtils.createDataFile("v0_6/db-dataset-expected.osm");
        actualResultFile = dataUtils.newFile();

        // Remove all existing data from the database.
        Osmosis.run(new String[] {"-q", "--truncate-pgsimp-0.6", "authFile=" + authFile.getPath()});

        // Load the database with the snapshot file.
        Osmosis.run(new String[] {
            "-q", "--read-xml-0.6", snapshotFile.getPath(), "--write-pgsimp-0.6", "authFile=" + authFile.getPath()
        });

        // Invoke the dataset driver task task to manipulate the database.
        Osmosis.run(new String[] {
            "-q",
            "-p",
            DatasetDriverPlugin.class.getName(),
            "--read-pgsimp-0.6",
            "authFile=" + authFile.getPath(),
            "--drive-dataset"
        });

        // Dump the database to an osm file.
        Osmosis.run(new String[] {
            "-q",
            "--read-pgsimp-0.6",
            "authFile=" + authFile.getPath(),
            "--dataset-dump-0.6",
            "--tag-sort-0.6",
            "--write-xml-0.6",
            actualResultFile.getPath()
        });

        // Validate that the dumped file matches the expected result.
        dataUtils.compareFiles(expectedResultFile, actualResultFile);
    }
}

// This software is released into the Public Domain.  See copying.txt for details.
package org.openstreetmap.osmosis.pgsnapshot.v0_6;

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

    private File getAuthFile(String name) {
        return dataUtils.createDataFile("db.pgsql.authfile", name);
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
        authFile = getAuthFile("v0_6/pgsql-authfile.txt");
        inputFile = dataUtils.createDataFile("v0_6/db-snapshot.osm");
        outputFile = dataUtils.newFile();

        // Remove all existing data from the database.
        Osmosis.run(new String[] {"-q", "--truncate-pgsql-0.6", "authFile=" + authFile.getPath()});

        // Load the database with a dataset.
        Osmosis.run(new String[] {
            "-q", "--read-xml-0.6", inputFile.getPath(), "--write-pgsql-0.6", "authFile=" + authFile.getPath()
        });

        // Dump the database to an osm file.
        Osmosis.run(new String[] {
            "-q",
            "--read-pgsql-0.6",
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
        authFile = getAuthFile("v0_6/pgsql-authfile.txt");
        snapshotFile = dataUtils.createDataFile("v0_6/db-snapshot.osm");
        changesetFile = dataUtils.createDataFile("v0_6/db-changeset.osc");
        expectedResultFile = dataUtils.createDataFile("v0_6/db-changeset-expected.osm");
        actualResultFile = dataUtils.newFile();

        // Remove all existing data from the database.
        Osmosis.run(new String[] {"-q", "--truncate-pgsql-0.6", "authFile=" + authFile.getPath()});

        // Load the database with the snapshot file.
        Osmosis.run(new String[] {
            "-q", "--read-xml-0.6", snapshotFile.getPath(), "--write-pgsql-0.6", "authFile=" + authFile.getPath()
        });

        // Apply the changeset file to the database.
        Osmosis.run(new String[] {
            "-q",
            "--read-xml-change-0.6",
            changesetFile.getPath(),
            "--write-pgsql-change-0.6",
            "keepInvalidWays=false",
            "authFile=" + authFile.getPath()
        });

        // Dump the database to an osm file.
        Osmosis.run(new String[] {
            "-q",
            "--read-pgsql-0.6",
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
        authFile = getAuthFile("v0_6/pgsql-authfile.txt");
        snapshotFile = dataUtils.createDataFile("v0_6/db-snapshot.osm");
        expectedResultFile = dataUtils.createDataFile("v0_6/db-dataset-expected.osm");
        actualResultFile = dataUtils.newFile();

        // Remove all existing data from the database.
        Osmosis.run(new String[] {"-q", "--truncate-pgsql-0.6", "authFile=" + authFile.getPath()});

        // Load the database with the snapshot file.
        Osmosis.run(new String[] {
            "-q", "--read-xml-0.6", snapshotFile.getPath(), "--write-pgsql-0.6", "authFile=" + authFile.getPath()
        });

        // Invoke the dataset driver task task to manipulate the database.
        Osmosis.run(new String[] {
            "-q",
            "-p",
            DatasetDriverPlugin.class.getName(),
            "--read-pgsql-0.6",
            "authFile=" + authFile.getPath(),
            "--drive-dataset"
        });

        // Dump the database to an osm file.
        Osmosis.run(new String[] {
            "-q",
            "--read-pgsql-0.6",
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
     * A test loading an osm file into a pgsql database, then reading it via a
     * dataset bounding box covering the entire planet and verifying the output
     * is as expected.
     */
    @Test
    public void testDatasetBoundingBox() {
        File authFile;
        File inputFile;
        File outputFile;

        // Generate input files.
        authFile = getAuthFile("v0_6/pgsql-authfile.txt");
        inputFile = dataUtils.createDataFile("v0_6/db-snapshot.osm");
        outputFile = dataUtils.newFile();

        // Remove all existing data from the database.
        Osmosis.run(new String[] {"-q", "--truncate-pgsql-0.6", "authFile=" + authFile.getPath()});

        // Load the database with a dataset.
        Osmosis.run(new String[] {
            "-q", "--read-xml-0.6", inputFile.getPath(), "--write-pgsql-0.6", "authFile=" + authFile.getPath()
        });

        // Dump the database to an osm file.
        Osmosis.run(new String[] {
            "-q",
            "--read-pgsql-0.6",
            "authFile=" + authFile.getPath(),
            "--dataset-bounding-box-0.6",
            "completeWays=true",
            "--tag-sort-0.6",
            "--write-xml-0.6",
            outputFile.getPath()
        });

        // Validate that the output file matches the input file.
        dataUtils.compareFiles(inputFile, outputFile);
    }

    /**
     * A test loading an osm file into a pgsql database with a schema, then dumping it
     * again and verifying that it is identical.
     */
    @Test
    public void testLoadAndDumpWithSchema() {
        File authFile;
        File inputFile;
        File outputFile;

        // Generate input files.
        authFile = getAuthFile("v0_6/pgsql_with_schema-authfile.txt");
        inputFile = dataUtils.createDataFile("v0_6/db-snapshot.osm");
        outputFile = dataUtils.newFile();

        // Remove all existing data from the database.
        Osmosis.run(new String[] {
            "-q", "--truncate-pgsql-0.6", "postgresSchema=test_schema", "authFile=" + authFile.getPath()
        });

        // Load the database with a dataset.
        Osmosis.run(new String[] {
            "-q",
            "--read-xml-0.6",
            inputFile.getPath(),
            "--write-pgsql-0.6",
            "postgresSchema=test_schema",
            "authFile=" + authFile.getPath()
        });

        // Dump the database to an osm file.
        Osmosis.run(new String[] {
            "-q",
            "--read-pgsql-0.6",
            "authFile=" + authFile.getPath(),
            "postgresSchema=test_schema",
            "--dataset-dump-0.6",
            "--tag-sort-0.6",
            "--write-xml-0.6",
            outputFile.getPath()
        });

        // Validate that the output file matches the input file.
        dataUtils.compareFiles(inputFile, outputFile);
    }
}

// This software is released into the Public Domain.  See copying.txt for details.
package org.openstreetmap.osmosis.apidb.v0_6;

import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openstreetmap.osmosis.apidb.v0_6.impl.DatabaseUtilities;
import org.openstreetmap.osmosis.core.Osmosis;
import org.openstreetmap.osmosis.testutil.AbstractDataTest;

/**
 * Tests for PostgreSQL tasks.
 *
 * @author Brett Henderson
 */
public class ApiDbTest extends AbstractDataTest {

    private static final String DATE_FORMAT = "yyyy-MM-dd_HH:mm:ss";

    private DatabaseUtilities dbUtils;

    @BeforeEach
    private void setUpDbUtils() {
        dbUtils = new DatabaseUtilities(dataUtils);
    }

    private String convertUTCTimeToLocalTime(String dateString) {
        DateFormat inFormat;
        DateFormat outFormat;
        Date date;

        inFormat = new SimpleDateFormat(DATE_FORMAT, Locale.US);
        inFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        outFormat = new SimpleDateFormat(DATE_FORMAT, Locale.US);

        try {
            date = inFormat.parse(dateString);
        } catch (ParseException e) {
            throw new RuntimeException("Failed to parse date: " + dateString, e);
        }

        return outFormat.format(date);
    }

    /**
     * A basic test loading an osm file into a mysql database, then dumping it again and verifying
     * that it is identical.
     */
    @Test
    public void testLoadAndDump() {
        File authFile;
        File inputFile;
        File outputFile;

        // Generate input files.
        authFile = dbUtils.getAuthorizationFile();
        inputFile = dataUtils.createDataFile("v0_6/db-snapshot.osm");
        outputFile = dataUtils.newFile();

        // Remove all existing data from the database.
        dbUtils.truncateDatabase();

        // Load the database with a dataset.
        Osmosis.run(new String[] {
            "-q",
            "--read-xml-0.6",
            inputFile.getPath(),
            "--write-apidb-0.6",
            "authFile=" + authFile.getPath(),
            "allowIncorrectSchemaVersion=true"
        });

        // Dump the database to an osm file.
        Osmosis.run(new String[] {
            "-q",
            "--read-apidb-0.6",
            "authFile=" + authFile.getPath(),
            "allowIncorrectSchemaVersion=true",
            "--tag-sort-0.6",
            "--write-xml-0.6",
            outputFile.getPath()
        });

        // Validate that the output file matches the input file.
        dataUtils.compareFiles(inputFile, outputFile);
    }

    /**
     * A basic test loading an osm file into a apidb database, then dumping it from current tables
     * and verifying that it is identical.
     */
    @Test
    public void testLoadAndCurrentDump() {
        File authFile;
        File inputFile;
        File outputFile;

        // Generate input files.
        authFile = dbUtils.getAuthorizationFile();
        inputFile = dataUtils.createDataFile("v0_6/db-snapshot.osm");
        outputFile = dataUtils.newFile();

        // Remove all existing data from the database.
        dbUtils.truncateDatabase();

        // Load the database with a dataset.
        Osmosis.run(new String[] {
            "-q",
            "--read-xml-0.6",
            inputFile.getPath(),
            "--write-apidb-0.6",
            "authFile=" + authFile.getPath(),
            "allowIncorrectSchemaVersion=true"
        });

        // Dump the database to an osm file.
        Osmosis.run(new String[] {
            "-q",
            "--read-apidb-current-0.6",
            "authFile=" + authFile.getPath(),
            "allowIncorrectSchemaVersion=true",
            "--tag-sort-0.6",
            "--write-xml-0.6",
            outputFile.getPath()
        });

        // Validate that the output file matches the input file.
        dataUtils.compareFiles(inputFile, outputFile);
    }

    /**
     * A test loading an osm file into a apidb database, then applying a changeset, then dumping it
     * again and verifying the output is as expected.
     */
    @Test
    public void testApplyChangeset() {
        File authFile;
        File snapshotFile;
        File changesetFile;
        File expectedResultFile;
        File actualResultFile;

        // Generate input files.
        authFile = dbUtils.getAuthorizationFile();
        snapshotFile = dataUtils.createDataFile("v0_6/db-snapshot.osm");
        changesetFile = dataUtils.createDataFile("v0_6/db-changeset.osc");
        expectedResultFile = dataUtils.createDataFile("v0_6/db-changeset-expected.osm");
        actualResultFile = dataUtils.newFile();

        // Remove all existing data from the database.
        dbUtils.truncateDatabase();

        // Load the database with the snapshot file.
        Osmosis.run(new String[] {
            "-q",
            "--read-xml-0.6",
            snapshotFile.getPath(),
            "--write-apidb-0.6",
            "authFile=" + authFile.getPath(),
            "allowIncorrectSchemaVersion=true"
        });

        // Apply the changeset file to the database.
        Osmosis.run(new String[] {
            "-q",
            "--read-xml-change-0.6",
            changesetFile.getPath(),
            "--write-apidb-change-0.6",
            "authFile=" + authFile.getPath(),
            "allowIncorrectSchemaVersion=true"
        });

        // Dump the database to an osm file.
        Osmosis.run(new String[] {
            "-q",
            "--read-apidb-0.6",
            "authFile=" + authFile.getPath(),
            "allowIncorrectSchemaVersion=true",
            "--tag-sort-0.6",
            "--write-xml-0.6",
            actualResultFile.getPath()
        });

        // Validate that the dumped file matches the expected result.
        dataUtils.compareFiles(expectedResultFile, actualResultFile);
    }

    /**
     * A test loading an osm file into a apidb database, then applying a changeset, then dumping the
     * original snapshot timeframe and verifying the output is as expected.
     */
    @Test
    public void testSnapshotDump() {
        File authFile;
        File snapshotFile;
        File changesetFile;
        File expectedResultFile;
        File actualResultFile;

        // Generate input files.
        authFile = dbUtils.getAuthorizationFile();
        snapshotFile = dataUtils.createDataFile("v0_6/db-snapshot.osm");
        changesetFile = dataUtils.createDataFile("v0_6/db-changeset.osc");
        expectedResultFile = dataUtils.createDataFile("v0_6/db-snapshot-b.osm");
        actualResultFile = dataUtils.newFile();

        // Remove all existing data from the database.
        dbUtils.truncateDatabase();

        // Load the database with the snapshot file.
        Osmosis.run(new String[] {
            "-q",
            "--read-xml-0.6",
            snapshotFile.getPath(),
            "--write-apidb-0.6",
            "authFile=" + authFile.getPath(),
            "allowIncorrectSchemaVersion=true"
        });

        // Apply the changeset file to the database.
        Osmosis.run(new String[] {
            "-q",
            "--read-xml-change-0.6",
            changesetFile.getPath(),
            "--write-apidb-change-0.6",
            "authFile=" + authFile.getPath(),
            "allowIncorrectSchemaVersion=true"
        });

        // Dump the database to an osm file.
        Osmosis.run(new String[] {
            "-q",
            "--read-apidb-0.6",
            "snapshotInstant=" + convertUTCTimeToLocalTime("2008-01-03_00:00:00"),
            "authFile=" + authFile.getPath(),
            "allowIncorrectSchemaVersion=true",
            "--tag-sort-0.6",
            "--write-xml-0.6",
            actualResultFile.getPath()
        });

        // Validate that the dumped file matches the expected result.
        dataUtils.compareFiles(expectedResultFile, actualResultFile);
    }

    /**
     * A test loading an osm file into a apidb database, then applying a changeset, then extracting
     * the changeset timeframe and verifying the output is as expected.
     */
    @Test
    public void testChangesetDump() {
        File authFile;
        File snapshotFile;
        File changesetFile;
        File expectedResultFile;
        File actualResultFile;

        // Generate input files.
        authFile = dbUtils.getAuthorizationFile();
        snapshotFile = dataUtils.createDataFile("v0_6/db-snapshot.osm");
        changesetFile = dataUtils.createDataFile("v0_6/db-changeset.osc");
        expectedResultFile = dataUtils.createDataFile("v0_6/db-changeset-b.osc");
        actualResultFile = dataUtils.newFile();

        // Remove all existing data from the database.
        dbUtils.truncateDatabase();

        // Load the database with the snapshot file.
        Osmosis.run(new String[] {
            "-q",
            "--read-xml-0.6",
            snapshotFile.getPath(),
            "--write-apidb-0.6",
            "authFile=" + authFile.getPath(),
            "allowIncorrectSchemaVersion=true"
        });

        // Apply the changeset file to the database.
        Osmosis.run(new String[] {
            "-q",
            "--read-xml-change-0.6",
            changesetFile.getPath(),
            "--write-apidb-change-0.6",
            "authFile=" + authFile.getPath(),
            "allowIncorrectSchemaVersion=true"
        });

        // Dump the changeset to an osm file.
        Osmosis.run(new String[] {
            "-q",
            "--read-apidb-change-0.6",
            "intervalBegin=" + convertUTCTimeToLocalTime("2008-01-03_00:00:00"),
            "intervalEnd=" + convertUTCTimeToLocalTime("2008-01-04_00:00:00"),
            "authFile=" + authFile.getPath(),
            "allowIncorrectSchemaVersion=true",
            "--write-xml-change-0.6",
            actualResultFile.getPath()
        });

        // Validate that the dumped file matches the expected result.
        dataUtils.compareFiles(expectedResultFile, actualResultFile);
    }
}

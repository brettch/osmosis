// This software is released into the Public Domain.  See copying.txt for details.
package org.openstreetmap.osmosis.extract.apidb.v0_6;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.util.Date;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openstreetmap.osmosis.extract.apidb.common.Configuration;
import org.openstreetmap.osmosis.testutil.AbstractDataTest;

/**
 * Tests the operation of the database system time loader.
 *
 * @author Brett Henderson
 */
public class DatabaseTimeLoaderTest extends AbstractDataTest {
    private DatabaseUtilities dbUtils;

    @BeforeEach
    private void setUpDbUtils() {
        dbUtils = new DatabaseUtilities(dataUtils);
    }

    /**
     * Tests getting the current time from the database.
     */
    @Test
    public void testGetTime() {
        File authFile;
        Configuration config;
        DatabaseTimeLoader timeLoader;
        Date systemTime;
        Date databaseTime;
        long difference;

        authFile = dbUtils.getAuthorizationFile();
        config = new Configuration(authFile);
        timeLoader = new DatabaseTimeLoader(config.getDatabaseLoginCredentials());

        databaseTime = timeLoader.getDatabaseTime();
        systemTime = new Date();
        difference = databaseTime.getTime() - systemTime.getTime();

        assertTrue(
                difference > -1000 && difference < 1000,
                "Database time is different to system time, databaseTime=" + databaseTime + ", systemTime=" + systemTime
                        + ".");
    }
}

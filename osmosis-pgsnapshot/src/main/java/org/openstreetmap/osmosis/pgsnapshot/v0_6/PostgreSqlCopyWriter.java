// This software is released into the Public Domain.  See copying.txt for details.
package org.openstreetmap.osmosis.pgsnapshot.v0_6;

import java.util.Map;
import java.util.logging.Logger;
import org.openstreetmap.osmosis.core.container.v0_6.EntityContainer;
import org.openstreetmap.osmosis.core.database.DatabaseLoginCredentials;
import org.openstreetmap.osmosis.core.database.DatabasePreferences;
import org.openstreetmap.osmosis.core.task.v0_6.Sink;
import org.openstreetmap.osmosis.pgsnapshot.common.DatabaseContext;
import org.openstreetmap.osmosis.pgsnapshot.common.NodeLocationStoreType;
import org.openstreetmap.osmosis.pgsnapshot.v0_6.impl.CopyFilesetBuilder;
import org.openstreetmap.osmosis.pgsnapshot.v0_6.impl.CopyFilesetLoader;
import org.openstreetmap.osmosis.pgsnapshot.v0_6.impl.DatabaseCapabilityChecker;
import org.openstreetmap.osmosis.pgsnapshot.v0_6.impl.TempCopyFileset;

/**
 * An OSM data sink for storing all data to a database using the COPY command.
 * This task is intended for writing to an empty database.
 *
 * @author Brett Henderson
 */
public class PostgreSqlCopyWriter implements Sink {

    private static final Logger LOG = Logger.getLogger(PostgreSqlCopyWriter.class.getName());

    private CopyFilesetBuilder copyFilesetBuilder;
    private CopyFilesetLoader copyFilesetLoader;
    private TempCopyFileset copyFileset;
    private DatabaseLoginCredentials loginCredentials;
    private DatabasePreferences preferences;
    private NodeLocationStoreType storeType;
    private boolean populateBbox;
    private boolean populateLinestring;
    private boolean enableKeepPartialLinestring;
    private boolean keepInvalidWays;
    private boolean initialized;

    /**
     * Creates a new instance.
     *
     * @param loginCredentials
     *            Contains all information required to connect to the database.
     * @param preferences
     *            Contains preferences configuring database behaviour.
     * @param storeType
     *            The node location storage type used by the geometry builders.
     * @param enableKeepPartialLinestring
     *            If true, the way linestring is build even on invalid or missing
     *            nodes.
     * @param keepInvalidWays
     *            If true, zero and single node ways are kept. Otherwise they are
     *            silently dropped to avoid putting invalid geometries into the
     *            database which can cause problems with postgis functions.
     */
    public PostgreSqlCopyWriter(
            DatabaseLoginCredentials loginCredentials,
            DatabasePreferences preferences,
            boolean enableKeepPartialLinestring,
            NodeLocationStoreType storeType,
            boolean keepInvalidWays) {
        this.loginCredentials = loginCredentials;
        this.preferences = preferences;
        this.storeType = storeType;
        this.enableKeepPartialLinestring = enableKeepPartialLinestring;
        this.keepInvalidWays = keepInvalidWays;

        copyFileset = new TempCopyFileset();
    }

    private void initialize() {
        if (!initialized) {
            LOG.fine("Initializing the database and temporary processing files.");

            try (DatabaseContext dbCtx = new DatabaseContext(loginCredentials)) {
                DatabaseCapabilityChecker capabilityChecker = new DatabaseCapabilityChecker(dbCtx);

                populateBbox = capabilityChecker.isWayBboxSupported();
                populateLinestring = capabilityChecker.isWayLinestringSupported();
            }

            copyFilesetBuilder = new CopyFilesetBuilder(
                    copyFileset,
                    populateBbox,
                    populateLinestring,
                    enableKeepPartialLinestring,
                    storeType,
                    keepInvalidWays);

            copyFilesetLoader = new CopyFilesetLoader(loginCredentials, preferences, copyFileset);

            LOG.fine("Processing input data, building geometries and creating database load files.");

            initialized = true;
        }
    }

    /**
     * {@inheritDoc}
     */
    public void initialize(Map<String, Object> metaData) {
        // Do nothing.
    }

    /**
     * {@inheritDoc}
     */
    public void process(EntityContainer entityContainer) {
        initialize();

        copyFilesetBuilder.process(entityContainer);
    }

    /**
     * Writes any buffered data to the files, then loads the files into the database.
     */
    public void complete() {
        initialize();

        copyFilesetBuilder.complete();

        LOG.fine("All data has been received, beginning database load.");
        copyFilesetLoader.run();

        LOG.fine("Processing complete.");
    }

    /**
     * Releases all database resources.
     */
    public void close() {
        if (copyFilesetBuilder != null) {
            copyFilesetBuilder.close();
            copyFilesetBuilder = null;
        }
        copyFileset.close();

        initialized = false;
    }
}

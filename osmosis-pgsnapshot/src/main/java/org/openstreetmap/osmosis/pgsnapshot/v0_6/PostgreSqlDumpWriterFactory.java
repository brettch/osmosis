// This software is released into the Public Domain.  See copying.txt for details.
package org.openstreetmap.osmosis.pgsnapshot.v0_6;

import java.io.File;
import org.openstreetmap.osmosis.core.pipeline.common.TaskConfiguration;
import org.openstreetmap.osmosis.core.pipeline.common.TaskManager;
import org.openstreetmap.osmosis.core.pipeline.common.TaskManagerFactory;
import org.openstreetmap.osmosis.core.pipeline.v0_6.SinkManager;
import org.openstreetmap.osmosis.pgsnapshot.common.NodeLocationStoreType;

/**
 * The task manager factory for a database dump writer.
 *
 * @author Brett Henderson
 */
public class PostgreSqlDumpWriterFactory extends TaskManagerFactory {
    private static final String ARG_ENABLE_BBOX_BUILDER = "enableBboxBuilder";
    private static final String ARG_ENABLE_LINESTRING_BUILDER = "enableLinestringBuilder";
    private static final String ARG_ENABLE_KEEP_PARTIAL_LINESTRING = "enableKeepPartialLinestring";
    private static final String ARG_KEEP_INVALID_WAYS = "keepInvalidWays";
    private static final String ARG_FILE_NAME = "directory";
    private static final String ARG_NODE_LOCATION_STORE_TYPE = "nodeLocationStoreType";
    private static final boolean DEFAULT_ENABLE_BBOX_BUILDER = false;
    private static final boolean DEFAULT_ENABLE_LINESTRING_BUILDER = false;
    private static final boolean DEFAULT_ENABLE_KEEP_PARTIAL_LINESTRING = false;
    private static final boolean DEFAULT_KEEP_INVALID_WAYS = true;
    private static final String DEFAULT_FILE_PREFIX = "pgimport";
    private static final String DEFAULT_NODE_LOCATION_STORE_TYPE = "CompactTempFile";

    /**
     * {@inheritDoc}
     */
    @Override
    protected TaskManager createTaskManagerImpl(TaskConfiguration taskConfig) {
        String filePrefixString;
        File filePrefix;
        boolean enableBboxBuilder;
        boolean enableLinestringBuilder;
        boolean enableKeepPartialLinestring;
        boolean keepInvalidWays;
        NodeLocationStoreType storeType;

        // Get the task arguments.
        filePrefixString = getStringArgument(taskConfig, ARG_FILE_NAME, DEFAULT_FILE_PREFIX);
        enableBboxBuilder = getBooleanArgument(taskConfig, ARG_ENABLE_BBOX_BUILDER, DEFAULT_ENABLE_BBOX_BUILDER);
        enableLinestringBuilder =
                getBooleanArgument(taskConfig, ARG_ENABLE_LINESTRING_BUILDER, DEFAULT_ENABLE_LINESTRING_BUILDER);
        enableKeepPartialLinestring = getBooleanArgument(
                taskConfig, ARG_ENABLE_KEEP_PARTIAL_LINESTRING, DEFAULT_ENABLE_KEEP_PARTIAL_LINESTRING);
        keepInvalidWays = getBooleanArgument(taskConfig, ARG_KEEP_INVALID_WAYS, DEFAULT_KEEP_INVALID_WAYS);
        storeType = Enum.valueOf(
                NodeLocationStoreType.class,
                getStringArgument(taskConfig, ARG_NODE_LOCATION_STORE_TYPE, DEFAULT_NODE_LOCATION_STORE_TYPE));

        // Create a file object representing the directory from the file name provided.
        filePrefix = new File(filePrefixString);

        return new SinkManager(
                taskConfig.getId(),
                new PostgreSqlDumpWriter(
                        filePrefix,
                        enableBboxBuilder,
                        enableLinestringBuilder,
                        enableKeepPartialLinestring,
                        storeType,
                        keepInvalidWays),
                taskConfig.getPipeArgs());
    }
}

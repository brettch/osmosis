// This software is released into the Public Domain.  See copying.txt for details.
package org.openstreetmap.osmosis.replication.v0_6;

import org.openstreetmap.osmosis.core.pipeline.common.RunnableTaskManager;
import org.openstreetmap.osmosis.core.pipeline.common.TaskConfiguration;
import org.openstreetmap.osmosis.core.pipeline.common.TaskManager;

/**
 * The task manager factory for a replication file merger initializer.
 */
public class ReplicationFileMergerInitializerFactory extends WorkingTaskManagerFactory {

    /**
     * {@inheritDoc}
     */
    @Override
    protected TaskManager createTaskManagerImpl(TaskConfiguration taskConfig) {
        return new RunnableTaskManager(
                taskConfig.getId(),
                new ReplicationFileMergerInitializer(this.getWorkingDirectory(taskConfig)),
                taskConfig.getPipeArgs());
    }
}

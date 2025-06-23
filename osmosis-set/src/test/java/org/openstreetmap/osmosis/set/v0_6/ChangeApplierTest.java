// This software is released into the Public Domain.  See copying.txt for details.
package org.openstreetmap.osmosis.set.v0_6;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.File;
import org.junit.jupiter.api.Test;
import org.openstreetmap.osmosis.core.Osmosis;
import org.openstreetmap.osmosis.core.OsmosisRuntimeException;
import org.openstreetmap.osmosis.testutil.AbstractDataTest;

/**
 * Test the --apply-change task.
 *
 * @author Igor Podolskiy
 */
public class ChangeApplierTest extends AbstractDataTest {

    /**
     * Test the application of an empty change to a non-empty stream.
     */
    @Test
    public void emptyChange() {
        applyChange(
                "v0_6/apply_change/apply-change-base.osm",
                "v0_6/empty-change.osc",
                "v0_6/apply_change/apply-change-base.osm");
    }

    /**
     * Test the application of a non-empty change to an empty stream.
     */
    @Test
    public void emptyBase() {
        applyChange("v0_6/empty-entity.osm", "v0_6/apply_change/change-delete.osc", "v0_6/empty-entity.osm");
    }

    /**
     * Test the application of an empty change to an empty stream.
     */
    @Test
    public void emptyBoth() {
        applyChange("v0_6/empty-entity.osm", "v0_6/empty-change.osc", "v0_6/empty-entity.osm");
    }

    /**
     * Test the creation of a node.
     */
    @Test
    public void createNode() {
        applyChange(
                "v0_6/apply_change/apply-change-base.osm",
                "v0_6/apply_change/change-create.osc",
                "v0_6/apply_change/apply-change-create.osm");
    }

    /**
     * Test the modification of a node.
     */
    @Test
    public void modifyNode() {
        applyChange(
                "v0_6/apply_change/apply-change-base.osm",
                "v0_6/apply_change/change-modify.osc",
                "v0_6/apply_change/apply-change-modify.osm");
    }

    /**
     * Test the deletion of a node.
     */
    @Test
    public void deleteNode() {
        applyChange(
                "v0_6/apply_change/apply-change-base.osm",
                "v0_6/apply_change/change-delete.osc",
                "v0_6/apply_change/apply-change-delete.osm");
    }

    /**
     * Test the creation, modification and deletion of the same entity in a single stream.
     *
     * @throws Exception
     *             if something goes wrong
     */
    @Test
    public void createModifyDelete() {
        assertThrows(OsmosisRuntimeException.class, () -> {
            applyChange(
                    "v0_6/apply_change/apply-change-base.osm",
                    "v0_6/apply_change/change-create-modify-delete.osc",
                    "v0_6/apply_change/apply-change-base.osm");
        });
    }

    /**
     * Test the deletion of an entity that does not exist in the source stream.
     *
     * Deletion of a non-existent entity doesn't change anything.
     */
    @Test
    public void deleteNonExistent() {
        applyChange(
                "v0_6/apply_change/apply-change-base.osm",
                "v0_6/apply_change/change-delete-nonexistent.osc",
                "v0_6/apply_change/apply-change-base.osm");
    }

    /**
     * Test the modification of an entity that does not exist in the source stream.
     *
     * Modification of a non-existent entity has the same effect as its creation.
     */
    @Test
    public void modifyNonExistent() {
        applyChange(
                "v0_6/apply_change/apply-change-base.osm",
                "v0_6/apply_change/change-modify-nonexistent.osc",
                "v0_6/apply_change/apply-change-modify-nonexistent.osm");
    }

    /**
     * Test the creation of an entity that already exists in the source stream.
     *
     * Creation of an existent entity has the same effect as a modification.
     */
    @Test
    public void createExistent() {
        applyChange(
                "v0_6/apply_change/apply-change-base.osm",
                "v0_6/apply_change/change-create-existent.osc",
                "v0_6/apply_change/apply-change-base.osm");
    }

    /**
     * Test the case when the version in the change stream is lower than in the
     * source stream.
     */
    @Test
    public void modifyHigherVersion() {
        applyChange(
                "v0_6/apply_change/apply-change-base-high.osm",
                "v0_6/apply_change/change-modify.osc",
                "v0_6/apply_change/apply-change-modify-higher.osm");
    }

    /**
     * Test the case when the change is longer than the source stream
     * and consists of creates.
     */
    @Test
    public void longChangeCreate() {
        applyChange(
                "v0_6/apply_change/apply-change-base-node-only.osm",
                "v0_6/apply_change/change-big-create.osc",
                "v0_6/apply_change/apply-change-big.osm");
    }

    /**
     * Test the case when the change is longer than the source
     * stream and consists of deletes.
     */
    @Test
    public void longChangeDelete() {
        applyChange(
                "v0_6/apply_change/apply-change-base-node-only.osm",
                "v0_6/apply_change/change-big-delete.osc",
                "v0_6/apply_change/apply-change-base-node-only.osm");
    }

    private void applyChange(String sourceFileName, String changeFileName, String expectedOutputFileName) {
        File sourceFile;
        File changeFile;
        File expectedOutputFile;
        File actualOutputFile;

        sourceFile = dataUtils.createDataFile(sourceFileName);
        changeFile = dataUtils.createDataFile(changeFileName);
        expectedOutputFile = dataUtils.createDataFile(expectedOutputFileName);
        actualOutputFile = dataUtils.newFile();

        Osmosis.run(new String[] {
            "-q",
            "--read-xml-change-0.6",
            changeFile.getPath(),
            "--read-xml-0.6",
            sourceFile.getPath(),
            "--apply-change-0.6",
            "--write-xml-0.6",
            actualOutputFile.getPath()
        });

        dataUtils.compareFiles(expectedOutputFile, actualOutputFile);
    }
}

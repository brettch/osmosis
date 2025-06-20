// This software is released into the Public Domain.  See copying.txt for details.
package org.openstreetmap.osmosis.replication.common;

import java.io.File;
import java.util.StringTokenizer;
import org.openstreetmap.osmosis.core.OsmosisRuntimeException;

/**
 * Formats replication sequence numbers into file names.
 */
public class ReplicationFileSequenceFormatter {

    private ReplicationSequenceFormatter sequenceFormatter;
    private File workingDirectory;

    /**
     * Creates a new instance. The minimum length and grouping length will default to 9 and 3
     * respectively.
     *
     * @param workingDirectory
     *            The directory from which to base all created files.
     */
    public ReplicationFileSequenceFormatter(File workingDirectory) {
        this(workingDirectory, 9, 3);
    }

    /**
     * Creates a new instance.
     *
     * @param minimumLength
     *            The minimum length file sequence string to generate. For example, setting a length
     *            of 2 will generate sequence numbers from "00" to "99".
     * @param groupingLength
     *            The number of characters to write before separating with a '/' character. Used for
     *            creating sequence numbers to be written to files in a nested directory structure.
     * @param workingDirectory
     *            The directory from which to base all created files.
     */
    public ReplicationFileSequenceFormatter(File workingDirectory, int minimumLength, int groupingLength) {
        this.workingDirectory = workingDirectory;

        sequenceFormatter = new ReplicationSequenceFormatter(minimumLength, groupingLength);
    }

    /**
     * Formats the sequence number into a file name. Any sub-directories required will be
     * automatically created.
     *
     * @param sequenceNumber
     *            The sequence number.
     * @param fileNameSuffix
     *            The suffix to append to the end of the file name.
     * @return The formatted file.
     */
    public File getFormattedName(long sequenceNumber, String fileNameSuffix) {
        String fileName;
        StringTokenizer pathTokenizer;
        File formattedPath;

        fileName = sequenceFormatter.getFormattedName(sequenceNumber, fileNameSuffix);

        pathTokenizer = new StringTokenizer(fileName, "/");

        formattedPath = workingDirectory;
        while (pathTokenizer.hasMoreTokens()) {
            // Move to the next item in the path.
            formattedPath = new File(formattedPath, pathTokenizer.nextToken());

            // If this is a directory within the path (ie. not the final element in the path) then
            // ensure it exists and create it if it doesn't exist.
            if (pathTokenizer.hasMoreTokens()) {
                if (!formattedPath.exists() && !formattedPath.mkdir()) {
                    throw new OsmosisRuntimeException("Unable to create directory \"" + formattedPath + "\".");
                }
            }
        }

        return formattedPath;
    }
}

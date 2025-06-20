// This software is released into the Public Domain.  See copying.txt for details.
package org.openstreetmap.osmosis.pbf2.v0_6.impl;

/**
 * Represents a single piece of raw blob data extracted from the PBF stream. It has not yet been decoded into a
 * {@link crosby.binary.Fileformat.Blob} object. We delay this additional parsing until later when
 * we can spread the work across multiple threads.
 *
 * @author Brett Henderson
 */
public class RawBlob {
    private String type;
    private byte[] data;

    /**
     * Creates a new instance.
     *
     * @param type
     *            The type of data represented by this blob. This corresponds to
     *            the type field in the blob header.
     * @param data
     *            The raw contents of the blob in binary undecoded form.
     */
    public RawBlob(String type, byte[] data) {
        this.type = type;
        this.data = data;
    }

    /**
     * Gets the type of data represented by this blob. This corresponds to the
     * type field in the blob header.
     *
     * @return The blob type.
     */
    public String getType() {
        return type;
    }

    /**
     * Gets the raw contents of the blob in binary undecoded form.
     *
     * @return The raw blob data.
     */
    public byte[] getData() {
        return data;
    }
}

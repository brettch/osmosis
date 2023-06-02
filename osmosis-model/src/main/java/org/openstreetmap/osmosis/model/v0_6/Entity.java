// This software is released into the Public Domain.  See copying.txt for details.
package org.openstreetmap.osmosis.model.v0_6;

import java.time.Instant;

/**
 * All entity records have a common set of fields that are defined by this interface.
 */
interface Entity {
    long id();
    int version();
    Instant timestamp();
    OsmUser user();
    long changesetId();
}

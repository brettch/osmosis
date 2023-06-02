// This software is released into the Public Domain.  See copying.txt for details.
package org.openstreetmap.osmosis.model.v0_6;

import java.time.Instant;

/**
 * Represents an OSM Node.
 * 
 * @param id The unique identifier.
 * @param version The version of the entity.
 * 
 */
public record Node(
    long id,
    int version,
    Instant timestamp,
    OsmUser user,
    long changesetId
) implements Entity {
}

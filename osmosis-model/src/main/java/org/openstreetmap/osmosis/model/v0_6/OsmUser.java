// This software is released into the Public Domain.  See copying.txt for details.
package org.openstreetmap.osmosis.model.v0_6;

/**
 * Represents an OSM user.
 * 
 * @param id       The unique identifier.
 * @param userName The name of the user.
 * 
 * @author Brett Henderson
 */
public record OsmUser(int id, String userName) {
}

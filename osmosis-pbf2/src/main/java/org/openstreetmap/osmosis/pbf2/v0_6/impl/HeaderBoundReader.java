// This software is released into the Public Domain.  See copying.txt for details.
package org.openstreetmap.osmosis.pbf2.v0_6.impl;

import crosby.binary.Osmformat;
import java.util.function.Function;
import org.openstreetmap.osmosis.core.container.v0_6.BoundContainer;
import org.openstreetmap.osmosis.core.domain.v0_6.Bound;

/**
 * Obtains {@link Bound} data from a PBF {@link Osmformat.HeaderBlock}.
 */
public class HeaderBoundReader implements Function<Osmformat.HeaderBlock, BoundContainer> {
    private static final double COORDINATE_SCALING_FACTOR = 0.000000001;

    @Override
    public BoundContainer apply(Osmformat.HeaderBlock header) {
        Bound bound;
        if (header.hasBbox()) {
            Osmformat.HeaderBBox bbox = header.getBbox();
            bound = new Bound(
                    bbox.getRight() * COORDINATE_SCALING_FACTOR,
                    bbox.getLeft() * COORDINATE_SCALING_FACTOR,
                    bbox.getTop() * COORDINATE_SCALING_FACTOR,
                    bbox.getBottom() * COORDINATE_SCALING_FACTOR,
                    header.getSource());
        } else {
            bound = new Bound(header.getSource());
        }

        return new BoundContainer(bound);
    }
}

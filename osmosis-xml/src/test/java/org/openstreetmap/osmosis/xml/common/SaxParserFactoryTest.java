// This software is released into the Public Domain.  See copying.txt for details.
package org.openstreetmap.osmosis.xml.common;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import javax.xml.parsers.SAXParser;
import org.junit.jupiter.api.Test;

/**
 * Test the SaxParserFactory.
 *
 * @author Brett Henderson
 */
public class SaxParserFactoryTest {
    /**
     * Verify that a parser is successfully created.
     */
    @Test
    public void testCreateParser() {
        SAXParser parser = SaxParserFactory.createParser();
        assertNotNull(parser, "Parser was not created");
    }
}

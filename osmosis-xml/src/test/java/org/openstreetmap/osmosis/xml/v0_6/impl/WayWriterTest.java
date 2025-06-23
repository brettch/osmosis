// This software is released into the Public Domain.  See copying.txt for details.
package org.openstreetmap.osmosis.xml.v0_6.impl;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Calendar;
import java.util.Date;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openstreetmap.osmosis.core.domain.v0_6.CommonEntityData;
import org.openstreetmap.osmosis.core.domain.v0_6.OsmUser;
import org.openstreetmap.osmosis.core.domain.v0_6.Tag;
import org.openstreetmap.osmosis.core.domain.v0_6.Way;
import org.openstreetmap.osmosis.core.domain.v0_6.WayNode;

/**
 * Tests the XML way writer implementation.
 */
public class WayWriterTest {

    private StringWriter testWriter;
    private BufferedWriter testBufferedWriter;
    private WayWriter testWayWriter;
    private Date timestamp;
    // If any tests fail, it could be because the regexes have broken. There are a number of
    // variations which are valid XML which the regexes won't catch. They might need any number of
    // \\s* to account for variable whitespace, or the order of attributes may have shifted.
    private final String wayOpeningMatch = "^\\s*<way\\s*"
            + "id=['\"]1234['\"]\\s*"
            + "version=['\"]2['\"]\\s*"
            + "timestamp=['\"]2013-10-07T10:24:31Z?['\"]\\s*"
            + "uid=['\"]23['\"]\\s*"
            + "user=['\"]someuser['\"]\\s*"
            + ">\\s*";
    private final String wayNode1Match = "\\s*<nd\\s*ref=['\"]1235['\"]\\s*/>\\s*";
    private final String wayNode2Match = "\\s*<nd\\s*ref=['\"]1236['\"]\\s*/>\\s*";
    private final String wayTagMatch = "\\s*<tag\\s*k=['\"]waykey['\"]\\s*v=['\"]wayvalue['\"]\\s*/>\\s*";
    private final String wayClosingMatch = "\\s*</way>\\s*$";

    /**
     * Performs pre-test activities.
     */
    @BeforeEach
    public void setUp() {
        testWriter = new StringWriter();
        testBufferedWriter = new BufferedWriter(testWriter);
        testWayWriter = new WayWriter("way", 2);
        testWayWriter.setWriter(testBufferedWriter);
        Calendar calendar;
        calendar = Calendar.getInstance();
        calendar.set(Calendar.ZONE_OFFSET, 0);
        calendar.set(Calendar.DST_OFFSET, 0);
        calendar.set(Calendar.YEAR, 2013);
        calendar.set(Calendar.MONTH, Calendar.OCTOBER);
        calendar.set(Calendar.DAY_OF_MONTH, 7);
        calendar.set(Calendar.HOUR_OF_DAY, 10);
        calendar.set(Calendar.MINUTE, 24);
        calendar.set(Calendar.SECOND, 31);
        calendar.set(Calendar.MILLISECOND, 0);
        timestamp = calendar.getTime();
    }

    /**
     * Performs post-test activities.
     *
     * @throws IOException
     *             if stream cleanup fails.
     */
    @AfterEach
    public void tearDown() throws IOException {
        testBufferedWriter.close();
        testWriter.close();
    }

    /**
     * Test writing out a normal Way element.
     */
    @Test
    public final void testProcessNormalWay() {
        Way way = new Way(new CommonEntityData(1234, 2, timestamp, new OsmUser(23, "someuser"), 0));
        way.getWayNodes().add(new WayNode(1235));
        way.getWayNodes().add(new WayNode(1236));
        way.getTags().add(new Tag("waykey", "wayvalue"));

        testWayWriter.process(way);
        try {
            testBufferedWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
            fail("IOException");
        }
        String[] strArray = testWriter.toString().split("\\n", 5);
        assertTrue(strArray[0].matches(wayOpeningMatch), "Way opening element does not match.");
        assertTrue(strArray[1].matches(wayNode1Match), "Way node 1 does not match.");
        assertTrue(strArray[2].matches(wayNode2Match), "Way node 2 does not match.");
        assertTrue(strArray[3].matches(wayTagMatch), "Way tag does not match.");
        assertTrue(strArray[4].matches(wayClosingMatch), "Way closing element does not match.");
    }

    /**
     * Test writing of a Way element with no user.
     */
    @Test
    public final void testProcessWayWithNoUser() {
        Way way = new Way(new CommonEntityData(1234, 2, timestamp, OsmUser.NONE, 0));
        way.getWayNodes().add(new WayNode(1235));
        way.getWayNodes().add(new WayNode(1236));
        way.getTags().add(new Tag("waykey", "wayvalue"));

        testWayWriter.process(way);
        try {
            testBufferedWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
            fail("IOException");
        }
        String wayOpeningNoUserMatch = "^\\s*<way\\s*"
                + "id=['\"]1234['\"]\\s*"
                + "version=['\"]2['\"]\\s*"
                + "timestamp=['\"]2013-10-07T10:24:31Z?['\"]\\s*"
                + ">\\s*";
        String[] strArray = testWriter.toString().split("\\n", 5);
        assertTrue(strArray[0].matches(wayOpeningNoUserMatch), "Way opening element does not match.");
        assertTrue(strArray[1].matches(wayNode1Match), "Way node 1 does not match.");
        assertTrue(strArray[2].matches(wayNode2Match), "Way node 2 does not match.");
        assertTrue(strArray[3].matches(wayTagMatch), "Way tag does not match.");
        assertTrue(strArray[4].matches(wayClosingMatch), "Way closing element does not match.");
    }

    /**
     * Test writing out a Way element with no tags.
     */
    @Test
    public final void testProcessWayNoTags() {
        Way way = new Way(new CommonEntityData(1234, 2, timestamp, new OsmUser(23, "someuser"), 0));
        way.getWayNodes().add(new WayNode(1235));
        way.getWayNodes().add(new WayNode(1236));

        testWayWriter.process(way);
        try {
            testBufferedWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
            fail("IOException");
        }
        String[] strArray = testWriter.toString().split("\\n", 4);
        assertTrue(strArray[0].matches(wayOpeningMatch), "Way opening element does not match.");
        assertTrue(strArray[1].matches(wayNode1Match), "Way node 1 does not match.");
        assertTrue(strArray[2].matches(wayNode2Match), "Way node 2 does not match.");
        assertTrue(strArray[3].matches(wayClosingMatch), "Way closing element does not match.");
    }
}

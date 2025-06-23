// This software is released into the Public Domain.  See copying.txt for details.
package org.openstreetmap.osmosis.xml.common;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringWriter;
import org.junit.jupiter.api.Test;

/**
 * Tests the element writer.
 */
public class ElementWriterTest {

    /**
     * Tests the element writer.
     *
     * @throws IOException
     *             if an IO error occurs.
     */
    @Test
    public void testBasic() throws IOException {
        MyElementWriter elementWriter;
        StringWriter stringWriter;
        BufferedWriter bufferedWriter;

        stringWriter = new StringWriter();
        bufferedWriter = new BufferedWriter(stringWriter);

        elementWriter = new MyElementWriter();
        elementWriter.setWriter(bufferedWriter);

        elementWriter.buildContent();

        bufferedWriter.close();

        assertEquals(
                "  <testElement myAttribute=\"ValueBeginValueEnd\"/>" + System.getProperty("line.separator"),
                stringWriter.toString(),
                "Generated xml is incorrect.");
    }

    private static class MyElementWriter extends ElementWriter {
        MyElementWriter() {
            super("testElement", 1);
        }

        public void buildContent() {
            beginOpenElement();
            addAttribute("myAttribute", "ValueBegin" + (char) 0x02 + "ValueEnd");
            endOpenElement(true);
        }
    }
}

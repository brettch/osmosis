// This software is released into the Public Domain.  See copying.txt for details.
package org.openstreetmap.osmosis.core.cli;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.logging.Level;
import org.junit.jupiter.api.Test;
import org.openstreetmap.osmosis.core.LogLevels;
import org.openstreetmap.osmosis.core.OsmosisRuntimeException;

/**
 * Tests the CommandLineParser class.
 *
 * @author Brett Henderson
 */
public class CommandLineParserTest {
    /**
     * Validates the quiet option.
     */
    @Test
    public void testQuietOption() {
        CommandLineParser commandLineParser;

        commandLineParser = new CommandLineParser();
        commandLineParser.parse(new String[] {});
        assertEquals(
                Level.INFO,
                LogLevels.getLogLevel(commandLineParser.getLogLevelIndex()),
                "Incorrect default log level.");

        commandLineParser = new CommandLineParser();
        commandLineParser.parse(new String[] {"-q"});
        assertEquals(
                Level.WARNING,
                LogLevels.getLogLevel(commandLineParser.getLogLevelIndex()),
                "Incorrect quiet log level.");

        commandLineParser = new CommandLineParser();
        commandLineParser.parse(new String[] {"-q", "1"});
        assertEquals(
                Level.SEVERE,
                LogLevels.getLogLevel(commandLineParser.getLogLevelIndex()),
                "Incorrect very quiet log level.");

        commandLineParser = new CommandLineParser();
        commandLineParser.parse(new String[] {"-q", "2"});
        assertEquals(
                Level.OFF,
                LogLevels.getLogLevel(commandLineParser.getLogLevelIndex()),
                "Incorrect very very quiet log level.");
    }

    /**
     * Validates the verbose option.
     */
    @Test
    public void testVerboseOption() {
        CommandLineParser commandLineParser;

        commandLineParser = new CommandLineParser();
        commandLineParser.parse(new String[] {});
        assertEquals(
                Level.INFO,
                LogLevels.getLogLevel(commandLineParser.getLogLevelIndex()),
                "Incorrect default log level.");

        commandLineParser = new CommandLineParser();
        commandLineParser.parse(new String[] {"-v"});
        assertEquals(
                Level.FINE,
                LogLevels.getLogLevel(commandLineParser.getLogLevelIndex()),
                "Incorrect verbose log level.");

        commandLineParser = new CommandLineParser();
        commandLineParser.parse(new String[] {"-v", "1"});
        assertEquals(
                Level.FINER,
                LogLevels.getLogLevel(commandLineParser.getLogLevelIndex()),
                "Incorrect very verbose log level.");

        commandLineParser = new CommandLineParser();
        commandLineParser.parse(new String[] {"-v", "2"});
        assertEquals(
                Level.FINEST,
                LogLevels.getLogLevel(commandLineParser.getLogLevelIndex()),
                "Incorrect very very verbose log level.");

        commandLineParser = new CommandLineParser();
        commandLineParser.parse(new String[] {"-v", "3"});
        assertEquals(
                Level.FINEST,
                LogLevels.getLogLevel(commandLineParser.getLogLevelIndex()),
                "Incorrect very very very verbose log level.");
    }

    /**
     * Validates the quiet and verbose options in combination.
     */
    @Test
    public void testQuietAndVerboseOption() {
        CommandLineParser commandLineParser;

        commandLineParser = new CommandLineParser();
        commandLineParser.parse(new String[] {});
        assertEquals(
                Level.INFO,
                LogLevels.getLogLevel(commandLineParser.getLogLevelIndex()),
                "Incorrect default log level.");

        commandLineParser = new CommandLineParser();
        commandLineParser.parse(new String[] {"-v", "-q"});
        assertEquals(
                Level.INFO,
                LogLevels.getLogLevel(commandLineParser.getLogLevelIndex()),
                "Incorrect default log level.");

        commandLineParser = new CommandLineParser();
        commandLineParser.parse(new String[] {"-v", "1", "-q", "1"});
        assertEquals(
                Level.INFO,
                LogLevels.getLogLevel(commandLineParser.getLogLevelIndex()),
                "Incorrect default log level.");

        commandLineParser = new CommandLineParser();
        commandLineParser.parse(new String[] {"-v", "1", "-q", "2"});
        assertEquals(
                Level.WARNING,
                LogLevels.getLogLevel(commandLineParser.getLogLevelIndex()),
                "Incorrect quiet log level.");
    }

    /**
     * Validates the quiet and verbose options in combination.
     */
    @Test
    public void testPluginOption() {
        CommandLineParser commandLineParser;

        commandLineParser = new CommandLineParser();
        commandLineParser.parse(new String[] {"-p", "plugin1", "-p", "plugin2"});
        assertEquals(Arrays.asList("plugin1", "plugin2"), commandLineParser.getPlugins(), "Incorrect plugin list.");
    }

    /**
     * Validates failure when an unknown option is specified.
     */
    @Test
    void testUnknownOptionThrowsException() {
        CommandLineParser commandLineParser = new CommandLineParser();
        assertThrows(OsmosisRuntimeException.class, () -> {
            commandLineParser.parse(new String[] {"-a"});
        });
    }
}

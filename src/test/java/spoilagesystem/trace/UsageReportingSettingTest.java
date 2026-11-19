package spoilagesystem.trace;

import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * An operator who sets usage-reporting.enabled: false must find reporting off, every time,
 * whatever the plugin does with config.yml afterwards.
 */
class UsageReportingSettingTest {
    @TempDir
    File folder;

    private File configFile(String contents) throws Exception {
        File file = new File(folder, "config.yml");
        Files.write(file.toPath(), contents.getBytes(StandardCharsets.UTF_8));
        return file;
    }

    private static String read(File file) throws Exception {
        return new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);
    }

    /** What JavaPlugin.getConfig() loads: the file, with the jar's config.yml as defaults. */
    private YamlConfiguration loadLikeBukkit(File file) throws Exception {
        // Bukkit's loadConfiguration() logs a file it cannot parse and returns it empty; calling
        // it here would need a running server, so the empty result is simulated.
        YamlConfiguration config = new YamlConfiguration();
        try {
            config.load(file);
        } catch (Exception e) {
            // left empty, as Bukkit does
        }
        try (InputStream stream = getClass().getResourceAsStream("/config.yml")) {
            config.setDefaults(YamlConfiguration.loadConfiguration(new InputStreamReader(stream, StandardCharsets.UTF_8)));
        }
        return config;
    }

    /** What the plugin's saveConfig() does: copy defaults in, then save only through the guards. */
    private void guardedSave(YamlConfiguration config, File file) throws Exception {
        config.options().copyDefaults(true);
        if (UsageReportingSetting.isUnparseable(file)) {
            return;
        }
        UsageReportingSetting.keepOptOutFromDisk(config, file);
        config.save(file);
    }

    @Test
    void anUpgradeKeepsReportingOff() throws Exception {
        File file = configFile("usage-reporting:\n  enabled: false\n");

        guardedSave(loadLikeBukkit(file), file);

        assertEquals(Boolean.FALSE, UsageReportingSetting.readFromDisk(file));
        assertFalse(UsageReportingSetting.isEnabled(loadLikeBukkit(file), file));
    }

    @Test
    void aFileWithoutTheBlockReportsByDefault() throws Exception {
        File file = configFile("unrelated: 1\n");

        assertTrue(UsageReportingSetting.isEnabled(loadLikeBukkit(file), file));
    }

    @Test
    void aQuotedFalseTurnsReportingOff() throws Exception {
        File file = configFile("usage-reporting:\n  enabled: \"false\"\n");
        // Bukkit's getBoolean() returns the bundled default, true, for the string "false".
        assertTrue(loadLikeBukkit(file).getBoolean(UsageReportingSetting.ENABLED_KEY));

        assertFalse(UsageReportingSetting.isEnabled(loadLikeBukkit(file), file));
    }

    @Test
    void anUnrecognisedValueTurnsReportingOff() throws Exception {
        File file = configFile("usage-reporting:\n  enabled: disabled\n");

        assertFalse(UsageReportingSetting.isEnabled(loadLikeBukkit(file), file));
    }

    @Test
    void aStaleLoadedConfigCannotOverwriteAnOptOutMadeWhileRunning() throws Exception {
        File file = configFile("usage-reporting:\n  enabled: true\n");
        YamlConfiguration loaded = loadLikeBukkit(file);
        // The operator turns reporting off by hand while the server runs.
        Files.write(file.toPath(), "usage-reporting:\n  enabled: false\n".getBytes(StandardCharsets.UTF_8));

        // Something later saves the loaded, stale config.
        guardedSave(loaded, file);

        assertEquals(Boolean.FALSE, UsageReportingSetting.readFromDisk(file));
    }

    @Test
    void aFileBukkitCannotParseIsNeverOverwrittenAndReportingStaysOff() throws Exception {
        String broken = "usage-reporting:\n  enabled: false\n\tbroken: [\n";
        File file = configFile(broken);
        // Bukkit loads it as empty, so every getter falls through to the bundled defaults.
        assertTrue(loadLikeBukkit(file).getBoolean(UsageReportingSetting.ENABLED_KEY));

        assertTrue(UsageReportingSetting.isUnparseable(file));
        assertFalse(UsageReportingSetting.isEnabled(loadLikeBukkit(file), file));
        guardedSave(loadLikeBukkit(file), file);
        assertEquals(broken, read(file), "the operator's file must be left as it is");
    }

    @Test
    void aMissingFileIsNotAnOptOut() {
        File file = new File(folder, "absent.yml");

        assertNull(UsageReportingSetting.readFromDisk(file));
        assertFalse(UsageReportingSetting.turnedOffOnDisk(file));
        assertFalse(UsageReportingSetting.isUnparseable(file));
    }
}

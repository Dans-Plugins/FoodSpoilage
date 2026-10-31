package spoilagesystem.config;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import spoilagesystem.FoodSpoilage;
import spoilagesystem.config.migration.ConfigMigration;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Tests for {@link LocalConfigService}, covering how the configured {@code spoiled-food-material}
 * is resolved, how {@code spoil-time} and {@code spoil-chance} are read, how the
 * {@code usage-reporting} block is read, how the feature settings and {@code wax-material} fall
 * back when absent, how the command replies, item text and lore in the {@code text} section
 * are read, and which config migrations run on construction.
 */
public class LocalConfigServiceTest {

    private static final String KEY = "spoiled-food-material";

    @Mock
    private FoodSpoilage plugin;

    @Mock
    private FileConfiguration config;

    private LocalConfigService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        when(plugin.getConfig()).thenReturn(config);
        when(plugin.getLogger()).thenReturn(Logger.getLogger(LocalConfigServiceTest.class.getName()));
        configure(LocalConfigService.DEFAULT_SPOILED_FOOD_MATERIAL.name());

        service = new LocalConfigService(plugin);
    }

    @Test
    void theShippedDefaultResolvesToRottenFlesh() {
        assertEquals(Material.ROTTEN_FLESH, service.getSpoiledFoodMaterial());
    }

    @Test
    void aConfiguredMaterialReplacesTheDefault() {
        configure("POISONOUS_POTATO");

        assertEquals(Material.POISONOUS_POTATO, service.getSpoiledFoodMaterial());
    }

    /**
     * A server that upgrades from an earlier version keeps a config.yml with no such key, so the
     * absent-key path has to behave exactly as the shipped default does.
     */
    @Test
    void anAbsentKeyFallsBackToTheDefault() {
        when(config.getString(eq(KEY), anyString()))
                .thenAnswer(invocation -> invocation.getArgument(1));

        assertEquals(Material.ROTTEN_FLESH, service.getSpoiledFoodMaterial());
    }

    @Test
    void aNameThatMatchesNoMaterialFallsBackToTheDefault() {
        configure("NOT_A_MATERIAL");

        assertEquals(Material.ROTTEN_FLESH, service.getSpoiledFoodMaterial());
    }

    /**
     * A block-only material resolves through {@code matchMaterial} but cannot be put in an
     * ItemStack, so it has to be rejected before it reaches the spoilage path.
     */
    @Test
    void aMaterialThatIsNotAnItemFallsBackToTheDefault() {
        configure("WATER");

        assertEquals(Material.ROTTEN_FLESH, service.getSpoiledFoodMaterial());
    }

    /**
     * The resolved material is cached against the name it came from, so the cache must not outlive
     * a value replaced by {@code /fs reload}.
     */
    @Test
    void aReconfiguredMaterialIsPickedUpWithoutARestart() {
        assertEquals(Material.ROTTEN_FLESH, service.getSpoiledFoodMaterial());

        configure("POISONOUS_POTATO");

        assertEquals(Material.POISONOUS_POTATO, service.getSpoiledFoodMaterial());
    }

    @Test
    void aMaterialWithItsOwnSpoilTimeUsesIt() {
        useSpoilTimes("PT24H", "PT72H");

        assertEquals(Duration.ofHours(72), service.getTime(Material.BREAD));
    }

    @Test
    void aMaterialWithoutItsOwnSpoilTimeUsesTheDefault() {
        useSpoilTimes("PT36H", "PT72H");

        assertEquals(Duration.ofHours(36), service.getTime(Material.COOKED_BEEF));
    }

    /**
     * "0" makes a single food never spoil while the default still applies. An unparseable value
     * also comes back as zero, so the absence of the warning is what shows "0" is understood
     * rather than rejected.
     */
    @Test
    void aSpoilTimeOfZeroMeansTheMaterialNeverSpoils() {
        Logger logger = mock(Logger.class);
        when(plugin.getLogger()).thenReturn(logger);
        useSpoilTimes("PT24H", "0");

        assertEquals(Duration.ZERO, service.getTime(Material.BREAD));
        verify(logger, never()).warning(anyString());
    }

    @Test
    void aZeroDefaultSurroundedByWhitespaceStillMeansNeverSpoil() {
        Logger logger = mock(Logger.class);
        when(plugin.getLogger()).thenReturn(logger);
        useSpoilTimes(" 0 ", null);

        assertEquals(Duration.ZERO, service.getTime(Material.COOKED_BEEF));
        verify(logger, never()).warning(anyString());
    }

    @Test
    void noSpoilTimeAnywhereMeansTheMaterialNeverSpoils() {
        useSpoilTimes(null, null);

        assertEquals(Duration.ZERO, service.getTime(Material.BREAD));
    }

    /** An unparseable value must not throw on the stamping path, and the operator has to be told. */
    @Test
    void anUnparseableSpoilTimeMeansNeverSpoilAndLogsAWarning() {
        Logger logger = mock(Logger.class);
        when(plugin.getLogger()).thenReturn(logger);
        useSpoilTimes("PT24H", "24 hours");

        assertEquals(Duration.ZERO, service.getTime(Material.BREAD));
        verify(logger).warning(contains("'24 hours'"));
    }

    @Test
    void aMaterialWithNoSpoilChanceNeverSpoilsOnCraft() {
        useSpoilChance(null);

        assertEquals(0, service.determineSpoiledAmount(Material.BREAD, 64));
    }

    @Test
    void aNegativeSpoilChanceIsTreatedAsNone() {
        useSpoilChance(-1.0);

        assertEquals(0, service.determineSpoiledAmount(Material.BREAD, 64));
    }

    /** {@code nextDouble()} is always below 1.0, so a chance of 1.0 spoils every item. */
    @Test
    void aSpoilChanceOfOneSpoilsTheWholeQuantity() {
        useSpoilChance(1.0);

        assertEquals(64, service.determineSpoiledAmount(Material.BREAD, 64));
    }

    @Test
    void theItemStackOverloadUsesTheStacksTypeAndAmount() {
        useSpoilChance(1.0);
        ItemStack stack = mock(ItemStack.class);
        when(stack.getType()).thenReturn(Material.BREAD);
        when(stack.getAmount()).thenReturn(5);

        assertEquals(5, service.determineSpoiledAmount(stack));
    }

    /**
     * A server upgraded from before usage reporting has no usage-reporting block in its
     * config.yml. Bukkit's one-argument getters fall through to the jar's defaults; the
     * two-argument ones would return their fallback and turn reporting off on every existing
     * installation.
     */
    @Test
    void usageReportingReadsThroughToTheBundledDefaultsWhenTheFileHasNoBlock() {
        when(config.getBoolean("usage-reporting.enabled")).thenReturn(true);
        when(config.getString("usage-reporting.endpoint")).thenReturn("https://trace.danielstephenson.dev");
        when(config.getString("usage-reporting.key")).thenReturn("bundled-key");

        assertTrue(service.isUsageReportingEnabled());
        assertEquals("https://trace.danielstephenson.dev", service.getUsageReportingEndpoint());
        assertEquals("bundled-key", service.getUsageReportingKey());
        verify(config, never()).getString(eq("usage-reporting.key"), anyString());
        verify(config, never()).getString(eq("usage-reporting.endpoint"), anyString());
        verify(config, never()).getBoolean(eq("usage-reporting.enabled"), anyBoolean());
    }

    @Test
    void usageReportingIsOffWithNoKeyAnywhere() {
        when(config.getString("usage-reporting.key")).thenReturn(null);
        when(config.getString("usage-reporting.endpoint")).thenReturn(null);

        assertEquals("", service.getUsageReportingKey(), "no key anywhere must read as off, not as null");
        assertEquals(LocalConfigService.DEFAULT_USAGE_REPORTING_ENDPOINT, service.getUsageReportingEndpoint());
    }

    @Test
    void usageReportingReadsTheConfiguredValues() {
        when(config.getBoolean("usage-reporting.enabled")).thenReturn(false);
        when(config.getString("usage-reporting.endpoint")).thenReturn("http://localhost:8080");
        when(config.getString("usage-reporting.key")).thenReturn("abc");

        assertFalse(service.isUsageReportingEnabled());
        assertEquals("http://localhost:8080", service.getUsageReportingEndpoint());
        assertEquals("abc", service.getUsageReportingKey());
    }

    /**
     * A server upgraded from before usage reporting keeps a config.yml without the block, and
     * {@code saveDefaultConfig()} never rewrites an existing file. The block has to be written so
     * the opt-out is visible, with the bundled values rather than new literals. A real
     * {@link YamlConfiguration} is used here because the point is that {@code isSet} answers for
     * the file alone while the defaults are registered on it.
     */
    @Test
    void theUsageReportingBlockIsWrittenToDiskWhenTheFileLacksIt() {
        YamlConfiguration onDisk = new YamlConfiguration();
        onDisk.set("version", "3.0.0");
        YamlConfiguration bundled = YamlConfiguration.loadConfiguration(new InputStreamReader(
                getClass().getResourceAsStream("/config.yml"), StandardCharsets.UTF_8));
        onDisk.setDefaults(bundled);
        when(plugin.getConfig()).thenReturn(onDisk);
        assertFalse(onDisk.isSet("usage-reporting"), "the file itself must start without the block");

        service.ensureUsageReportingBlockOnDisk();

        assertEquals(bundled.get("usage-reporting.enabled"), onDisk.get("usage-reporting.enabled"));
        assertEquals(bundled.get("usage-reporting.endpoint"), onDisk.get("usage-reporting.endpoint"));
        assertEquals(bundled.get("usage-reporting.key"), onDisk.get("usage-reporting.key"));
        assertTrue(onDisk.isSet("usage-reporting.key"));
        verify(plugin).saveConfig();
    }

    /** A file that already carries the block -- including one where enabled is false -- is never rewritten. */
    @Test
    void aFileThatAlreadyHasTheUsageReportingBlockIsLeftAlone() {
        when(config.isSet("usage-reporting")).thenReturn(true);

        service.ensureUsageReportingBlockOnDisk();

        verify(config, never()).set(anyString(), any());
        verify(plugin, never()).saveConfig();
    }

    /**
     * A server upgraded from before these keys existed keeps a config.yml without them. They must
     * read through to the bundled text rather than come back null, which the colour-code
     * translation would reject.
     */
    @Test
    void commandRepliesReadThroughToTheBundledTextWhenTheFileLacksThem() {
        YamlConfiguration onDisk = new YamlConfiguration();
        onDisk.set("version", "3.0.0");
        onDisk.setDefaults(bundledConfig());
        when(plugin.getConfig()).thenReturn(onDisk);

        assertEquals("In order to use this command, you need the following permission: 'fs.default'", service.getNoPermsDefaultText());
        assertEquals("In order to use this command, you need the following permission: 'fs.help'", service.getNoPermsHelpText());
        assertEquals("In order to use this command, you need the following permission: 'fs.timeleft'", service.getNoPermsTimeLeftText());
        assertEquals("That command wasn't found.", service.getCommandNotFoundText());
    }

    @Test
    void configuredCommandRepliesReplaceTheBundledText() {
        YamlConfiguration onDisk = new YamlConfiguration();
        onDisk.set("text.no-permission-default", "&cfs.default fehlt");
        onDisk.set("text.no-permission-help", "&cfs.help fehlt");
        onDisk.set("text.no-permission-timeleft", "&cfs.timeleft fehlt");
        onDisk.set("text.command-not-found", "&cUnbekannter Befehl.");
        onDisk.setDefaults(bundledConfig());
        when(plugin.getConfig()).thenReturn(onDisk);

        assertEquals("§cfs.default fehlt", service.getNoPermsDefaultText());
        assertEquals("§cfs.help fehlt", service.getNoPermsHelpText());
        assertEquals("§cfs.timeleft fehlt", service.getNoPermsTimeLeftText());
        assertEquals("§cUnbekannter Befehl.", service.getCommandNotFoundText());
    }

    /**
     * These getters use the two-argument form, so a key missing from the file on disk returns the
     * literal fallback in Java rather than reading through to the bundled defaults. An upgraded
     * server therefore behaves as the shipped config.yml describes only while each literal agrees
     * with the shipped value, which is what this pins.
     */
    @Test
    void theFallbacksForAbsentSettingsMatchTheShippedConfig() {
        YamlConfiguration bundled = bundledConfig();
        YamlConfiguration onDisk = new YamlConfiguration();
        onDisk.setDefaults(bundled);
        when(plugin.getConfig()).thenReturn(onDisk);

        assertEquals(bundled.getBoolean("enable-waxing"), service.isWaxingEnabled());
        assertEquals(bundled.getString("wax-material"), service.getWaxMaterialName());
        assertEquals(bundled.getString("expiry-date-format"), service.getExpiryDateFormat());
        assertEquals(bundled.getString("expiry-date-format"), LocalConfigService.DEFAULT_EXPIRY_DATE_FORMAT);
        assertEquals(bundled.getString("spoiled-food-material"), service.getSpoiledFoodMaterialName());
        assertEquals(bundled.getBoolean("timestamp-furnace-output"), service.isTimestampFurnaceOutput());
        assertEquals(bundled.getBoolean("debug"), service.isDebugEnabled());
    }

    @Test
    void configuredSettingsReplaceTheFallbacks() {
        YamlConfiguration onDisk = new YamlConfiguration();
        onDisk.set("enable-waxing", false);
        onDisk.set("wax-material", "SLIME_BALL");
        onDisk.set("expiry-date-format", "dd.MM.yyyy");
        onDisk.set("timestamp-furnace-output", true);
        onDisk.set("debug", true);
        when(plugin.getConfig()).thenReturn(onDisk);

        assertFalse(service.isWaxingEnabled());
        assertEquals(Material.SLIME_BALL, service.getWaxMaterial());
        assertEquals("dd.MM.yyyy", service.getExpiryDateFormat());
        assertTrue(service.isTimestampFurnaceOutput());
        assertTrue(service.isDebugEnabled());
    }

    /** Both callers treat a null wax material as "waxing is unavailable", so no fallback is substituted. */
    @Test
    void aWaxMaterialThatMatchesNothingResolvesToNull() {
        YamlConfiguration onDisk = new YamlConfiguration();
        onDisk.set("wax-material", "NOT_A_MATERIAL");
        when(plugin.getConfig()).thenReturn(onDisk);

        assertNull(service.getWaxMaterial());
    }

    /** Each lore line is colour-translated on its own, and placeholders are left for the caller to fill. */
    @Test
    void loreLinesAreReadInOrderAndColourTranslated() {
        YamlConfiguration onDisk = new YamlConfiguration();
        onDisk.setDefaults(bundledConfig());
        when(plugin.getConfig()).thenReturn(onDisk);

        assertEquals(List.of("", "§fExpiry Date:", "§f${expiry_date}"), service.getExpiryDateText());
        assertEquals(List.of("", "§fWaxed", "§7This item will not spoil but cannot be eaten."), service.getWaxedFoodLore());
    }

    @Test
    void absentLoreReadsAsNoLinesRatherThanNull() {
        when(plugin.getConfig()).thenReturn(new YamlConfiguration());

        assertEquals(List.of(), service.getExpiryDateText());
        assertEquals(List.of(), service.getWaxedFoodLore());
    }

    /**
     * The item and reload text, like the command replies above, has to read through to the bundled
     * text on a server whose config.yml predates a key.
     */
    @Test
    void itemAndReloadTextReadsThroughToTheBundledTextWhenTheFileLacksIt() {
        YamlConfiguration onDisk = new YamlConfiguration();
        onDisk.set("version", "3.0.0");
        onDisk.setDefaults(bundledConfig());
        when(plugin.getConfig()).thenReturn(onDisk);

        assertEquals("Values loaded!", service.getValuesLoadedText());
        assertEquals("Sorry! In order to use this command, you need the following permission: 'fs.reload'", service.getNoPermsReloadText());
        assertEquals("Spoiled Food", service.getSpoiledFoodName());
        assertEquals("This food has gone bad.", service.getSpoiledFoodLore());
        assertEquals("This item will never spoil.", service.getNeverSpoilText());
        assertEquals("This item will expire in ${time}.", service.getTimeLeftText());
        assertEquals("This item will expire in less than an hour.", service.getLessThanAnHour());
        assertEquals("This item will expire in less than a day.", service.getLessThanADay());
        assertEquals("This item has expired.", service.getNoTimeLeftText());
    }

    @Test
    void configuredItemAndReloadTextIsColourTranslated() {
        YamlConfiguration onDisk = new YamlConfiguration();
        onDisk.set("text.values-loaded", "&aNeu geladen!");
        onDisk.set("text.no-permission-reload", "&cfs.reload fehlt");
        onDisk.set("text.spoiled-food-name", "&2Verdorben");
        onDisk.set("text.spoiled-food-lore", "&7Schlecht geworden.");
        onDisk.set("text.never-spoil", "&bHaltbar.");
        onDisk.set("text.time-left", "&eNoch ${time}.");
        onDisk.set("text.less-than-an-hour", "&eUnter einer Stunde.");
        onDisk.set("text.less-than-a-day", "&eUnter einem Tag.");
        onDisk.set("text.no-time-left", "&4Abgelaufen.");
        onDisk.setDefaults(bundledConfig());
        when(plugin.getConfig()).thenReturn(onDisk);

        assertEquals("§aNeu geladen!", service.getValuesLoadedText());
        assertEquals("§cfs.reload fehlt", service.getNoPermsReloadText());
        assertEquals("§2Verdorben", service.getSpoiledFoodName());
        assertEquals("§7Schlecht geworden.", service.getSpoiledFoodLore());
        assertEquals("§bHaltbar.", service.getNeverSpoilText());
        assertEquals("§eNoch ${time}.", service.getTimeLeftText());
        assertEquals("§eUnter einer Stunde.", service.getLessThanAnHour());
        assertEquals("§eUnter einem Tag.", service.getLessThanADay());
        assertEquals("§4Abgelaufen.", service.getNoTimeLeftText());
    }

    @Test
    void aMigrationFromTheConfiguredVersionRunsAndRecordsItsNewVersion() {
        YamlConfiguration onDisk = configAtVersion("1");
        RecordingMigration migration = new RecordingMigration("1", "2");

        new LocalConfigService(plugin, List.of(migration));

        assertEquals(1, migration.runs);
        assertEquals("2", onDisk.getString("version"));
        verify(plugin).saveConfig();
    }

    @Test
    void aMigrationFromAnotherVersionIsSkipped() {
        YamlConfiguration onDisk = configAtVersion("3");
        RecordingMigration migration = new RecordingMigration("1", "2");

        new LocalConfigService(plugin, List.of(migration));

        assertEquals(0, migration.runs);
        assertEquals("3", onDisk.getString("version"));
        verify(plugin, never()).saveConfig();
    }

    @Test
    void consecutiveMigrationsChainWithinOnePass() {
        YamlConfiguration onDisk = configAtVersion("1");
        RecordingMigration first = new RecordingMigration("1", "2");
        RecordingMigration second = new RecordingMigration("2", "3");

        new LocalConfigService(plugin, List.of(first, second));

        assertEquals(1, first.runs);
        assertEquals(1, second.runs);
        assertEquals("3", onDisk.getString("version"));
    }

    @Test
    void migrationsListedOutOfOrderDoNotChainBackwards() {
        YamlConfiguration onDisk = configAtVersion("1");
        RecordingMigration later = new RecordingMigration("2", "3");
        RecordingMigration earlier = new RecordingMigration("1", "2");

        new LocalConfigService(plugin, List.of(later, earlier));

        assertEquals(0, later.runs);
        assertEquals(1, earlier.runs);
        assertEquals("2", onDisk.getString("version"));
    }

    @Test
    void migrationsRunBeforeTheBundledDefaultsAreSaved() {
        configAtVersion("1");
        RecordingMigration migration = new RecordingMigration("1", "2");
        doAnswer(invocation -> {
            assertEquals(1, migration.runs, "saveDefaultConfig ran before the migration");
            return null;
        }).when(plugin).saveDefaultConfig();

        new LocalConfigService(plugin, List.of(migration));

        verify(plugin).saveDefaultConfig();
    }

    @Test
    void theShippedServiceRunsNoMigrations() {
        YamlConfiguration onDisk = configAtVersion("1");

        new LocalConfigService(plugin);

        assertEquals("1", onDisk.getString("version"));
        verify(plugin, never()).saveConfig();
    }

    /**
     * A config holding only {@code version}, with no bundled defaults, so that the usage-reporting
     * write on construction is skipped and any {@code saveConfig} call comes from a migration.
     */
    private YamlConfiguration configAtVersion(String version) {
        YamlConfiguration onDisk = new YamlConfiguration();
        onDisk.set("version", version);
        when(plugin.getConfig()).thenReturn(onDisk);
        clearInvocations(plugin); // forget the calls made by the service built in setUp
        return onDisk;
    }

    private static final class RecordingMigration implements ConfigMigration {
        private final String previousVersion;
        private final String newVersion;
        private int runs;

        RecordingMigration(String previousVersion, String newVersion) {
            this.previousVersion = previousVersion;
            this.newVersion = newVersion;
        }

        @Override
        public String getPreviousVersion() {
            return previousVersion;
        }

        @Override
        public String getNewVersion() {
            return newVersion;
        }

        @Override
        public void run() {
            runs++;
        }
    }

    private YamlConfiguration bundledConfig() {
        return YamlConfiguration.loadConfiguration(new InputStreamReader(
                getClass().getResourceAsStream("/config.yml"), StandardCharsets.UTF_8));
    }

    /** A null leaves the key out of the file, so it is absent rather than empty. */
    private void useSpoilTimes(String defaultTime, String breadTime) {
        YamlConfiguration onDisk = new YamlConfiguration();
        onDisk.set("spoil-time.default", defaultTime);
        onDisk.set("spoil-time.BREAD", breadTime);
        when(plugin.getConfig()).thenReturn(onDisk);
    }

    private void useSpoilChance(Double breadChance) {
        YamlConfiguration onDisk = new YamlConfiguration();
        onDisk.set("spoil-chance.BREAD", breadChance);
        when(plugin.getConfig()).thenReturn(onDisk);
    }

    private void configure(String materialName) {
        when(config.getString(eq(KEY), anyString())).thenReturn(materialName);
    }
}

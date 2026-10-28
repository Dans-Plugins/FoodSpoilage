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

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Tests for {@link LocalConfigService}, covering how the configured {@code spoiled-food-material}
 * is resolved, how {@code spoil-time} and {@code spoil-chance} are read, how the
 * {@code usage-reporting} block is read, and how the command replies in the {@code text} section
 * are read.
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

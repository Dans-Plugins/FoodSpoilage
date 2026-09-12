package spoilagesystem.config;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import spoilagesystem.FoodSpoilage;

import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Tests for {@link LocalConfigService}, covering how the configured {@code spoiled-food-material}
 * is resolved and how the {@code usage-reporting} block is read.
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

    private void configure(String materialName) {
        when(config.getString(eq(KEY), anyString())).thenReturn(materialName);
    }
}

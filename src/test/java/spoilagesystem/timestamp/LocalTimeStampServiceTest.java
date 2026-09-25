package spoilagesystem.timestamp;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import spoilagesystem.FoodSpoilage;
import spoilagesystem.config.LocalConfigService;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.logging.Logger;

import static org.bukkit.persistence.PersistentDataType.STRING;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Tests for {@link LocalTimeStampService}, covering how the configured {@code expiry-date-format}
 * is picked up.
 */
public class LocalTimeStampServiceTest {

    private static final Duration ONE_DAY = Duration.ofHours(24);

    @Mock
    private FoodSpoilage plugin;

    @Mock
    private LocalConfigService configService;

    @Mock
    private ItemStack item;

    @Mock
    private ItemMeta meta;

    @Mock
    private PersistentDataContainer persistentDataContainer;

    private LocalTimeStampService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        when(plugin.getName()).thenReturn("foodspoilage");
        when(plugin.getLogger()).thenReturn(Logger.getLogger(LocalTimeStampServiceTest.class.getName()));
        when(item.getItemMeta()).thenReturn(meta);
        when(meta.getPersistentDataContainer()).thenReturn(persistentDataContainer);
        when(configService.getExpiryDateText()).thenReturn(List.of("${expiry_date}"));
        when(configService.getExpiryDateFormat()).thenReturn(LocalConfigService.DEFAULT_EXPIRY_DATE_FORMAT);

        service = new LocalTimeStampService(plugin, configService);
    }

    /**
     * The format is read on every use rather than captured while the plugin starts up, so that
     * {@code /fs reload} applies a new one without a server restart.
     */
    @Test
    void aChangedExpiryDateFormatAppliesToTheNextStamp() {
        service.assignTimeStamp(item, ONE_DAY);
        assertTrue(lastLoreLine().matches("\\d{2}/\\d{2}/\\d{4}"),
                "expected the default MM/dd/yyyy format, got: " + lastLoreLine());

        when(configService.getExpiryDateFormat()).thenReturn("yyyy-MM-dd");

        service.assignTimeStamp(item, ONE_DAY);
        assertTrue(lastLoreLine().matches("\\d{4}-\\d{2}-\\d{2}"),
                "expected the reconfigured yyyy-MM-dd format, got: " + lastLoreLine());
    }

    @Test
    void anUnparseableExpiryDateFormatFallsBackToTheDefault() {
        when(configService.getExpiryDateFormat()).thenReturn("'unclosed");

        service.assignTimeStamp(item, ONE_DAY);

        assertTrue(lastLoreLine().matches("\\d{2}/\\d{2}/\\d{4}"),
                "expected a fallback to MM/dd/yyyy, got: " + lastLoreLine());
    }

    /**
     * Whatever {@code spoiled-food-material} names is the product of spoilage, so it must not be
     * stamped itself — a stamped product would spoil again into another stack of itself.
     */
    @Test
    void theConfiguredSpoiledFoodMaterialIsNotStampable() {
        when(configService.getSpoiledFoodMaterial()).thenReturn(Material.POISONOUS_POTATO);
        when(item.getType()).thenReturn(Material.POISONOUS_POTATO);

        assertFalse(service.isStampable(item));
    }

    /**
     * Reconfiguring the material has to release the previous one, or the food an operator moved
     * away from would stay permanently unspoilable.
     */
    @Test
    void aMaterialNoLongerConfiguredAsSpoiledFoodBecomesStampable() {
        when(configService.getSpoiledFoodMaterial()).thenReturn(Material.POISONOUS_POTATO);
        when(item.getType()).thenReturn(Material.ROTTEN_FLESH);

        assertTrue(service.isStampable(item));
    }

    /**
     * The expiry kept in persistent data is what lets an item spoil when its lore carries no date,
     * as it does when {@code text.expiry-date-lore} is configured empty; it has to be readable back.
     */
    @Test
    void anExpiryStampedWithoutLoreIsReadBackFromPersistentData() {
        when(configService.getExpiryDateText()).thenReturn(List.of());

        service.assignTimeStamp(item, ONE_DAY);
        rereadStoredExpiry();

        assertTrue(service.timeStampAssigned(item));
        assertNotNull(service.getTimeStamp(item));
    }

    /**
     * The stored value carries a date and an offset but no time, and is read as 01:01:01 on that
     * date at that offset — the time the lore fallback has always assumed.
     */
    @Test
    void aStoredExpiryIsReadAtTheTimeOfDayTheLoreFallbackAssumes() {
        when(item.hasItemMeta()).thenReturn(true);
        when(persistentDataContainer.get(any(NamespacedKey.class), eq(STRING))).thenReturn("2026-09-26+02:00");

        assertEquals(OffsetDateTime.of(2026, 9, 26, 1, 1, 1, 0, ZoneOffset.ofHours(2)), service.getTimeStamp(item));
    }

    /**
     * Makes the value written to persistent data by the last stamp readable from the mocked item.
     */
    private void rereadStoredExpiry() {
        ArgumentCaptor<String> stored = ArgumentCaptor.forClass(String.class);
        verify(persistentDataContainer).set(any(NamespacedKey.class), eq(STRING), stored.capture());
        when(persistentDataContainer.get(any(NamespacedKey.class), eq(STRING))).thenReturn(stored.getValue());
        when(item.hasItemMeta()).thenReturn(true);
    }

    /**
     * Returns the single lore line written by the most recent stamp.
     */
    private String lastLoreLine() {
        ArgumentCaptor<List<String>> captor = captureLore();
        verify(meta, atLeastOnce()).setLore(captor.capture());
        List<String> lore = captor.getValue();
        return lore.get(lore.size() - 1);
    }

    @SuppressWarnings("unchecked")
    private ArgumentCaptor<List<String>> captureLore() {
        return ArgumentCaptor.forClass(List.class);
    }
}

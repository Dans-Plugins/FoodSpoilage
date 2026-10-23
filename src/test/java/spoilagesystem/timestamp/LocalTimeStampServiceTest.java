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
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import static java.time.format.DateTimeFormatter.ISO_OFFSET_DATE;
import static org.bukkit.persistence.PersistentDataType.STRING;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Tests for {@link LocalTimeStampService}, covering how the configured {@code expiry-date-format}
 * is picked up, how an expiry is read back from persistent data or from lore alone, what
 * {@code /fs timeleft} is told about the time left, and what waxing does to an item.
 */
public class LocalTimeStampServiceTest {

    private static final Duration ONE_DAY = Duration.ofHours(24);
    private static final long MAX_OFFSET_SECONDS = 18 * 60 * 60;
    private static final List<String> DEFAULT_EXPIRY_LORE = List.of("", "&fExpiry Date:", "&f${expiry_date}");
    private static final List<String> WAXED_LORE = List.of("", "&fWaxed", "&7This item will not spoil but cannot be eaten.");

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
     * An item whose expiry passed less than a day ago has expired; it must not be reported as
     * expiring within the hour or the day.
     */
    @Test
    void anExpiryPassedLessThanADayAgoIsReportedAsExpired() {
        when(configService.getNoTimeLeftText()).thenReturn("expired");
        when(configService.getLessThanADay()).thenReturn("less than a day");
        when(configService.getLessThanAnHour()).thenReturn("less than an hour");
        when(item.hasItemMeta()).thenReturn(true);

        OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);
        LocalDate expiryDate = now.toLocalDate();
        if (expiryDate.atTime(1, 1, 1).atOffset(ZoneOffset.UTC).isAfter(now)) {
            expiryDate = expiryDate.minusDays(1);
        }
        when(persistentDataContainer.get(any(NamespacedKey.class), eq(STRING))).thenReturn(expiryDate + "Z");

        assertEquals("expired", service.getTimeLeft(item));
    }

    @Test
    void anExpiryMoreThanADayAwayIsReportedInWholeDays() {
        when(configService.getTimeLeftText()).thenReturn("This item will expire in ${time}.");
        storeExpiryAt(OffsetDateTime.now().plusDays(3).plusHours(12));

        assertEquals("This item will expire in 3 days.", service.getTimeLeft(item));
    }

    @Test
    void anExpiryLaterTodayIsReportedAsLessThanADay() {
        when(configService.getLessThanADay()).thenReturn("less than a day");
        storeExpiryAt(OffsetDateTime.now().plusHours(12));

        assertEquals("less than a day", service.getTimeLeft(item));
    }

    @Test
    void anExpiryWithinTheHourIsReportedAsLessThanAnHour() {
        when(configService.getLessThanAnHour()).thenReturn("less than an hour");
        storeExpiryAt(OffsetDateTime.now().plusMinutes(30));

        assertEquals("less than an hour", service.getTimeLeft(item));
    }

    @Test
    void anUnstampedItemHasNoTimeLeft() {
        assertNull(service.getTimeLeft(item));
    }

    @Test
    void timeIsReachedOnceTheStoredExpiryHasPassed() {
        storeExpiryAt(OffsetDateTime.now().minusHours(1));

        assertTrue(service.timeReached(item));
    }

    @Test
    void timeIsNotReachedBeforeTheStoredExpiry() {
        storeExpiryAt(OffsetDateTime.now().plusHours(1));

        assertFalse(service.timeReached(item));
    }

    @Test
    void anUnstampedItemNeverReachesItsTime() {
        assertFalse(service.timeReached(item));
    }

    /**
     * A material with no {@code spoil-time} entry is reported as {@link Duration#ZERO}, and is
     * left exactly as it was rather than being given an expiry date of today.
     */
    @Test
    void foodWithNoConfiguredSpoilTimeIsNotStamped() {
        when(item.getType()).thenReturn(Material.GOLDEN_APPLE);
        when(configService.getTime(Material.GOLDEN_APPLE)).thenReturn(Duration.ZERO);

        service.assignTimeStamp(item);

        verify(item, never()).setItemMeta(any());
        verify(persistentDataContainer, never()).set(any(NamespacedKey.class), eq(STRING), anyString());
    }

    /**
     * Items stamped before 3.0.0 carry their expiry date only in their lore, which still has to be
     * recognised as a stamp and read back as an expiry.
     */
    @Test
    void anExpiryCarriedOnlyInTheLoreIsReadBack() {
        when(configService.getExpiryDateText()).thenReturn(DEFAULT_EXPIRY_LORE);
        giveLore(List.of("", "&fExpiry Date:", "&f01/02/2030"));

        assertTrue(service.timeStampAssigned(item));
        assertEquals(LocalDate.of(2030, 1, 2).atTime(1, 1, 1).atZone(ZoneId.systemDefault()).toOffsetDateTime(),
                service.getTimeStamp(item));
    }

    @Test
    void loreWhoseDateLineHoldsNoDateIsNotAStamp() {
        when(configService.getExpiryDateText()).thenReturn(DEFAULT_EXPIRY_LORE);
        giveLore(List.of("", "&fExpiry Date:", "&fsoon"));

        assertFalse(service.timeStampAssigned(item));
        assertNull(service.getTimeStamp(item));
    }

    @Test
    void loreWithADifferentNumberOfLinesIsNotAStamp() {
        when(configService.getExpiryDateText()).thenReturn(DEFAULT_EXPIRY_LORE);
        giveLore(List.of("&f01/02/2030"));

        assertFalse(service.timeStampAssigned(item));
    }

    /**
     * Waxing a stamped item removes its expiry, both the stored date and the lore that displays
     * it, so that the waxed lore is all that remains.
     */
    @Test
    void waxingAStampedItemReplacesItsExpiryWithTheWaxedLore() {
        when(configService.getExpiryDateText()).thenReturn(DEFAULT_EXPIRY_LORE);
        when(configService.getWaxedFoodLore()).thenReturn(WAXED_LORE);
        storeExpiryAt(OffsetDateTime.now().plusDays(2));
        giveLore(List.of("", "&fExpiry Date:", "&f01/02/2030"));

        service.applyWax(item);

        verify(persistentDataContainer).remove(argThat(key -> key.getKey().equals("expiry")));
        verify(persistentDataContainer).set(argThat(key -> key.getKey().equals("waxed")), eq(STRING), eq("true"));
        verify(meta).setLore(WAXED_LORE);
        verify(item).setItemMeta(meta);
    }

    @Test
    void waxingAnUnstampedItemKeepsItsExistingLore() {
        when(configService.getExpiryDateText()).thenReturn(DEFAULT_EXPIRY_LORE);
        when(configService.getWaxedFoodLore()).thenReturn(WAXED_LORE);
        giveLore(List.of("Grandma's recipe"));

        service.applyWax(item);

        List<String> expected = new ArrayList<>(List.of("Grandma's recipe"));
        expected.addAll(WAXED_LORE);
        verify(meta).setLore(expected);
    }

    @Test
    void anItemCarryingTheWaxedMarkerIsWaxed() {
        when(item.hasItemMeta()).thenReturn(true);
        when(persistentDataContainer.has(argThat(key -> key.getKey().equals("waxed")), eq(STRING))).thenReturn(true);

        assertTrue(service.isWaxed(item));
    }

    @Test
    void anItemWithoutTheWaxedMarkerIsNotWaxed() {
        when(item.hasItemMeta()).thenReturn(true);

        assertFalse(service.isWaxed(item));
        assertFalse(service.isWaxed(null));
    }

    /**
     * Stores an expiry on the mocked item that falls on exactly the given instant. Expiries are
     * kept as a date and an offset and read at 01:01:01 on that date, so the offset is chosen to
     * put that time of day on the instant.
     */
    private void storeExpiryAt(OffsetDateTime instant) {
        long epochSecond = instant.toEpochSecond();
        LocalDate utcDate = instant.atZoneSameInstant(ZoneOffset.UTC).toLocalDate();
        for (LocalDate date : List.of(utcDate.minusDays(1), utcDate, utcDate.plusDays(1))) {
            long offsetSeconds = date.atTime(1, 1, 1).toEpochSecond(ZoneOffset.UTC) - epochSecond;
            if (Math.abs(offsetSeconds) <= MAX_OFFSET_SECONDS) {
                String stored = ISO_OFFSET_DATE.format(date.atStartOfDay().atOffset(ZoneOffset.ofTotalSeconds((int) offsetSeconds)));
                when(persistentDataContainer.get(argThat(key -> key.getKey().equals("expiry")), eq(STRING))).thenReturn(stored);
                when(item.hasItemMeta()).thenReturn(true);
                return;
            }
        }
        throw new IllegalStateException("no offset puts 01:01:01 on " + instant);
    }

    private void giveLore(List<String> lore) {
        when(item.hasItemMeta()).thenReturn(true);
        when(meta.hasLore()).thenReturn(true);
        when(meta.getLore()).thenReturn(lore);
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

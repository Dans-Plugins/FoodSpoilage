package spoilagesystem.timestamp;

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
import java.util.List;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertTrue;
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

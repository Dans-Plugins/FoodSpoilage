package spoilagesystem.rpkit;

import com.rpkit.core.service.Services;
import com.rpkit.core.service.ServicesDelegate;
import com.rpkit.food.bukkit.expiry.RPKExpiryService;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import spoilagesystem.FoodSpoilage;
import spoilagesystem.timestamp.LocalTimeStampService;

import java.time.Duration;
import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Tests for {@link FoodSpoilageRpkitExpiryService}, covering how RPKit's expiry calls are passed on to
 * {@link LocalTimeStampService}.
 */
public class FoodSpoilageRpkitExpiryServiceTest {

    @Mock
    private FoodSpoilage plugin;

    @Mock
    private LocalTimeStampService timeStampService;

    @Mock
    private ItemStack item;

    // RPKit's service registry forwards to a delegate that RPKit's core plugin installs on a real server.
    @Mock
    private ServicesDelegate servicesDelegate;

    private FoodSpoilageRpkitExpiryService expiryService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        Services.INSTANCE.setDelegate(servicesDelegate);
        expiryService = new FoodSpoilageRpkitExpiryService(plugin, timeStampService);
    }

    @Test
    void registersItselfAsRpkitsExpiryService() {
        verify(servicesDelegate).set(RPKExpiryService.class, expiryService);
    }

    @Test
    void reportsThePluginItWasCreatedFor() {
        assertSame(plugin, expiryService.getPlugin());
    }

    @Test
    void stampsAnItemWithTheGivenDuration() {
        Duration duration = Duration.ofHours(30);

        expiryService.setExpiry(item, duration);

        verify(timeStampService).assignTimeStamp(item, duration);
    }

    @Test
    void stampsAnItemWithTheTimeLeftUntilTheGivenDate() {
        OffsetDateTime before = OffsetDateTime.now();
        OffsetDateTime expiry = before.plusHours(30);

        expiryService.setExpiry(item, expiry);

        OffsetDateTime after = OffsetDateTime.now();
        ArgumentCaptor<Duration> duration = ArgumentCaptor.forClass(Duration.class);
        verify(timeStampService).assignTimeStamp(eq(item), duration.capture());
        // The duration is measured from the moment of the call, so it lies between the two readings.
        assertFalse(duration.getValue().compareTo(Duration.between(after, expiry)) < 0);
        assertFalse(duration.getValue().compareTo(Duration.between(before, expiry)) > 0);
    }

    @Test
    void stampsAnItemWithItsConfiguredSpoilTimeWhenNoExpiryIsGiven() {
        expiryService.setExpiry(item);

        verify(timeStampService).assignTimeStamp(item);
    }

    @Test
    void readsTheExpiryBackFromTheItemsTimeStamp() {
        OffsetDateTime expiry = OffsetDateTime.parse("2026-03-04T00:00:00Z");
        when(timeStampService.getTimeStamp(item)).thenReturn(expiry);

        assertSame(expiry, expiryService.getExpiry(item));
    }

    @Test
    void reportsAnItemAsExpiredOnlyOnceItsTimeHasBeenReached() {
        when(timeStampService.timeReached(item)).thenReturn(true);
        assertTrue(expiryService.isExpired(item));

        when(timeStampService.timeReached(item)).thenReturn(false);
        assertFalse(expiryService.isExpired(item));
    }
}

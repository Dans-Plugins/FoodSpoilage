package spoilagesystem.listeners;

import org.bukkit.Material;
import org.bukkit.event.block.BlockCookEvent;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import spoilagesystem.config.LocalConfigService;
import spoilagesystem.timestamp.LocalTimeStampService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Tests for {@link BlockCookListener}, covering when a furnace's cooked result is given an expiry
 * date as it is produced.
 */
public class BlockCookListenerTest {

    @Mock
    private LocalConfigService configService;

    @Mock
    private LocalTimeStampService timeStampService;

    @Mock
    private BlockCookEvent event;

    private BlockCookListener listener;

    private ItemStack cookedBeef;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        listener = new BlockCookListener(configService, timeStampService);

        cookedBeef = mock(ItemStack.class);
        when(cookedBeef.getType()).thenReturn(Material.COOKED_BEEF);
        when(event.getResult()).thenReturn(cookedBeef);
    }

    @Test
    void leavesTheCookedResultUnstampedByDefault() {
        // Stamping the output slot stalls the furnace on Minecraft 1.20.5+, so the result is left
        // vanilla and stamped later, once it reaches a player.
        when(configService.isTimestampFurnaceOutput()).thenReturn(false);

        listener.onBlockCook(event);

        verify(timeStampService, never()).stampIfEligible(any());
    }

    @Test
    void stampsTheCookedResultWhenFurnaceOutputStampingIsEnabled() {
        when(configService.isTimestampFurnaceOutput()).thenReturn(true);

        listener.onBlockCook(event);

        verify(timeStampService).stampIfEligible(cookedBeef);
    }
}

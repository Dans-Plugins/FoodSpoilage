package spoilagesystem.listeners;

import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import spoilagesystem.FoodSpoilage;
import spoilagesystem.factories.SpoiledFoodFactory;
import spoilagesystem.timestamp.LocalTimeStampService;

import java.util.logging.Logger;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Tests for {@link InventoryDragListener}, covering when food being dragged on the cursor is
 * turned into spoiled food.
 */
public class InventoryDragListenerTest {

    private static final int AMOUNT_DRAGGED = 12;

    @Mock
    private FoodSpoilage plugin;

    @Mock
    private LocalTimeStampService timeStampService;

    @Mock
    private SpoiledFoodFactory spoiledFoodFactory;

    @Mock
    private InventoryDragEvent event;

    @Mock
    private ItemStack food;

    @Mock
    private ItemStack spoiledFood;

    private InventoryDragListener listener;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        listener = new InventoryDragListener(plugin, timeStampService, spoiledFoodFactory);

        when(plugin.getLogger()).thenReturn(Logger.getLogger(InventoryDragListenerTest.class.getName()));
        when(food.getAmount()).thenReturn(AMOUNT_DRAGGED);
        when(spoiledFoodFactory.createSpoiledFood(AMOUNT_DRAGGED)).thenReturn(spoiledFood);
    }

    @Test
    void replacesExpiredFoodOnTheCursorWithTheSameAmountOfSpoiledFood() {
        givenCursor(true, true);

        listener.onInventoryDrag(event);

        verify(event).setCursor(spoiledFood);
    }

    @Test
    void leavesFoodThatHasNotExpiredYetOnTheCursor() {
        givenCursor(true, false);

        listener.onInventoryDrag(event);

        verify(spoiledFoodFactory, never()).createSpoiledFood(anyInt());
        verify(event, never()).setCursor(any());
    }

    @Test
    void leavesUnstampedItemsOnTheCursor() {
        // timeReached is stubbed true so that only the missing timestamp can stop the replacement.
        givenCursor(false, true);

        listener.onInventoryDrag(event);

        verify(event, never()).setCursor(any());
    }

    @Test
    void leavesWaxedFoodOnTheCursor() {
        // Stamped and expired are both stubbed true so that only the waxed check can stop the replacement.
        givenCursor(true, true);
        when(timeStampService.isWaxed(food)).thenReturn(true);

        listener.onInventoryDrag(event);

        verify(spoiledFoodFactory, never()).createSpoiledFood(anyInt());
        verify(event, never()).setCursor(any());
    }

    @Test
    void ignoresADragWithNothingOnTheCursor() {
        when(event.getCursor()).thenReturn(null);

        listener.onInventoryDrag(event);

        verify(timeStampService, never()).timeStampAssigned(any());
        verify(event, never()).setCursor(any());
    }

    private void givenCursor(boolean stamped, boolean expired) {
        when(event.getCursor()).thenReturn(food);
        when(timeStampService.timeStampAssigned(food)).thenReturn(stamped);
        when(timeStampService.timeReached(food)).thenReturn(expired);
    }
}

package spoilagesystem.listeners;

import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import spoilagesystem.timestamp.LocalTimeStampService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Tests for {@link EntityPickupItemListener}, covering which picked-up items are given an expiry date.
 */
public class EntityPickupItemListenerTest {

    @Mock
    private LocalTimeStampService timeStampService;

    @Mock
    private EntityPickupItemEvent event;

    @Mock
    private Item droppedItem;

    private EntityPickupItemListener listener;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        listener = new EntityPickupItemListener(timeStampService);

        when(event.getItem()).thenReturn(droppedItem);
    }

    @Test
    void stampsUnstampedEdibleFoodAndHandsTheStampedStackBack() {
        ItemStack bread = item(Material.BREAD);
        ItemStack stampedBread = item(Material.BREAD);
        when(timeStampService.assignTimeStamp(bread)).thenReturn(stampedBread);
        givenPickedUp(bread);

        listener.onEntityPickupItem(event);

        verify(droppedItem).setItemStack(stampedBread);
    }

    @Test
    void doesNotStampNonEdibleItems() {
        givenPickedUp(item(Material.STONE));

        listener.onEntityPickupItem(event);

        verify(timeStampService, never()).assignTimeStamp(any(ItemStack.class));
        verify(droppedItem, never()).setItemStack(any());
    }

    @Test
    void doesNotStampTheSpoiledFoodMaterial() {
        ItemStack spoiled = item(Material.ROTTEN_FLESH);
        when(timeStampService.isSpoiledFoodMaterial(spoiled)).thenReturn(true);
        givenPickedUp(spoiled);

        listener.onEntityPickupItem(event);

        verify(timeStampService, never()).assignTimeStamp(any(ItemStack.class));
    }

    @Test
    void doesNotRestampFoodThatAlreadyHasAnExpiryDate() {
        ItemStack stamped = item(Material.BREAD);
        when(timeStampService.timeStampAssigned(stamped)).thenReturn(true);
        givenPickedUp(stamped);

        listener.onEntityPickupItem(event);

        verify(timeStampService, never()).assignTimeStamp(any(ItemStack.class));
        verify(droppedItem, never()).setItemStack(any());
    }

    @Test
    void doesNotStampWaxedFood() {
        ItemStack waxed = item(Material.BREAD);
        when(timeStampService.isWaxed(waxed)).thenReturn(true);
        givenPickedUp(waxed);

        listener.onEntityPickupItem(event);

        verify(timeStampService, never()).assignTimeStamp(any(ItemStack.class));
    }

    private ItemStack item(Material type) {
        ItemStack item = mock(ItemStack.class);
        when(item.getType()).thenReturn(type);
        return item;
    }

    private void givenPickedUp(ItemStack itemStack) {
        when(droppedItem.getItemStack()).thenReturn(itemStack);
    }
}

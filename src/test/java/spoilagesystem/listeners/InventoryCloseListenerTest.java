package spoilagesystem.listeners;

import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantInventory;
import org.bukkit.inventory.PlayerInventory;
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
 * Tests for {@link InventoryCloseListener}, covering which items in the player's own inventory
 * are stamped when any container is closed.
 */
public class InventoryCloseListenerTest {

    @Mock
    private LocalTimeStampService timeStampService;

    @Mock
    private InventoryCloseEvent event;

    @Mock
    private HumanEntity player;

    @Mock
    private PlayerInventory playerInventory;

    private InventoryCloseListener listener;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        listener = new InventoryCloseListener(timeStampService);

        when(event.getPlayer()).thenReturn(player);
        when(player.getInventory()).thenReturn(playerInventory);
    }

    @Test
    void stampsUnstampedEdibleFood() {
        ItemStack bread = item(Material.BREAD);
        givenInventory(bread);

        listener.onInventoryClose(event);

        verify(timeStampService).assignTimeStamp(bread);
    }

    @Test
    void stampsFoodTakenFromAVillagerTradeWhenTheMerchantWindowCloses() {
        // A traded result lands in the player's inventory without passing through any other
        // stamping path, so closing the trade window is what gives it an expiry date.
        when(event.getInventory()).thenReturn(mock(MerchantInventory.class));
        ItemStack tradedBread = item(Material.BREAD);
        givenInventory(tradedBread);

        listener.onInventoryClose(event);

        verify(timeStampService).assignTimeStamp(tradedBread);
    }

    @Test
    void skipsEmptySlotsAndStillStampsTheRest() {
        ItemStack apple = item(Material.APPLE);
        givenInventory(null, apple, null);

        listener.onInventoryClose(event);

        verify(timeStampService).assignTimeStamp(apple);
    }

    @Test
    void doesNotStampNonEdibleItems() {
        givenInventory(item(Material.STONE));

        listener.onInventoryClose(event);

        verify(timeStampService, never()).assignTimeStamp(any(ItemStack.class));
    }

    @Test
    void doesNotStampTheSpoiledFoodMaterial() {
        ItemStack spoiled = item(Material.ROTTEN_FLESH);
        when(timeStampService.isSpoiledFoodMaterial(spoiled)).thenReturn(true);
        givenInventory(spoiled);

        listener.onInventoryClose(event);

        verify(timeStampService, never()).assignTimeStamp(any(ItemStack.class));
    }

    @Test
    void doesNotRestampFoodThatAlreadyHasAnExpiryDate() {
        ItemStack stamped = item(Material.BREAD);
        when(timeStampService.timeStampAssigned(stamped)).thenReturn(true);
        givenInventory(stamped);

        listener.onInventoryClose(event);

        verify(timeStampService, never()).assignTimeStamp(any(ItemStack.class));
    }

    @Test
    void doesNotStampWaxedFood() {
        ItemStack waxed = item(Material.BREAD);
        when(timeStampService.isWaxed(waxed)).thenReturn(true);
        givenInventory(waxed);

        listener.onInventoryClose(event);

        verify(timeStampService, never()).assignTimeStamp(any(ItemStack.class));
    }

    private ItemStack item(Material type) {
        ItemStack item = mock(ItemStack.class);
        when(item.getType()).thenReturn(type);
        return item;
    }

    private void givenInventory(ItemStack... contents) {
        when(playerInventory.getContents()).thenReturn(contents);
    }
}

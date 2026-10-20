package spoilagesystem.listeners;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
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
 * Tests for {@link PlayerJoinListener}, covering which items in a joining player's inventory are
 * given an expiry date.
 */
public class PlayerJoinListenerTest {

    @Mock
    private LocalTimeStampService timeStampService;

    @Mock
    private PlayerJoinEvent event;

    @Mock
    private Player player;

    @Mock
    private PlayerInventory playerInventory;

    private PlayerJoinListener listener;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        listener = new PlayerJoinListener(timeStampService);

        when(event.getPlayer()).thenReturn(player);
        when(player.getInventory()).thenReturn(playerInventory);
    }

    @Test
    void stampsUnstampedEdibleFood() {
        ItemStack bread = item(Material.BREAD);
        givenInventory(bread);

        listener.onPlayerJoin(event);

        verify(timeStampService).assignTimeStamp(bread);
    }

    @Test
    void skipsEmptySlotsAndStillStampsTheRest() {
        ItemStack apple = item(Material.APPLE);
        givenInventory(null, apple, null);

        listener.onPlayerJoin(event);

        verify(timeStampService).assignTimeStamp(apple);
    }

    @Test
    void doesNotStampNonEdibleItems() {
        givenInventory(item(Material.STONE));

        listener.onPlayerJoin(event);

        verify(timeStampService, never()).assignTimeStamp(any(ItemStack.class));
    }

    @Test
    void doesNotStampTheSpoiledFoodMaterial() {
        ItemStack spoiled = item(Material.ROTTEN_FLESH);
        when(timeStampService.isSpoiledFoodMaterial(spoiled)).thenReturn(true);
        givenInventory(spoiled);

        listener.onPlayerJoin(event);

        verify(timeStampService, never()).assignTimeStamp(any(ItemStack.class));
    }

    @Test
    void doesNotRestampFoodThatAlreadyHasAnExpiryDate() {
        ItemStack stamped = item(Material.BREAD);
        when(timeStampService.timeStampAssigned(stamped)).thenReturn(true);
        givenInventory(stamped);

        listener.onPlayerJoin(event);

        verify(timeStampService, never()).assignTimeStamp(any(ItemStack.class));
    }

    @Test
    void doesNotStampWaxedFood() {
        ItemStack waxed = item(Material.BREAD);
        when(timeStampService.isWaxed(waxed)).thenReturn(true);
        givenInventory(waxed);

        listener.onPlayerJoin(event);

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

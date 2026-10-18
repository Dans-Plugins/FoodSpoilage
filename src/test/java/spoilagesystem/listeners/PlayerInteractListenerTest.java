package spoilagesystem.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import spoilagesystem.FoodSpoilage;
import spoilagesystem.factories.SpoiledFoodFactory;
import spoilagesystem.timestamp.LocalTimeStampService;

import java.util.logging.Logger;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Tests for {@link PlayerInteractListener}, covering when expired food in a player's hand is
 * turned into spoiled food and when eating is blocked.
 */
public class PlayerInteractListenerTest {

    private static final int AMOUNT_HELD = 5;

    @Mock
    private FoodSpoilage plugin;

    @Mock
    private LocalTimeStampService timeStampService;

    @Mock
    private SpoiledFoodFactory spoiledFoodFactory;

    @Mock
    private PlayerInteractEvent event;

    @Mock
    private Player player;

    @Mock
    private PlayerInventory playerInventory;

    @Mock
    private ItemStack food;

    @Mock
    private ItemStack spoiledFood;

    private PlayerInteractListener listener;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        listener = new PlayerInteractListener(plugin, timeStampService, spoiledFoodFactory);

        when(plugin.getLogger()).thenReturn(Logger.getLogger(PlayerInteractListenerTest.class.getName()));
        when(event.getPlayer()).thenReturn(player);
        when(player.getInventory()).thenReturn(playerInventory);
        when(food.getAmount()).thenReturn(AMOUNT_HELD);
        when(spoiledFoodFactory.createSpoiledFood(AMOUNT_HELD)).thenReturn(spoiledFood);
    }

    @Test
    void replacesExpiredFoodInTheMainHandWithTheSameAmountOfSpoiledFood() {
        givenHeldFood(EquipmentSlot.HAND, true, true);

        listener.onPlayerInteract(event);

        verify(playerInventory).setItemInMainHand(spoiledFood);
        verify(playerInventory, never()).setItemInOffHand(any());
        verify(event).setCancelled(true);
    }

    @Test
    void replacesExpiredFoodInTheOffHandWithTheSameAmountOfSpoiledFood() {
        givenHeldFood(EquipmentSlot.OFF_HAND, true, true);

        listener.onPlayerInteract(event);

        verify(playerInventory).setItemInOffHand(spoiledFood);
        verify(playerInventory, never()).setItemInMainHand(any());
        verify(event).setCancelled(true);
    }

    @Test
    void leavesFoodThatHasNotExpiredYetInHand() {
        givenHeldFood(EquipmentSlot.HAND, true, false);

        listener.onPlayerInteract(event);

        verify(spoiledFoodFactory, never()).createSpoiledFood(anyInt());
        verify(playerInventory, never()).setItemInMainHand(any());
        verify(event, never()).setCancelled(anyBoolean());
    }

    @Test
    void leavesUnstampedItemsAlone() {
        // timeReached is stubbed true so that only the missing timestamp can stop the replacement.
        givenHeldFood(EquipmentSlot.HAND, false, true);

        listener.onPlayerInteract(event);

        verify(spoiledFoodFactory, never()).createSpoiledFood(anyInt());
        verify(playerInventory, never()).setItemInMainHand(any());
        verify(event, never()).setCancelled(anyBoolean());
    }

    @Test
    void ignoresInteractionsWithNothingInHand() {
        when(event.getItem()).thenReturn(null);

        listener.onPlayerInteract(event);

        verify(timeStampService, never()).timeStampAssigned(any());
        verify(event, never()).setCancelled(anyBoolean());
    }

    @Test
    void cancelsAnInteractionWithStampedFoodThatReportsNoHand() {
        givenHeldFood(null, true, true);

        listener.onPlayerInteract(event);

        verify(spoiledFoodFactory, never()).createSpoiledFood(anyInt());
        verify(event).setCancelled(true);
    }

    @Test
    void preventsWaxedFoodFromBeingEaten() {
        PlayerItemConsumeEvent consumeEvent = consumeEventFor(food);
        when(timeStampService.isWaxed(food)).thenReturn(true);

        listener.onPlayerItemConsume(consumeEvent);

        verify(consumeEvent).setCancelled(true);
    }

    @Test
    void allowsUnwaxedFoodToBeEaten() {
        PlayerItemConsumeEvent consumeEvent = consumeEventFor(food);

        listener.onPlayerItemConsume(consumeEvent);

        verify(consumeEvent, never()).setCancelled(anyBoolean());
    }

    private void givenHeldFood(EquipmentSlot hand, boolean stamped, boolean expired) {
        when(event.getItem()).thenReturn(food);
        when(event.getHand()).thenReturn(hand);
        when(timeStampService.timeStampAssigned(food)).thenReturn(stamped);
        when(timeStampService.timeReached(food)).thenReturn(expired);
    }

    private PlayerItemConsumeEvent consumeEventFor(ItemStack item) {
        PlayerItemConsumeEvent consumeEvent = mock(PlayerItemConsumeEvent.class);
        when(consumeEvent.getItem()).thenReturn(item);
        return consumeEvent;
    }
}

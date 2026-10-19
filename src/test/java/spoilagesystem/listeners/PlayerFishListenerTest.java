package spoilagesystem.listeners;

import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerFishEvent;
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
 * Tests for {@link PlayerFishListener}, covering which catches are given an expiry date.
 */
public class PlayerFishListenerTest {

    @Mock
    private LocalTimeStampService timeStampService;

    @Mock
    private PlayerFishEvent event;

    @Mock
    private Item caughtItem;

    private PlayerFishListener listener;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        listener = new PlayerFishListener(timeStampService);
    }

    @Test
    void stampsACaughtFishAndHandsTheStampedStackBack() {
        ItemStack cod = item(Material.COD);
        ItemStack stampedCod = item(Material.COD);
        when(timeStampService.assignTimeStamp(cod)).thenReturn(stampedCod);
        givenCaught(cod);

        listener.onPlayerFish(event);

        verify(caughtItem).setItemStack(stampedCod);
    }

    @Test
    void ignoresAnEventWithNothingCaught() {
        when(event.getCaught()).thenReturn(null);

        listener.onPlayerFish(event);

        verify(timeStampService, never()).assignTimeStamp(any(ItemStack.class));
    }

    @Test
    void ignoresAHookedEntityThatIsNotAnItem() {
        when(event.getCaught()).thenReturn(mock(Player.class));

        listener.onPlayerFish(event);

        verify(timeStampService, never()).assignTimeStamp(any(ItemStack.class));
    }

    @Test
    void doesNotStampANonEdibleCatch() {
        givenCaught(item(Material.LEATHER_BOOTS));

        listener.onPlayerFish(event);

        verify(timeStampService, never()).assignTimeStamp(any(ItemStack.class));
        verify(caughtItem, never()).setItemStack(any());
    }

    @Test
    void doesNotStampACaughtStackOfTheSpoiledFoodMaterial() {
        // Rotten flesh is in the vanilla fishing junk table, and is also the default spoiled-food
        // material, which is never given an expiry date.
        ItemStack rottenFlesh = item(Material.ROTTEN_FLESH);
        when(timeStampService.isSpoiledFoodMaterial(rottenFlesh)).thenReturn(true);
        givenCaught(rottenFlesh);

        listener.onPlayerFish(event);

        verify(timeStampService, never()).assignTimeStamp(any(ItemStack.class));
        verify(caughtItem, never()).setItemStack(any());
    }

    @Test
    void doesNotStampWaxedFood() {
        ItemStack waxed = item(Material.COD);
        when(timeStampService.isWaxed(waxed)).thenReturn(true);
        givenCaught(waxed);

        listener.onPlayerFish(event);

        verify(timeStampService, never()).assignTimeStamp(any(ItemStack.class));
    }

    private ItemStack item(Material type) {
        ItemStack item = mock(ItemStack.class);
        when(item.getType()).thenReturn(type);
        return item;
    }

    private void givenCaught(ItemStack itemStack) {
        when(event.getCaught()).thenReturn(caughtItem);
        when(caughtItem.getItemStack()).thenReturn(itemStack);
    }
}

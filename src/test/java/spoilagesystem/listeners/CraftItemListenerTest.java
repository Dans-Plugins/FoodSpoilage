package spoilagesystem.listeners;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.Recipe;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import spoilagesystem.config.LocalConfigService;
import spoilagesystem.factories.SpoiledFoodFactory;
import spoilagesystem.timestamp.LocalTimeStampService;

import java.time.Duration;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Tests for {@link CraftItemListener}, covering how the results of a partially spoiled craft are
 * delivered to the player.
 */
public class CraftItemListenerTest {

    private static final Material CRAFTED_TYPE = Material.COOKIE;
    private static final int AMOUNT_CRAFTED = 8;

    @Mock
    private LocalConfigService configService;

    @Mock
    private LocalTimeStampService timeStampService;

    @Mock
    private SpoiledFoodFactory spoiledFoodFactory;

    @Mock
    private CraftItemEvent event;

    @Mock
    private ItemStack craftedItem;

    @Mock
    private ItemStack recipeResult;

    @Mock
    private ItemStack spoiledFood;

    @Mock
    private Recipe recipe;

    @Mock
    private HumanEntity crafter;

    @Mock
    private PlayerInventory crafterInventory;

    @Mock
    private World world;

    @Mock
    private Location location;

    private CraftItemListener listener;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // The plugin instance is unused by the craft handling, so it is left out.
        listener = new CraftItemListener(null, configService, timeStampService, spoiledFoodFactory);

        when(event.getCurrentItem()).thenReturn(craftedItem);
        when(event.getRecipe()).thenReturn(recipe);
        when(event.getWhoClicked()).thenReturn(crafter);
        when(event.isShiftClick()).thenReturn(false);
        when(craftedItem.getType()).thenReturn(CRAFTED_TYPE);
        when(recipe.getResult()).thenReturn(recipeResult);
        when(recipeResult.getAmount()).thenReturn(AMOUNT_CRAFTED);
        when(recipeResult.getType()).thenReturn(CRAFTED_TYPE);
        when(crafter.getInventory()).thenReturn(crafterInventory);
        when(crafter.getWorld()).thenReturn(world);
        when(crafter.getLocation()).thenReturn(location);
        // An empty inventory, so the amount crafted is never trimmed for lack of space.
        when(crafterInventory.getStorageContents()).thenReturn(new ItemStack[36]);
        when(configService.getTime(CRAFTED_TYPE)).thenReturn(Duration.ofHours(1));
        when(timeStampService.isWaxed(craftedItem)).thenReturn(false);
        when(timeStampService.assignTimeStamp(craftedItem)).thenReturn(craftedItem);
        when(crafterInventory.addItem(any(ItemStack[].class))).thenReturn(new HashMap<>());
    }

    @Test
    void partiallySpoiledOrdinaryClickPlacesFreshFoodInResultSlotAndGivesSpoiledFoodToPlayer() {
        int spoiledAmount = 3;
        when(configService.determineSpoiledAmount(CRAFTED_TYPE, AMOUNT_CRAFTED)).thenReturn(spoiledAmount);
        when(spoiledFoodFactory.createSpoiledFood(spoiledAmount)).thenReturn(spoiledFood);

        listener.onCraftItem(event);

        // The unspoiled remainder stays in the result slot.
        verify(craftedItem).setAmount(AMOUNT_CRAFTED - spoiledAmount);
        verify(event).setCurrentItem(craftedItem);
        // The spoiled portion is handed over as well rather than being discarded.
        assertEquals(spoiledFood, captureAddedItem());
        verify(world, never()).dropItem(any(Location.class), any(ItemStack.class));
    }

    @Test
    void partiallySpoiledOrdinaryClickDropsSpoiledFoodWhenTheInventoryIsFull() {
        int spoiledAmount = 3;
        when(configService.determineSpoiledAmount(CRAFTED_TYPE, AMOUNT_CRAFTED)).thenReturn(spoiledAmount);
        when(spoiledFoodFactory.createSpoiledFood(spoiledAmount)).thenReturn(spoiledFood);
        HashMap<Integer, ItemStack> leftovers = new HashMap<>();
        leftovers.put(0, spoiledFood);
        when(crafterInventory.addItem(any(ItemStack[].class))).thenReturn(leftovers);

        listener.onCraftItem(event);

        verify(event).setCurrentItem(craftedItem);
        verify(world).dropItem(location, spoiledFood);
    }

    @Test
    void fullySpoiledOrdinaryClickPlacesSpoiledFoodInTheResultSlot() {
        when(configService.determineSpoiledAmount(CRAFTED_TYPE, AMOUNT_CRAFTED)).thenReturn(AMOUNT_CRAFTED);
        when(spoiledFoodFactory.createSpoiledFood(AMOUNT_CRAFTED)).thenReturn(spoiledFood);

        listener.onCraftItem(event);

        verify(event).setCurrentItem(spoiledFood);
        verify(crafterInventory, never()).addItem(any(ItemStack[].class));
        verify(world, never()).dropItem(any(Location.class), any(ItemStack.class));
    }

    @Test
    void unspoiledOrdinaryClickPlacesTimeStampedFoodInTheResultSlot() {
        when(configService.determineSpoiledAmount(CRAFTED_TYPE, AMOUNT_CRAFTED)).thenReturn(0);

        listener.onCraftItem(event);

        verify(spoiledFoodFactory, never()).createSpoiledFood(anyInt());
        verify(timeStampService).assignTimeStamp(craftedItem);
        verify(event).setCurrentItem(craftedItem);
        verify(crafterInventory, never()).addItem(any(ItemStack[].class));
    }

    /**
     * Returns the single stack passed to the crafter's inventory, failing the test if more or
     * fewer than one stack was handed over.
     */
    private ItemStack captureAddedItem() {
        org.mockito.ArgumentCaptor<ItemStack[]> captor = org.mockito.ArgumentCaptor.forClass(ItemStack[].class);
        verify(crafterInventory, times(1)).addItem(captor.capture());
        ItemStack[] added = captor.getValue();
        assertEquals(1, added.length);
        return added[0];
    }
}

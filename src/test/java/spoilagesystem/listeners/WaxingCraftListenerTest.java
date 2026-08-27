package spoilagesystem.listeners;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapelessRecipe;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import spoilagesystem.config.LocalConfigService;
import spoilagesystem.timestamp.LocalTimeStampService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Tests for {@link WaxingCraftListener}, covering what the waxing recipe produces while the
 * feature is switched on and while it is switched off.
 */
public class WaxingCraftListenerTest {

    private static final NamespacedKey WAXING_KEY = new NamespacedKey("foodspoilage", "waxing");
    // HONEYCOMB, the shipped default, does not exist in the API version this project builds
    // against; the material only has to be non-edible and resolvable.
    private static final Material WAX_MATERIAL = Material.PAPER;
    private static final Material FOOD_MATERIAL = Material.COOKIE;

    @Mock
    private LocalConfigService configService;

    @Mock
    private LocalTimeStampService timeStampService;

    @Mock
    private PrepareItemCraftEvent prepareEvent;

    @Mock
    private CraftItemEvent craftEvent;

    @Mock
    private CraftingInventory inventory;

    @Mock
    private ShapelessRecipe recipe;

    @Mock
    private ItemStack waxItem;

    @Mock
    private ItemStack foodItem;

    @Mock
    private ItemStack waxedResult;

    private WaxingCraftListener listener;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        listener = new WaxingCraftListener(configService, timeStampService, WAXING_KEY);

        when(recipe.getKey()).thenReturn(WAXING_KEY);
        when(prepareEvent.getRecipe()).thenReturn(recipe);
        when(prepareEvent.getInventory()).thenReturn(inventory);
        when(craftEvent.getRecipe()).thenReturn(recipe);
        when(inventory.getMatrix()).thenReturn(new ItemStack[]{waxItem, foodItem});
        when(waxItem.getType()).thenReturn(WAX_MATERIAL);
        when(foodItem.getType()).thenReturn(FOOD_MATERIAL);
        when(foodItem.clone()).thenReturn(waxedResult);
        when(configService.isWaxingEnabled()).thenReturn(true);
        when(configService.getWaxMaterial()).thenReturn(WAX_MATERIAL);
        when(timeStampService.isWaxed(foodItem)).thenReturn(false);
    }

    @Test
    void preparingTheRecipeWhileWaxingIsEnabledProducesWaxedFood() {
        listener.onPrepareItemCraft(prepareEvent);

        verify(waxedResult).setAmount(1);
        verify(timeStampService).applyWax(waxedResult);
        verify(inventory).setResult(waxedResult);
    }

    /**
     * The recipe's registered result is only a placeholder that this listener overwrites, so
     * leaving it in place while the feature is off would let a player craft the placeholder.
     */
    @Test
    void preparingTheRecipeWhileWaxingIsDisabledClearsTheResult() {
        when(configService.isWaxingEnabled()).thenReturn(false);

        listener.onPrepareItemCraft(prepareEvent);

        verify(inventory).setResult(null);
        verify(timeStampService, never()).applyWax(any(ItemStack.class));
    }

    @Test
    void preparingTheRecipeWithAnUnresolvableWaxMaterialClearsTheResult() {
        when(configService.getWaxMaterial()).thenReturn(null);

        listener.onPrepareItemCraft(prepareEvent);

        verify(inventory).setResult(null);
        verify(timeStampService, never()).applyWax(any(ItemStack.class));
    }

    @Test
    void craftingTheRecipeWhileWaxingIsDisabledIsCancelled() {
        when(configService.isWaxingEnabled()).thenReturn(false);

        listener.onCraftItem(craftEvent);

        verify(craftEvent).setCancelled(true);
        verify(craftEvent, never()).getCurrentItem();
    }

    @Test
    void anUnrelatedRecipeIsLeftAlone() {
        when(recipe.getKey()).thenReturn(new NamespacedKey("minecraft", "bread"));

        listener.onPrepareItemCraft(prepareEvent);

        verify(inventory, never()).setResult(any());
    }
}

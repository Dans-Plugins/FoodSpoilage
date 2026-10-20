package spoilagesystem.listeners;

import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.FurnaceInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import spoilagesystem.config.LocalConfigService;
import spoilagesystem.timestamp.LocalTimeStampService;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Tests for {@link InventoryOpenListener}, covering which slots of the opened container and of the
 * player's own inventory are stamped when a container is opened.
 */
public class InventoryOpenListenerTest {

    @Mock
    private LocalConfigService configService;

    @Mock
    private LocalTimeStampService timeStampService;

    @Mock
    private InventoryOpenEvent event;

    @Mock
    private HumanEntity player;

    @Mock
    private PlayerInventory playerInventory;

    private InventoryOpenListener listener;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        listener = new InventoryOpenListener(configService, timeStampService);

        when(event.getPlayer()).thenReturn(player);
        when(player.getInventory()).thenReturn(playerInventory);
        when(playerInventory.getContents()).thenReturn(new ItemStack[0]);
    }

    @Test
    void stampsEverySlotOfAChest() {
        ItemStack bread = item(Material.BREAD);
        ItemStack apple = item(Material.APPLE);
        ItemStack carrot = item(Material.CARROT);
        givenOpened(mock(Inventory.class), bread, apple, carrot);

        listener.onInventoryOpen(event);

        verify(timeStampService).stampIfEligible(bread);
        verify(timeStampService).stampIfEligible(apple);
        verify(timeStampService).stampIfEligible(carrot);
    }

    @Test
    void skipsTheFurnaceResultSlotByDefault() {
        // Stamping the result slot stalls the furnace on Minecraft 1.20.5+, so it is left vanilla
        // while the input and fuel slots are still stamped.
        when(configService.isTimestampFurnaceOutput()).thenReturn(false);
        ItemStack rawBeef = item(Material.BEEF);
        ItemStack fuel = item(Material.COAL);
        ItemStack cookedBeef = item(Material.COOKED_BEEF);
        givenOpened(mock(FurnaceInventory.class), rawBeef, fuel, cookedBeef);

        listener.onInventoryOpen(event);

        verify(timeStampService).stampIfEligible(rawBeef);
        verify(timeStampService).stampIfEligible(fuel);
        verify(timeStampService, never()).stampIfEligible(cookedBeef);
    }

    @Test
    void stampsTheFurnaceResultSlotWhenFurnaceOutputStampingIsEnabled() {
        when(configService.isTimestampFurnaceOutput()).thenReturn(true);
        ItemStack rawBeef = item(Material.BEEF);
        ItemStack fuel = item(Material.COAL);
        ItemStack cookedBeef = item(Material.COOKED_BEEF);
        givenOpened(mock(FurnaceInventory.class), rawBeef, fuel, cookedBeef);

        listener.onInventoryOpen(event);

        verify(timeStampService).stampIfEligible(rawBeef);
        verify(timeStampService).stampIfEligible(fuel);
        verify(timeStampService).stampIfEligible(cookedBeef);
    }

    @Test
    void stampsTheThirdSlotOfAContainerThatIsNotAFurnace() {
        // The result-slot exclusion is keyed on the furnace inventory type, not on slot index 2 alone.
        when(configService.isTimestampFurnaceOutput()).thenReturn(false);
        ItemStack thirdSlot = item(Material.BREAD);
        givenOpened(mock(Inventory.class), null, null, thirdSlot);

        listener.onInventoryOpen(event);

        verify(timeStampService).stampIfEligible(thirdSlot);
    }

    @Test
    void stampsThePlayersOwnInventoryToo() {
        givenOpened(mock(Inventory.class));
        ItemStack carried = item(Material.BREAD);
        when(playerInventory.getContents()).thenReturn(new ItemStack[]{carried});

        listener.onInventoryOpen(event);

        verify(timeStampService).stampIfEligible(carried);
    }

    private ItemStack item(Material type) {
        ItemStack item = mock(ItemStack.class);
        when(item.getType()).thenReturn(type);
        return item;
    }

    private void givenOpened(Inventory inventory, ItemStack... contents) {
        when(inventory.getContents()).thenReturn(contents);
        when(event.getInventory()).thenReturn(inventory);
    }
}

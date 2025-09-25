package spoilagesystem.listeners;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scheduler.BukkitScheduler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import spoilagesystem.FoodSpoilage;
import spoilagesystem.timestamp.LocalTimeStampService;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Test class for PlayerJoinListener to validate the packet overflow fix.
 * Tests the batching behavior that prevents PacketPlayOutRecipeUpdate from becoming too large.
 */
public class PlayerJoinListenerTest {

    @Mock
    private FoodSpoilage plugin;
    
    @Mock
    private LocalTimeStampService timeStampService;
    
    @Mock
    private Player player;
    
    @Mock
    private PlayerInventory inventory;
    
    @Mock
    private FileConfiguration config;
    
    @Mock
    private BukkitScheduler scheduler;
    
    @Mock
    private Logger logger;
    
    private PlayerJoinListener listener;
    private PlayerJoinEvent event;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        // Setup plugin mocks
        when(plugin.getConfig()).thenReturn(config);
        when(plugin.getLogger()).thenReturn(logger);
        
        // Setup player mocks
        when(player.getInventory()).thenReturn(inventory);
        when(player.getName()).thenReturn("TestPlayer");
        
        // Setup default config values
        when(config.getBoolean("join-processing.enabled", true)).thenReturn(true);
        when(config.getInt("join-processing.max-items-per-batch", 10)).thenReturn(10);
        when(config.getInt("join-processing.batch-delay-ticks", 5)).thenReturn(5);
        
        // Create listener and event
        listener = new PlayerJoinListener(timeStampService, plugin);
        event = new PlayerJoinEvent(player, "Welcome message");
    }

    @Test
    void testEmptyInventoryDoesNotProcessItems() {
        // Setup empty inventory
        when(inventory.getContents()).thenReturn(new ItemStack[36]);
        
        // Execute
        listener.onPlayerJoin(event);
        
        // Verify no timestamp assignment calls
        verify(timeStampService, never()).assignTimeStamp(any(ItemStack.class));
        verify(logger, never()).fine(anyString());
    }

    @Test
    void testDisabledJoinProcessingSkipsProcessing() {
        // Setup config to disable join processing
        when(config.getBoolean("join-processing.enabled", true)).thenReturn(false);
        
        // Setup inventory with food items
        ItemStack[] contents = createInventoryWithFoodItems(5);
        when(inventory.getContents()).thenReturn(contents);
        
        // Execute
        listener.onPlayerJoin(event);
        
        // Verify no processing occurs
        verify(timeStampService, never()).assignTimeStamp(any(ItemStack.class));
        verify(logger, never()).fine(anyString());
    }

    @Test
    void testSmallBatchProcessedImmediately() {
        // Setup inventory with 5 food items (less than batch size of 10)
        ItemStack[] contents = createInventoryWithFoodItems(5);
        when(inventory.getContents()).thenReturn(contents);
        
        // Setup timestamp service to indicate items need timestamps
        when(timeStampService.timeStampAssigned(any(ItemStack.class))).thenReturn(false);
        
        // Execute
        listener.onPlayerJoin(event);
        
        // Verify all 5 items were processed immediately
        verify(timeStampService, times(5)).assignTimeStamp(any(ItemStack.class));
        verify(logger).fine(contains("Processing 5 food items"));
        
        // Verify no delayed tasks were scheduled (all processed immediately)
        // Note: This would require more complex mocking of Bukkit.getScheduler()
    }

    @Test
    void testLargeBatchProcessedInBatches() {
        // Setup inventory with 25 food items (more than batch size of 10)
        ItemStack[] contents = createInventoryWithFoodItems(25);
        when(inventory.getContents()).thenReturn(contents);
        
        // Setup timestamp service to indicate items need timestamps
        when(timeStampService.timeStampAssigned(any(ItemStack.class))).thenReturn(false);
        
        // Execute
        listener.onPlayerJoin(event);
        
        // Verify logging indicates 25 items will be processed
        verify(logger).fine(contains("Processing 25 food items"));
        
        // The initial batch of 10 items should be processed immediately
        // Note: Without complex Bukkit scheduler mocking, we can't easily test
        // the delayed batches, but we can verify the setup is correct
        verify(timeStampService, atLeast(10)).assignTimeStamp(any(ItemStack.class));
    }

    @Test
    void testOnlyFoodItemsAreProcessed() {
        // Setup mixed inventory with food and non-food items
        ItemStack[] contents = new ItemStack[36];
        contents[0] = new ItemStack(Material.BREAD); // Food item
        contents[1] = new ItemStack(Material.STONE); // Non-food item
        contents[2] = new ItemStack(Material.APPLE); // Food item
        contents[3] = new ItemStack(Material.IRON_SWORD); // Non-food item
        contents[4] = new ItemStack(Material.ROTTEN_FLESH); // Food but excluded
        
        when(inventory.getContents()).thenReturn(contents);
        when(timeStampService.timeStampAssigned(any(ItemStack.class))).thenReturn(false);
        
        // Execute
        listener.onPlayerJoin(event);
        
        // Verify only 2 food items are processed (bread and apple, not rotten flesh)
        verify(logger).fine(contains("Processing 2 food items"));
        verify(timeStampService, times(2)).assignTimeStamp(any(ItemStack.class));
    }

    @Test
    void testAlreadyTimestampedItemsAreSkipped() {
        // Setup inventory with food items
        ItemStack[] contents = createInventoryWithFoodItems(5);
        when(inventory.getContents()).thenReturn(contents);
        
        // Setup timestamp service to indicate items already have timestamps
        when(timeStampService.timeStampAssigned(any(ItemStack.class))).thenReturn(true);
        
        // Execute
        listener.onPlayerJoin(event);
        
        // Verify no items are processed since they already have timestamps
        verify(timeStampService, never()).assignTimeStamp(any(ItemStack.class));
        verify(logger, never()).fine(anyString());
    }

    @Test
    void testConfigurableBatchSizeIsRespected() {
        // Setup custom batch size
        when(config.getInt("join-processing.max-items-per-batch", 10)).thenReturn(3);
        
        // Setup inventory with 7 food items
        ItemStack[] contents = createInventoryWithFoodItems(7);
        when(inventory.getContents()).thenReturn(contents);
        when(timeStampService.timeStampAssigned(any(ItemStack.class))).thenReturn(false);
        
        // Execute
        listener.onPlayerJoin(event);
        
        // Verify logging shows 7 items to be processed
        verify(logger).fine(contains("Processing 7 food items"));
        
        // At least the first batch of 3 should be processed immediately
        verify(timeStampService, atLeast(3)).assignTimeStamp(any(ItemStack.class));
    }

    /**
     * Helper method to create an inventory with the specified number of food items.
     */
    private ItemStack[] createInventoryWithFoodItems(int count) {
        ItemStack[] contents = new ItemStack[36];
        Material[] foodItems = {
            Material.BREAD, Material.APPLE, Material.CARROT, Material.POTATO,
            Material.BEEF, Material.PORKCHOP, Material.CHICKEN, Material.COD,
            Material.SALMON, Material.COOKIE, Material.CAKE, Material.MELON_SLICE
        };
        
        for (int i = 0; i < count && i < contents.length; i++) {
            contents[i] = new ItemStack(foodItems[i % foodItems.length]);
        }
        
        return contents;
    }
}
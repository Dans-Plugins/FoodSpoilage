package spoilagesystem.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import spoilagesystem.FoodSpoilage;
import spoilagesystem.timestamp.LocalTimeStampService;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Handles player join events and assigns timestamps to food items in batches to prevent 
 * PacketPlayOutRecipeUpdate from becoming too large and causing disconnections.
 * 
 * This fixes issue #208 where players would be disconnected with PacketEncoder$PacketTooLargeException
 * when the plugin tried to process all food items at once.
 */
public final class PlayerJoinListener implements Listener {

    private final LocalTimeStampService timeStampService;
    private final FoodSpoilage plugin;

    public PlayerJoinListener(LocalTimeStampService timeStampService, FoodSpoilage plugin) {
        this.timeStampService = timeStampService;
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        // Check if join processing is enabled
        if (!plugin.getConfig().getBoolean("join-processing.enabled", true)) {
            return;
        }
        
        // Get all food items that need timestamp assignment
        List<ItemStack> foodItems = Arrays.stream(event.getPlayer().getInventory().getContents())
                .filter(Objects::nonNull)
                .filter(item -> item.getType().isEdible() && item.getType() != Material.ROTTEN_FLESH)
                .filter(item -> !timeStampService.timeStampAssigned(item))
                .toList();
        
        if (foodItems.isEmpty()) {
            return;
        }
        
        plugin.getLogger().fine("Processing " + foodItems.size() + " food items for player " + 
                               event.getPlayer().getName() + " using batch processing to prevent packet overflow");
        
        // Process items in batches to prevent packet overflow
        processFoodItemsInBatches(foodItems);
    }
    
    private void processFoodItemsInBatches(List<ItemStack> foodItems) {
        // Get configurable values
        int maxItemsPerBatch = plugin.getConfig().getInt("join-processing.max-items-per-batch", 10);
        int batchDelayTicks = plugin.getConfig().getInt("join-processing.batch-delay-ticks", 5);
        
        AtomicInteger processedCount = new AtomicInteger(0);
        
        // Process first batch immediately (but limited in size)
        int initialBatchSize = Math.min(maxItemsPerBatch, foodItems.size());
        for (int i = 0; i < initialBatchSize; i++) {
            ItemStack item = foodItems.get(i);
            timeStampService.assignTimeStamp(item);
            processedCount.incrementAndGet();
        }
        
        // Schedule remaining items to be processed in delayed batches
        if (processedCount.get() < foodItems.size()) {
            scheduleNextBatch(foodItems, processedCount, maxItemsPerBatch, batchDelayTicks);
        }
    }
    
    private void scheduleNextBatch(List<ItemStack> foodItems, AtomicInteger processedCount, 
                                  int maxItemsPerBatch, int batchDelayTicks) {
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            int startIndex = processedCount.get();
            int endIndex = Math.min(startIndex + maxItemsPerBatch, foodItems.size());
            
            // Process the next batch
            for (int i = startIndex; i < endIndex; i++) {
                ItemStack item = foodItems.get(i);
                timeStampService.assignTimeStamp(item);
                processedCount.incrementAndGet();
            }
            
            // Schedule next batch if there are more items to process
            if (processedCount.get() < foodItems.size()) {
                scheduleNextBatch(foodItems, processedCount, maxItemsPerBatch, batchDelayTicks);
            }
        }, batchDelayTicks);
    }

}

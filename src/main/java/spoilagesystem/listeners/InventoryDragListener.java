package spoilagesystem.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.ItemStack;

import spoilagesystem.FoodSpoilage;
import spoilagesystem.factories.SpoiledFoodFactory;
import spoilagesystem.timestamp.LocalTimeStampService;

/**
 * @author Daniel McCoy Stephenson
 */
public final class InventoryDragListener implements Listener {

    private final FoodSpoilage plugin;
    private final LocalTimeStampService timeStampService;
    private final SpoiledFoodFactory spoiledFoodFactory;

    public InventoryDragListener(FoodSpoilage plugin, LocalTimeStampService timeStampService, SpoiledFoodFactory spoiledFoodFactory) {
        this.plugin = plugin;
        this.timeStampService = timeStampService;
        this.spoiledFoodFactory = spoiledFoodFactory;
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        ItemStack item = event.getCursor();

        if (item != null) {

            // if time stamped
            if (timeStampService.timeStampAssigned(item)) {

                plugin.getLogger().fine("Item has timestamp!");

                // if time stamp has been reached
                if (timeStampService.timeReached(item)) {

                    plugin.getLogger().fine("Time has been reached!");

                    // turn it into rotten flesh
                    event.setCursor(spoiledFoodFactory.createSpoiledFood(item.getAmount()));

                } else {
                    plugin.getLogger().fine("Time has not been reached!");
                }
            }
        }
    }
}
package spoilagesystem.eventhandlers;

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
public final class InventoryDragEventHandler implements Listener {

    private final FoodSpoilage plugin;
    private final LocalTimeStampService timeStampService;
    private final SpoiledFoodFactory spoiledFoodFactory;

    public InventoryDragEventHandler(FoodSpoilage plugin, LocalTimeStampService timeStampService, SpoiledFoodFactory spoiledFoodFactory) {
        this.plugin = plugin;
        this.timeStampService = timeStampService;
        this.spoiledFoodFactory = spoiledFoodFactory;
    }

    @EventHandler
    public void handle(InventoryDragEvent event) {
        ItemStack item = event.getCursor();

        if (item != null) {

            // if time stamped
            if (timeStampService.timeStampAssigned(item)) {

                plugin.getLogger().fine("Item has timestamp!");

                // if time stamp has been reached
                if (timeStampService.timeReached(item)) {

                    plugin.getLogger().fine("Time has been reached!");

                    // turn it into rotten flesh
                    event.setCursor(spoiledFoodFactory.createSpoiledFood(item));

                } else {
                    plugin.getLogger().fine("Time has not been reached!");
                }
            }
        }
    }
}
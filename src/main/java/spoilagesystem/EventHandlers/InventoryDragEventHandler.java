package spoilagesystem.EventHandlers;

import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.ItemStack;
import spoilagesystem.FoodSpoilage;

public class InventoryDragEventHandler {

    FoodSpoilage foodSpoilage = null;

    public InventoryDragEventHandler(FoodSpoilage plugin) {
        foodSpoilage = plugin;
    }

    public void handle(InventoryDragEvent event) {
        ItemStack item = event.getCursor();

        if (item != null) {

            // if time stamped
            if (foodSpoilage.timestamp.timeStampAssigned(item)) {

                System.out.println("Item has timestamp!");

                // if time stamp has been reached
                if (foodSpoilage.timestamp.timeReached(item)) {

                    System.out.println("Time has been reached!");

                    // turn it into rotten flesh
                    event.setCursor(foodSpoilage.utilities.createSpoiledFood(item));

                } else {
                    System.out.println("Time has not been reached!");
                }

            }

        }

    }

}

package spoilagesystem.EventHandlers;

import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.ItemStack;
import spoilagesystem.FoodSpoilage;

public class InventoryDragEventHandler {

    public void handle(InventoryDragEvent event) {
        ItemStack item = event.getCursor();

        if (item != null) {

            // if time stamped
            if (FoodSpoilage.getInstance().timestamp.timeStampAssigned(item)) {

                System.out.println("Item has timestamp!");

                // if time stamp has been reached
                if (FoodSpoilage.getInstance().timestamp.timeReached(item)) {

                    System.out.println("Time has been reached!");

                    // turn it into rotten flesh
                    event.setCursor(FoodSpoilage.getInstance().utilities.createSpoiledFood(item));

                } else {
                    System.out.println("Time has not been reached!");
                }

            }

        }

    }

}

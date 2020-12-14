package spoilagesystem.EventHandlers;

import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.ItemStack;
import spoilagesystem.FoodSpoilage;
import spoilagesystem.SpoiledFoodFactory;
import spoilagesystem.TimeStamper;

public class InventoryDragEventHandler {

    public void handle(InventoryDragEvent event) {
        ItemStack item = event.getCursor();

        if (item != null) {

            // if time stamped
            if (TimeStamper.getInstance().timeStampAssigned(item)) {

                System.out.println("Item has timestamp!");

                // if time stamp has been reached
                if (TimeStamper.getInstance().timeReached(item)) {

                    System.out.println("Time has been reached!");

                    // turn it into rotten flesh
                    event.setCursor(SpoiledFoodFactory.getInstance().createSpoiledFood(item));

                } else {
                    System.out.println("Time has not been reached!");
                }

            }

        }

    }

}

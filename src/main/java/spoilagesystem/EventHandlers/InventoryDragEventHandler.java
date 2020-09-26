package spoilagesystem.EventHandlers;

import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.ItemStack;
import spoilagesystem.Main;

public class InventoryDragEventHandler {

    Main main = null;

    public InventoryDragEventHandler(Main plugin) {
        main = plugin;
    }

    public void handle(InventoryDragEvent event) {
        ItemStack item = event.getCursor();

        if (item != null) {

            // if time stamped
            if (main.timestamp.timeStampAssigned(item)) {

                System.out.println("Item has timestamp!");

                // if time stamp has been reached
                if (main.timestamp.timeReached(item)) {

                    System.out.println("Time has been reached!");

                    // turn it into rotten flesh
                    event.setCursor(main.utilities.createSpoiledFood(item));

                } else {
                    System.out.println("Time has not been reached!");
                }

            }

        }

    }

}

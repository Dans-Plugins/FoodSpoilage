package spoilagesystem.EventHandlers;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.ItemStack;
import spoilagesystem.SpoiledFoodFactory;
import spoilagesystem.TimeStampManager;

public class InventoryDragEventHandler implements Listener {

    private boolean debug = false;

    @EventHandler()
    public void handle(InventoryDragEvent event) {
        ItemStack item = event.getCursor();

        if (item != null) {

            // if time stamped
            if (TimeStampManager.getInstance().timeStampAssigned(item)) {

                if (debug) { System.out.println("Item has timestamp!"); }

                // if time stamp has been reached
                if (TimeStampManager.getInstance().timeReached(item)) {

                    if (debug) { System.out.println("Time has been reached!"); }

                    // turn it into rotten flesh
                    event.setCursor(SpoiledFoodFactory.getInstance().createSpoiledFood(item));

                } else {
                    if (debug) { System.out.println("Time has not been reached!"); }
                }

            }

        }

    }

}

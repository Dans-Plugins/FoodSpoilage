package spoilagesystem.EventHandlers;

import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.ItemStack;
import spoilagesystem.Main;

public class InventoryDragEventHandler {

    Main main = null;

    public InventoryDragEventHandler(Main plugin) {
        main = plugin;
    }

    public void handle(InventoryDragEvent event) {

        // if time stamped
        if (main.timestamp.timeStampAssigned(event.getCursor())) {

            // if time stamp has been reached
            if (main.timestamp.timeReached(event.getCursor())) {

                // turn it into rotten flesh

                event.setCursor(new ItemStack(Material.ROTTEN_FLESH));

            }

        }

    }

}

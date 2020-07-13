package spoilagesystem.EventHandlers;

import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import spoilagesystem.Main;

public class InventoryClickEventHandler {

    Main main = null;

    public InventoryClickEventHandler(Main plugin) {
        main = plugin;
    }

    public void handle(InventoryClickEvent event) {

        // if time stamped
        if (main.timestamp.timeStampAssigned(event.getCurrentItem())) {

            // if time stamp has been reached
            if (main.timestamp.timeReached(event.getCurrentItem())) {

                // turn it into rotten flesh

                event.setCurrentItem(new ItemStack(Material.ROTTEN_FLESH));

            }

        }

    }

}

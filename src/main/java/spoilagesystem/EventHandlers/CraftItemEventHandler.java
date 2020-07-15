package spoilagesystem.EventHandlers;

import org.bukkit.Material;
import org.bukkit.event.inventory.CraftItemEvent;
import spoilagesystem.Main;

public class CraftItemEventHandler {

    Main main = null;

    public CraftItemEventHandler(Main plugin) {
        main = plugin;
    }

    public void handle(CraftItemEvent event) {

        if (event.getCurrentItem().getType() == Material.BREAD) {
            cancelIfShiftClick(event);
            event.setCurrentItem(main.timestamp.assignTimeStamp(event.getCurrentItem(), 2)); // spoils in 6 vanilla MC days
        }

        if (event.getCurrentItem().getType() == Material.POTATO) {
            cancelIfShiftClick(event);
            event.setCurrentItem(main.timestamp.assignTimeStamp(event.getCurrentItem(), 2)); // spoils in 6 vanilla MC days
        }

        if (event.getCurrentItem().getType() == Material.CARROT) {
            cancelIfShiftClick(event);
            event.setCurrentItem(main.timestamp.assignTimeStamp(event.getCurrentItem(), 4)); // spoils in 12 vanilla MC days
        }

    }

    public void cancelIfShiftClick(CraftItemEvent event) {
        if (event.isShiftClick()) {
            event.setCancelled(true); //TODO: find better solution
        }
    }

}

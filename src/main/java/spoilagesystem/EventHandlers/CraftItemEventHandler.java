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
            event.setCurrentItem(main.timestamp.assignTimeStamp(event.getCurrentItem(), 1));
        }

    }

}

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
            event.setCurrentItem(main.timestamp.assignTimeStamp(event.getCurrentItem(), main.storage.Bread));
        }

        if (event.getCurrentItem().getType() == Material.MUSHROOM_STEW) {
            cancelIfShiftClick(event);
            event.setCurrentItem(main.timestamp.assignTimeStamp(event.getCurrentItem(), main.storage.Mushroom_Stew));
        }

        if (event.getCurrentItem().getType() == Material.RABBIT_STEW) {
            cancelIfShiftClick(event);
            event.setCurrentItem(main.timestamp.assignTimeStamp(event.getCurrentItem(), main.storage.Rabbit_Stew));
        }

        if (event.getCurrentItem().getType() == Material.BEETROOT_SOUP) {
            cancelIfShiftClick(event);
            event.setCurrentItem(main.timestamp.assignTimeStamp(event.getCurrentItem(), main.storage.Beetroot_Soup));
        }

        if (event.getCurrentItem().getType() == Material.MELON_SLICE) {
            cancelIfShiftClick(event);
            event.setCurrentItem(main.timestamp.assignTimeStamp(event.getCurrentItem(), main.storage.Melon_Slice));
        }

    }

    public void cancelIfShiftClick(CraftItemEvent event) {
        if (event.isShiftClick()) {
            event.setCancelled(true); //TODO: find better solution
        }
    }

}

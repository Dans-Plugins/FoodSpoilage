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

        if (event.getCurrentItem().getType() == Material.BREAD && main.storage.Bread != 0) {
            cancelIfShiftClick(event);
            event.setCurrentItem(main.timestamp.assignTimeStamp(event.getCurrentItem(), main.storage.Bread));
        }

        if (event.getCurrentItem().getType() == Material.MUSHROOM_STEW && main.storage.Mushroom_Stew != 0) {
            cancelIfShiftClick(event);
            event.setCurrentItem(main.timestamp.assignTimeStamp(event.getCurrentItem(), main.storage.Mushroom_Stew));
        }

        if (event.getCurrentItem().getType() == Material.RABBIT_STEW && main.storage.Rabbit_Stew != 0) {
            cancelIfShiftClick(event);
            event.setCurrentItem(main.timestamp.assignTimeStamp(event.getCurrentItem(), main.storage.Rabbit_Stew));
        }

        if (event.getCurrentItem().getType() == Material.BEETROOT_SOUP && main.storage.Beetroot_Soup != 0) {
            cancelIfShiftClick(event);
            event.setCurrentItem(main.timestamp.assignTimeStamp(event.getCurrentItem(), main.storage.Beetroot_Soup));
        }

        if (event.getCurrentItem().getType() == Material.CAKE && main.storage.Cake != 0) {
            cancelIfShiftClick(event);
            event.setCurrentItem(main.timestamp.assignTimeStamp(event.getCurrentItem(), main.storage.Cake));
        }

        if (event.getCurrentItem().getType() == Material.PUMPKIN_PIE && main.storage.Pumpkin_Pie != 0) {
            cancelIfShiftClick(event);
            event.setCurrentItem(main.timestamp.assignTimeStamp(event.getCurrentItem(), main.storage.Pumpkin_Pie));
        }

        if (event.getCurrentItem().getType() == Material.SUGAR && main.storage.Sugar != 0) {
            cancelIfShiftClick(event);
            event.setCurrentItem(main.timestamp.assignTimeStamp(event.getCurrentItem(), main.storage.Sugar));
        }

        if (event.getCurrentItem().getType() == Material.COOKIE && main.storage.Cookie != 0) {
            cancelIfShiftClick(event);
            event.setCurrentItem(main.timestamp.assignTimeStamp(event.getCurrentItem(), main.storage.Cookie));
        }

    }

    public void cancelIfShiftClick(CraftItemEvent event) {
        if (event.isShiftClick()) {
            event.setCancelled(true); //TODO: find better solution
        }
    }

}

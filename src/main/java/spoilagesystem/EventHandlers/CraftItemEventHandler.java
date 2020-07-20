package spoilagesystem.EventHandlers;

import org.bukkit.Material;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.ItemStack;
import spoilagesystem.Main;

public class CraftItemEventHandler {

    Main main = null;

    public CraftItemEventHandler(Main plugin) {
        main = plugin;
    }

    public void handle(CraftItemEvent event) {

        ItemStack item = event.getCurrentItem();
        Material type = item.getType();

        if (type == Material.BREAD && main.storage.Bread != 0) {
            cancelIfShiftClick(event);
            event.setCurrentItem(main.timestamp.assignTimeStamp(item, main.storage.Bread));
        }

        if (type == Material.MUSHROOM_STEW && main.storage.Mushroom_Stew != 0) {
            cancelIfShiftClick(event);
            event.setCurrentItem(main.timestamp.assignTimeStamp(item, main.storage.Mushroom_Stew));
        }

        if (type == Material.RABBIT_STEW && main.storage.Rabbit_Stew != 0) {
            cancelIfShiftClick(event);
            event.setCurrentItem(main.timestamp.assignTimeStamp(item, main.storage.Rabbit_Stew));
        }

        if (type == Material.BEETROOT_SOUP && main.storage.Beetroot_Soup != 0) {
            cancelIfShiftClick(event);
            event.setCurrentItem(main.timestamp.assignTimeStamp(item, main.storage.Beetroot_Soup));
        }

        if (type == Material.CAKE && main.storage.Cake != 0) {
            cancelIfShiftClick(event);
            event.setCurrentItem(main.timestamp.assignTimeStamp(item, main.storage.Cake));
        }

        if (type == Material.PUMPKIN_PIE && main.storage.Pumpkin_Pie != 0) {
            cancelIfShiftClick(event);
            event.setCurrentItem(main.timestamp.assignTimeStamp(item, main.storage.Pumpkin_Pie));
        }

        if (type == Material.SUGAR && main.storage.Sugar != 0) {
            cancelIfShiftClick(event);
            event.setCurrentItem(main.timestamp.assignTimeStamp(item, main.storage.Sugar));
        }

        if (type == Material.COOKIE && main.storage.Cookie != 0) {
            cancelIfShiftClick(event);
            event.setCurrentItem(main.timestamp.assignTimeStamp(item, main.storage.Cookie));
        }

    }

    public void cancelIfShiftClick(CraftItemEvent event) {
        if (event.isShiftClick()) {
            event.setCancelled(true); //TODO: find better solution
        }
    }

}

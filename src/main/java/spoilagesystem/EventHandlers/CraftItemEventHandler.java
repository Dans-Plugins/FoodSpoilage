package spoilagesystem.EventHandlers;

import org.bukkit.Material;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.ItemStack;
import spoilagesystem.Main;

import static org.bukkit.Material.*;

public class CraftItemEventHandler {

    Main main = null;

    public CraftItemEventHandler(Main plugin) {
        main = plugin;
    }

    public void handle(CraftItemEvent event) {

        ItemStack item = event.getCurrentItem();
        Material type = item.getType();

        if (type == BREAD && main.storage.getTime(BREAD) != 0) {
            cancelIfShiftClick(event);
            event.setCurrentItem(main.timestamp.assignTimeStamp(item, main.storage.getTime(BREAD)));
        }

        if (type == Material.MUSHROOM_STEW && main.storage.getTime(MUSHROOM_STEM) != 0) {
            cancelIfShiftClick(event);
            event.setCurrentItem(main.timestamp.assignTimeStamp(item, main.storage.getTime(MUSHROOM_STEM)));
        }

        if (type == Material.RABBIT_STEW && main.storage.getTime(RABBIT_STEW) != 0) {
            cancelIfShiftClick(event);
            event.setCurrentItem(main.timestamp.assignTimeStamp(item, main.storage.getTime(RABBIT_STEW)));
        }

        if (type == Material.BEETROOT_SOUP && main.storage.getTime(BEETROOT_SOUP) != 0) {
            cancelIfShiftClick(event);
            event.setCurrentItem(main.timestamp.assignTimeStamp(item, main.storage.getTime(BEETROOT_SOUP)));
        }

        if (type == Material.CAKE && main.storage.getTime(CAKE) != 0) {
            cancelIfShiftClick(event);
            event.setCurrentItem(main.timestamp.assignTimeStamp(item, main.storage.getTime(CAKE)));
        }

        if (type == Material.PUMPKIN_PIE && main.storage.getTime(PUMPKIN_PIE) != 0) {
            cancelIfShiftClick(event);
            event.setCurrentItem(main.timestamp.assignTimeStamp(item, main.storage.getTime(PUMPKIN_PIE)));
        }

        if (type == Material.SUGAR && main.storage.getTime(SUGAR) != 0) {
            cancelIfShiftClick(event);
            event.setCurrentItem(main.timestamp.assignTimeStamp(item, main.storage.getTime(SUGAR)));
        }

        if (type == Material.COOKIE && main.storage.getTime(COOKIE) != 0) {
            cancelIfShiftClick(event);
            event.setCurrentItem(main.timestamp.assignTimeStamp(item, main.storage.getTime(COOKIE)));
        }

    }

    public void cancelIfShiftClick(CraftItemEvent event) {
        if (event.isShiftClick()) {
            event.setCancelled(true); //TODO: find better solution
        }
    }

}

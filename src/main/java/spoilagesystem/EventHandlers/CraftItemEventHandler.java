package spoilagesystem.EventHandlers;

import org.bukkit.Material;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.ItemStack;
import spoilagesystem.FoodSpoilage;
import spoilagesystem.StorageManager;
import spoilagesystem.TimeStamper;

public class CraftItemEventHandler {

    public void handle(CraftItemEvent event) {

        ItemStack item = event.getCurrentItem();
        Material type = item.getType();
        int time = StorageManager.getInstance().getTime(type);

        if (time != 0) {
            cancelIfShiftClick(event);
            event.setCurrentItem(TimeStamper.getInstance().assignTimeStamp(item, time));
        }
    }

    public void cancelIfShiftClick(CraftItemEvent event) {
        if (event.isShiftClick()) {
            event.setCancelled(true); //TODO: find better solution
        }
    }

}

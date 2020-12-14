package spoilagesystem.EventHandlers;

import org.bukkit.Material;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.ItemStack;
import spoilagesystem.FoodSpoilage;

public class CraftItemEventHandler {

    public void handle(CraftItemEvent event) {

        ItemStack item = event.getCurrentItem();
        Material type = item.getType();
        int time = FoodSpoilage.getInstance().storage.getTime(type);

        if (time != 0) {
            cancelIfShiftClick(event);
            event.setCurrentItem(FoodSpoilage.getInstance().timestamp.assignTimeStamp(item, time));
        }
    }

    public void cancelIfShiftClick(CraftItemEvent event) {
        if (event.isShiftClick()) {
            event.setCancelled(true); //TODO: find better solution
        }
    }

}

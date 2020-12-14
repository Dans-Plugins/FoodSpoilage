package spoilagesystem.EventHandlers;

import org.bukkit.Material;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.inventory.ItemStack;
import spoilagesystem.FoodSpoilage;

public class ItemSpawnEventHandler {

    public void handle(ItemSpawnEvent event) {

        ItemStack item = event.getEntity().getItemStack();
        Material type = item.getType();
        int time = FoodSpoilage.getInstance().storage.getTime(type);

        // if timestamp not already assigned
        if (time != 0 && !FoodSpoilage.getInstance().timestamp.timeStampAssigned(item)) {
            event.getEntity().setItemStack(FoodSpoilage.getInstance().timestamp.assignTimeStamp(item, time));
        }

    }

}

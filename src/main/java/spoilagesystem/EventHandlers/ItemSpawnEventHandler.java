package spoilagesystem.EventHandlers;

import org.bukkit.Material;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.inventory.ItemStack;
import spoilagesystem.FoodSpoilage;

public class ItemSpawnEventHandler {

    FoodSpoilage foodSpoilage = null;

    public ItemSpawnEventHandler(FoodSpoilage plugin) {
        foodSpoilage = plugin;
    }

    public void handle(ItemSpawnEvent event) {

        ItemStack item = event.getEntity().getItemStack();
        Material type = item.getType();
        int time = foodSpoilage.storage.getTime(type);

        // if timestamp not already assigned
        if (time != 0 && !foodSpoilage.timestamp.timeStampAssigned(item)) {
            event.getEntity().setItemStack(foodSpoilage.timestamp.assignTimeStamp(item, time));
        }

    }

}

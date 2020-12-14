package spoilagesystem.EventHandlers;

import org.bukkit.Material;
import org.bukkit.event.inventory.FurnaceSmeltEvent;
import org.bukkit.inventory.ItemStack;
import spoilagesystem.FoodSpoilage;

public class FurnaceSmeltEventHandler {

    FoodSpoilage foodSpoilage = null;

    public FurnaceSmeltEventHandler(FoodSpoilage plugin) {
        foodSpoilage = plugin;
    }

    public void handle(FurnaceSmeltEvent event) {

        ItemStack item = event.getResult();
        Material type = item.getType();
        int time = foodSpoilage.storage.getTime(type);

        if (time != 0) {
            event.setResult(foodSpoilage.timestamp.assignTimeStamp(item, time));
        }

    }

}

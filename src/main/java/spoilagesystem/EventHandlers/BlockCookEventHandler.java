package spoilagesystem.EventHandlers;

import org.bukkit.Material;
import org.bukkit.event.block.BlockCookEvent;
import org.bukkit.inventory.ItemStack;
import spoilagesystem.FoodSpoilage;

public class BlockCookEventHandler {

    FoodSpoilage foodSpoilage = null;

    public BlockCookEventHandler(FoodSpoilage plugin) {
        foodSpoilage = plugin;
    }

    public void handle(BlockCookEvent event) {

        ItemStack item = event.getResult();
        Material type = item.getType();
        int time = foodSpoilage.storage.getTime(type);

        if (time != 0) {
            event.setResult(foodSpoilage.timestamp.assignTimeStamp(item, time));
        }

    }

}

package spoilagesystem.EventHandlers;

import org.bukkit.Material;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.inventory.ItemStack;
import spoilagesystem.FoodSpoilage;
import spoilagesystem.TimeStamper;

public class ItemSpawnEventHandler {

    public void handle(ItemSpawnEvent event) {

        ItemStack item = event.getEntity().getItemStack();
        Material type = item.getType();
        int time = FoodSpoilage.getInstance().storage.getTime(type);

        // if timestamp not already assigned
        if (time != 0 && !TimeStamper.getInstance().timeStampAssigned(item)) {
            event.getEntity().setItemStack(TimeStamper.getInstance().assignTimeStamp(item, time));
        }

    }

}

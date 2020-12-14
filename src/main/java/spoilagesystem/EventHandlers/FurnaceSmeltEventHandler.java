package spoilagesystem.EventHandlers;

import org.bukkit.Material;
import org.bukkit.event.inventory.FurnaceSmeltEvent;
import org.bukkit.inventory.ItemStack;
import spoilagesystem.FoodSpoilage;
import spoilagesystem.TimeStamper;

public class FurnaceSmeltEventHandler {

    public void handle(FurnaceSmeltEvent event) {

        ItemStack item = event.getResult();
        Material type = item.getType();
        int time = FoodSpoilage.getInstance().storage.getTime(type);

        if (time != 0) {
            event.setResult(TimeStamper.getInstance().assignTimeStamp(item, time));
        }

    }

}

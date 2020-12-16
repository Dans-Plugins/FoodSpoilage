package spoilagesystem.EventHandlers;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.FurnaceSmeltEvent;
import org.bukkit.inventory.ItemStack;
import spoilagesystem.FoodSpoilage;
import spoilagesystem.StorageManager;
import spoilagesystem.TimeStamper;

public class FurnaceSmeltEventHandler implements Listener {

    @EventHandler()
    public void handle(FurnaceSmeltEvent event) {

        ItemStack item = event.getResult();
        Material type = item.getType();
        int time = StorageManager.getInstance().getTime(type);

        if (time != 0) {
            event.setResult(TimeStamper.getInstance().assignTimeStamp(item, time));
        }

    }

}

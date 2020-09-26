package spoilagesystem.EventHandlers;

import org.bukkit.Material;
import org.bukkit.event.inventory.FurnaceSmeltEvent;
import org.bukkit.inventory.ItemStack;
import spoilagesystem.Main;

public class FurnaceSmeltEventHandler {

    Main main = null;

    public FurnaceSmeltEventHandler(Main plugin) {
        main = plugin;
    }

    public void handle(FurnaceSmeltEvent event) {

        ItemStack item = event.getResult();
        Material type = item.getType();
        int time = main.storage.getTime(type);

        if (time != 0) {
            event.setResult(main.timestamp.assignTimeStamp(item, time));
        }

    }

}

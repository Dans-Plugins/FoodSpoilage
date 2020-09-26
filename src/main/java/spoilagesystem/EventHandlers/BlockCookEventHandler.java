package spoilagesystem.EventHandlers;

import org.bukkit.Material;
import org.bukkit.event.block.BlockCookEvent;
import org.bukkit.inventory.ItemStack;
import spoilagesystem.Main;

public class BlockCookEventHandler {

    Main main = null;

    public BlockCookEventHandler(Main plugin) {
        main = plugin;
    }

    public void handle(BlockCookEvent event) {

        ItemStack item = event.getResult();
        Material type = item.getType();
        int time = main.storage.getTime(type);

        if (time != 0) {
            event.setResult(main.timestamp.assignTimeStamp(item, time));
        }

    }

}

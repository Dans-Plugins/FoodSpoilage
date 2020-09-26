package spoilagesystem.EventHandlers;

import org.bukkit.Material;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import spoilagesystem.Main;

import static org.bukkit.Material.*;
import static org.bukkit.Material.DRIED_KELP;

public class ItemSpawnEventHandler {

    Main main = null;

    public ItemSpawnEventHandler(Main plugin) {
        main = plugin;
    }

    public void handle(ItemSpawnEvent event) {

        ItemStack item = event.getEntity().getItemStack();
        Material type = item.getType();
        int time = main.storage.getTime(type);

        // if timestamp not already assigned
        if (time != 0 && !main.timestamp.timeStampAssigned(item)) {
            event.getEntity().setItemStack(main.timestamp.assignTimeStamp(item, time));
        }

    }

}

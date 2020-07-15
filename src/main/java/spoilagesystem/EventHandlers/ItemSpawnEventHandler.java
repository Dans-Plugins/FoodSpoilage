package spoilagesystem.EventHandlers;

import org.bukkit.Material;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import spoilagesystem.Main;

public class ItemSpawnEventHandler {

    Main main = null;

    public ItemSpawnEventHandler(Main plugin) {
        main = plugin;
    }

    public void handle(ItemSpawnEvent event) {
        if (event.getEntity().getItemStack().getType() == Material.POTATO) {
            event.getEntity().setItemStack(main.timestamp.assignTimeStamp(event.getEntity().getItemStack(), 2)); // spoils in 6 vanilla MC days
        }

        if (event.getEntity().getItemStack().getType() == Material.CARROT) {
            event.getEntity().setItemStack(main.timestamp.assignTimeStamp(event.getEntity().getItemStack(), 4)); // spoils in 12 vanilla MC days
        }

        if (event.getEntity().getItemStack().getType() == Material.BEETROOT) {
            event.getEntity().setItemStack(main.timestamp.assignTimeStamp(event.getEntity().getItemStack(), 5)); // spoils in 15 vanilla MC days
        }

        if (event.getEntity().getItemStack().getType() == Material.BEEF) {
            event.getEntity().setItemStack(main.timestamp.assignTimeStamp(event.getEntity().getItemStack(), 5)); // spoils in 15 vanilla MC days
        }
    }

}

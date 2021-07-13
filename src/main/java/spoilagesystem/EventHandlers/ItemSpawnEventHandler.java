package spoilagesystem.EventHandlers;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.inventory.ItemStack;
import spoilagesystem.ConfigManager;
import spoilagesystem.TimeStampManager;

public class ItemSpawnEventHandler implements Listener {

    @EventHandler()
    public void handle(ItemSpawnEvent event) {

        ItemStack item = event.getEntity().getItemStack();
        Material type = item.getType();
        int time = ConfigManager.getInstance().getTime(type);

        // if timestamp not already assigned
        if (time != 0 && !TimeStampManager.getInstance().timeStampAssigned(item)) {
            event.getEntity().setItemStack(TimeStampManager.getInstance().assignTimeStamp(item, time));
        }

    }

}

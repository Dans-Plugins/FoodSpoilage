package spoilagesystem.EventHandlers;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.inventory.ItemStack;

import spoilagesystem.services.LocalConfigService;
import spoilagesystem.services.LocalTimeStampService;

public class ItemSpawnEventHandler implements Listener {

    @EventHandler()
    public void handle(ItemSpawnEvent event) {

        ItemStack item = event.getEntity().getItemStack();
        Material type = item.getType();
        int time = LocalConfigService.getInstance().getTime(type);

        // if timestamp not already assigned
        if (time != 0 && !LocalTimeStampService.getInstance().timeStampAssigned(item)) {
            event.getEntity().setItemStack(LocalTimeStampService.getInstance().assignTimeStamp(item, time));
        }

    }

}

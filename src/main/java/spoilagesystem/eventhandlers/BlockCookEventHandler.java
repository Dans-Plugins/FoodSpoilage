package spoilagesystem.eventhandlers;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockCookEvent;
import org.bukkit.inventory.ItemStack;

import spoilagesystem.services.LocalConfigService;
import spoilagesystem.services.LocalTimeStampService;

/**
 * @author Daniel McCoy Stephenson
 */
public class BlockCookEventHandler implements Listener {

    @EventHandler()
    public void handle(BlockCookEvent event) {
        ItemStack item = event.getResult();
        Material type = item.getType();
        int time = LocalConfigService.getInstance().getTime(type);

        if (time != 0) {
            event.setResult(LocalTimeStampService.getInstance().assignTimeStamp(item, time));
        }
    }
}
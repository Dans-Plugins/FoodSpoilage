package spoilagesystem.EventHandlers;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.FurnaceSmeltEvent;
import org.bukkit.inventory.ItemStack;

import spoilagesystem.services.LocalConfigService;
import spoilagesystem.services.LocalTimeStampService;

public class FurnaceSmeltEventHandler implements Listener {

    @EventHandler()
    public void handle(FurnaceSmeltEvent event) {

        ItemStack item = event.getResult();
        Material type = item.getType();
        int time = LocalConfigService.getInstance().getTime(type);

        if (time != 0) {
            event.setResult(LocalTimeStampService.getInstance().assignTimeStamp(item, time));
        }

    }

}

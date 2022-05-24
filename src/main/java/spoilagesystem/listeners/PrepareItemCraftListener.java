package spoilagesystem.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;
import spoilagesystem.timestamp.LocalTimeStampService;

public final class PrepareItemCraftListener implements Listener {

    private final LocalTimeStampService timeStampService;

    public PrepareItemCraftListener(LocalTimeStampService timeStampService) {
        this.timeStampService = timeStampService;
    }

    @EventHandler
    public void onPrepareItemCraft(PrepareItemCraftEvent event) {
        ItemStack item = event.getInventory().getResult();
        if (item != null) {
            if (item.getType().isEdible()) {
                event.getInventory().setResult(timeStampService.assignTimeStamp(item));
            }
        }
    }

}

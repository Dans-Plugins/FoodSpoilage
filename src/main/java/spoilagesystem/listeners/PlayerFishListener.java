package spoilagesystem.listeners;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;
import spoilagesystem.timestamp.LocalTimeStampService;

public final class PlayerFishListener implements Listener {

    private final LocalTimeStampService timeStampService;

    public PlayerFishListener(LocalTimeStampService timeStampService) {
        this.timeStampService = timeStampService;
    }

    @EventHandler
    public void onPlayerFish(PlayerFishEvent event) {
        Entity caught = event.getCaught();
        if (caught != null) {
            if (caught instanceof Item caughtItem) {
                ItemStack item = caughtItem.getItemStack();
                caughtItem.setItemStack(timeStampService.assignTimeStamp(item));
            }
        }
    }

}

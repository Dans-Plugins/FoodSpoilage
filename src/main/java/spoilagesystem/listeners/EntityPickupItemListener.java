package spoilagesystem.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import spoilagesystem.timestamp.LocalTimeStampService;

public final class EntityPickupItemListener implements Listener {

    private final LocalTimeStampService timeStampService;

    public EntityPickupItemListener(LocalTimeStampService timeStampService) {
        this.timeStampService = timeStampService;
    }

    @EventHandler
    public void onEntityPickupItem(EntityPickupItemEvent event) {
        ItemStack itemStack = event.getItem().getItemStack();
        if (itemStack.getType().isEdible()) {
            if (!timeStampService.timeStampAssigned(itemStack)) {
                event.getItem().setItemStack(timeStampService.assignTimeStamp(itemStack));
            }
        }
    }

}

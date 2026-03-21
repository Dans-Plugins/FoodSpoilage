package spoilagesystem.listeners;

import org.bukkit.Material;
import org.bukkit.entity.Player;
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
        if (event.getEntity() instanceof Player player && player.hasPermission("fs.bypass")) {
            return;
        }

        ItemStack itemStack = event.getItem().getItemStack();
        if (itemStack.getType().isEdible() && itemStack.getType() != Material.ROTTEN_FLESH) {
            if (!timeStampService.timeStampAssigned(itemStack)) {
                event.getItem().setItemStack(timeStampService.assignTimeStamp(itemStack));
            }
        }
    }

}

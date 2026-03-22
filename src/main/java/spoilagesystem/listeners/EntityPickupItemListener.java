package spoilagesystem.listeners;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import spoilagesystem.config.LocalConfigService;
import spoilagesystem.timestamp.LocalTimeStampService;

public final class EntityPickupItemListener implements Listener {

    private final LocalConfigService configService;
    private final LocalTimeStampService timeStampService;

    public EntityPickupItemListener(LocalConfigService configService, LocalTimeStampService timeStampService) {
        this.configService = configService;
        this.timeStampService = timeStampService;
    }

    @EventHandler
    public void onEntityPickupItem(EntityPickupItemEvent event) {
        if (configService.isBypassPermissionsEnabled() && event.getEntity() instanceof Player player && player.hasPermission("fs.bypass.timestamp")) {
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

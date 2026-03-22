package spoilagesystem.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.ItemStack;

import spoilagesystem.FoodSpoilage;
import spoilagesystem.config.LocalConfigService;
import spoilagesystem.factories.SpoiledFoodFactory;
import spoilagesystem.timestamp.LocalTimeStampService;

/**
 * @author Daniel McCoy Stephenson
 */
public final class InventoryDragListener implements Listener {

    private final FoodSpoilage plugin;
    private final LocalConfigService configService;
    private final LocalTimeStampService timeStampService;
    private final SpoiledFoodFactory spoiledFoodFactory;

    public InventoryDragListener(FoodSpoilage plugin, LocalConfigService configService, LocalTimeStampService timeStampService, SpoiledFoodFactory spoiledFoodFactory) {
        this.plugin = plugin;
        this.configService = configService;
        this.timeStampService = timeStampService;
        this.spoiledFoodFactory = spoiledFoodFactory;
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        if (configService.isBypassPermissionsEnabled() && event.getWhoClicked() instanceof Player player && player.hasPermission("fs.bypass.spoilage")) {
            return;
        }

        ItemStack item = event.getCursor();

        if (item != null) {

            // if time stamped
            if (timeStampService.timeStampAssigned(item)) {

                plugin.getLogger().fine("Item has timestamp!");

                // if time stamp has been reached
                if (timeStampService.timeReached(item)) {

                    plugin.getLogger().fine("Time has been reached!");

                    // turn it into rotten flesh
                    event.setCursor(spoiledFoodFactory.createSpoiledFood(item.getAmount()));

                } else {
                    plugin.getLogger().fine("Time has not been reached!");
                }
            }
        }
    }
}
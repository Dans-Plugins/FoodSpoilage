package spoilagesystem.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import spoilagesystem.FoodSpoilage;
import spoilagesystem.factories.SpoiledFoodFactory;
import spoilagesystem.timestamp.LocalTimeStampService;

/**
 * @author Daniel McCoy Stephenson
 */
public final class PlayerInteractListener implements Listener {

    private final FoodSpoilage plugin;
    private final LocalTimeStampService timeStampService;
    private final SpoiledFoodFactory spoiledFoodFactory;

    public PlayerInteractListener(FoodSpoilage plugin, LocalTimeStampService timeStampService, SpoiledFoodFactory spoiledFoodFactory) {
        this.plugin = plugin;
        this.timeStampService = timeStampService;
        this.spoiledFoodFactory = spoiledFoodFactory;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        ItemStack item = event.getItem();

        if (item != null) {

            // if time stamped
            if (timeStampService.timeStampAssigned(item)) {

                plugin.getLogger().fine("Item has timestamp!");

                EquipmentSlot hand = event.getHand();
                if (hand != null) {

                    // if time stamp has been reached
                    if (timeStampService.timeReached(item)) {

                        // turn it into rotten flesh
                        ItemStack spoiledFood = spoiledFoodFactory.createSpoiledFood(item.getAmount());

                        switch (hand) {
                            case HAND -> event.getPlayer().getInventory().setItemInMainHand(spoiledFood);
                            case OFF_HAND -> event.getPlayer().getInventory().setItemInOffHand(spoiledFood);
                            default -> plugin.getLogger().fine("Unknown Hand " + hand);
                        }
                        event.setCancelled(true);
                    } else {
                        plugin.getLogger().fine("Time has not been reached!");
                    }
                } else {
                    event.setCancelled(true);
                }
            }
        }
    }
}
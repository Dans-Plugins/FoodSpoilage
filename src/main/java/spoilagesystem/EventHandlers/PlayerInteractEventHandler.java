package spoilagesystem.EventHandlers;

import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import spoilagesystem.FoodSpoilage;

public class PlayerInteractEventHandler {

    FoodSpoilage foodSpoilage = null;

    public PlayerInteractEventHandler(FoodSpoilage plugin) {
        foodSpoilage = plugin;
    }

    public void handle(PlayerInteractEvent event) {
        ItemStack item = event.getItem();

        if (item != null) {

            // if time stamped
            if (foodSpoilage.timestamp.timeStampAssigned(item)) {

                System.out.println("Item has timestamp!");

                EquipmentSlot hand = event.getHand();
                if (hand != null) {

                    // if time stamp has been reached
                    if (foodSpoilage.timestamp.timeReached(item)) {

                        // turn it into rotten flesh
                        ItemStack spoiledFood = foodSpoilage.utilities.createSpoiledFood(item);

                        switch(hand) {
                            case HAND:
                                event.getPlayer().getInventory().setItemInMainHand(spoiledFood);
                                break;
                            case OFF_HAND:
                                event.getPlayer().getInventory().setItemInOffHand(spoiledFood);
                                break;
                            default:
                                System.out.println("Unknown Hand" + hand);
                        }
                        event.setCancelled(true);
                    } else {
                        System.out.println("Time has not been reached!");
                    }
                }
                else {
                    event.setCancelled(true);
                }
            }

        }

    }

}

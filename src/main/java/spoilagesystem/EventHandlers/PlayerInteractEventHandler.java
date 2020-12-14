package spoilagesystem.EventHandlers;

import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import spoilagesystem.FoodSpoilage;
import spoilagesystem.SpoiledFoodFactory;
import spoilagesystem.TimeStamper;

public class PlayerInteractEventHandler {

    public void handle(PlayerInteractEvent event) {
        ItemStack item = event.getItem();

        if (item != null) {

            // if time stamped
            if (TimeStamper.getInstance().timeStampAssigned(item)) {

                System.out.println("Item has timestamp!");

                EquipmentSlot hand = event.getHand();
                if (hand != null) {

                    // if time stamp has been reached
                    if (TimeStamper.getInstance().timeReached(item)) {

                        // turn it into rotten flesh
                        ItemStack spoiledFood = SpoiledFoodFactory.getInstance().createSpoiledFood(item);

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

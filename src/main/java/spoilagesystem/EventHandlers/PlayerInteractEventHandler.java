package spoilagesystem.EventHandlers;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import spoilagesystem.factories.SpoiledFoodFactory;
import spoilagesystem.services.LocalTimeStampService;

public class PlayerInteractEventHandler implements Listener {

    private boolean debug = false;

    @EventHandler()
    public void handle(PlayerInteractEvent event) {
        ItemStack item = event.getItem();

        if (item != null) {

            // if time stamped
            if (LocalTimeStampService.getInstance().timeStampAssigned(item)) {

                if (debug) { System.out.println("Item has timestamp!"); }

                EquipmentSlot hand = event.getHand();
                if (hand != null) {

                    // if time stamp has been reached
                    if (LocalTimeStampService.getInstance().timeReached(item)) {

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
                                if (debug) { System.out.println("Unknown Hand" + hand); }
                        }
                        event.setCancelled(true);
                    } else {
                        if (debug) { System.out.println("Time has not been reached!"); }
                    }
                }
                else {
                    event.setCancelled(true);
                }
            }

        }

    }

}

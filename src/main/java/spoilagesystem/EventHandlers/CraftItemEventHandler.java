package spoilagesystem.eventhandlers;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.ItemStack;

import spoilagesystem.factories.SpoiledFoodFactory;
import spoilagesystem.services.LocalConfigService;
import spoilagesystem.services.LocalTimeStampService;

/**
 * @author Daniel McCoy Stephenson
 */
public class CraftItemEventHandler implements Listener {

    @EventHandler()
    public void handle(CraftItemEvent event) {

        ItemStack item = event.getCurrentItem();
        Material type = item.getType();
        int time = LocalConfigService.getInstance().getTime(type);
        if (time != 0) {
            cancelIfShiftClick(event);
            int spoilAmt = LocalConfigService.getInstance().getSpoilChance(type, item.getAmount());
            if (spoilAmt > 0) {
                item.setAmount(item.getAmount() - spoilAmt);
                ItemStack spoiled = item.clone();
                spoiled.setAmount(spoilAmt);
                ItemStack spoiledFood = SpoiledFoodFactory.getInstance().createSpoiledFood(spoiled);
                event.getWhoClicked().getInventory().addItem(spoiledFood);
            }
            event.setCurrentItem(LocalTimeStampService.getInstance().assignTimeStamp(item, time));
        }
    }

    private void cancelIfShiftClick(CraftItemEvent event) {
        if (event.isShiftClick()) {
            event.setCancelled(true); //TODO: find better solution
        }
    }
}
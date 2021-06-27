package spoilagesystem.EventHandlers;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import spoilagesystem.FoodSpoilage;
import spoilagesystem.SpoiledFoodFactory;
import spoilagesystem.StorageManager;
import spoilagesystem.TimeStamper;

public class CraftItemEventHandler implements Listener {

    @EventHandler()
    public void handle(CraftItemEvent event) {

        ItemStack item = event.getCurrentItem();
        Material type = item.getType();
        int time = StorageManager.getInstance().getTime(type);
        if (time != 0) {
            cancelIfShiftClick(event);
            int spoilAmt = StorageManager.getInstance().getSpoilAmt(type, item.getAmount());
            if (spoilAmt > 0) {
                item.setAmount(item.getAmount() - spoilAmt);
                ItemStack spoiled = item.clone();
                spoiled.setAmount(spoilAmt);
                ItemStack spoiledFood = SpoiledFoodFactory.getInstance().createSpoiledFood(spoiled);
                event.getInventory().addItem(spoiledFood);
            }
            event.setCurrentItem(TimeStamper.getInstance().assignTimeStamp(item, time));
        }
    }

    private void cancelIfShiftClick(CraftItemEvent event) {
        if (event.isShiftClick()) {
            event.setCancelled(true); //TODO: find better solution
        }
    }

}

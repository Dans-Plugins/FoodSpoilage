package spoilagesystem.EventHandlers;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import spoilagesystem.ConfigManager;
import spoilagesystem.SpoiledFoodFactory;
import spoilagesystem.TimeStampManager;

public class VillagerInventoryClickEvent implements Listener {

    @EventHandler()
    public void handle(InventoryClickEvent event) {
        // If the slot clicked was a 'result' type slot we need to check
        // if it's a villager inventory.
        if (event.getSlotType().equals(InventoryType.SlotType.RESULT)) {
            Entity entity = (Entity) event.getClickedInventory().getHolder();
            if (entity.getType().equals(EntityType.VILLAGER)) {
                ItemStack item = event.getCurrentItem();
                Material type = item.getType();
                int time = ConfigManager.getInstance().getTime(type);
                if (time != 0) {
                    event.setCurrentItem(TimeStampManager.getInstance().assignTimeStamp(item, time));
                }
            }
        }
    }
}

package spoilagesystem.EventHandlers;

import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import spoilagesystem.Main;

import java.util.ArrayList;
import java.util.List;

public class InventoryDragEventHandler {

    Main main = null;

    public InventoryDragEventHandler(Main plugin) {
        main = plugin;
    }

    public void handle(InventoryDragEvent event) {

        // if time stamped
        if (main.timestamp.timeStampAssigned(event.getCursor())) {

            System.out.println("Item has timestamp!");

            // if time stamp has been reached
            if (main.timestamp.timeReached(event.getCursor())) {

                System.out.println("Time has been reached!");

                // turn it into rotten flesh
                ItemStack spoiledFood = new ItemStack(Material.ROTTEN_FLESH);
                ItemMeta meta = spoiledFood.getItemMeta();
                meta.setDisplayName("Spoiled Food");
                List<String> lore = new ArrayList<>();
                lore.add("This food has gone bad.");
                meta.setLore(lore);
                spoiledFood.setItemMeta(meta);
                event.setCursor(spoiledFood);

            }
            else {
                System.out.println("Time has not been reached!");
            }

        }

    }

}

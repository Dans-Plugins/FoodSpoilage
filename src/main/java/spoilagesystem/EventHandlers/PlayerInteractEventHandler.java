package spoilagesystem.EventHandlers;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import spoilagesystem.Main;

import java.util.ArrayList;
import java.util.List;

public class PlayerInteractEventHandler {

    Main main = null;

    public PlayerInteractEventHandler(Main plugin) {
        main = plugin;
    }

    public void handle(PlayerInteractEvent event) {
        if (event.getItem() != null) {
            if (main.timestamp.timeStampAssigned(event.getItem())) {
                if (event.getHand() != EquipmentSlot.OFF_HAND) {
                    if (main.timestamp.timeReached(event.getItem())) {

                        // turn it into rotten flesh
                        ItemStack spoiledFood = new ItemStack(Material.ROTTEN_FLESH);
                        ItemMeta meta = spoiledFood.getItemMeta();
                        meta.setDisplayName("Spoiled Food");
                        List<String> lore = new ArrayList<>();
                        lore.add(ChatColor.WHITE + "This food has gone bad.");
                        meta.setLore(lore);
                        spoiledFood.setItemMeta(meta);
                        event.getPlayer().getInventory().setItemInMainHand(spoiledFood);
                        event.setCancelled(true);
                    }
                }
                else {
                    event.setCancelled(true);
                }
            }
        }
    }

}

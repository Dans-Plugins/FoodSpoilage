package spoilagesystem;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collections;

public class Utilities {

    FoodSpoilage foodSpoilage = null;

    public Utilities(FoodSpoilage plugin) {
        foodSpoilage = plugin;
    }

    public ItemStack createSpoiledFood(ItemStack item) {
        ItemStack spoiledFood = new ItemStack(Material.ROTTEN_FLESH);

        ItemMeta meta = spoiledFood.getItemMeta();

        if (meta != null) {
            meta.setDisplayName(foodSpoilage.storage.spoiledFoodName);
            meta.setLore(Collections.singletonList(ChatColor.WHITE + foodSpoilage.storage.spoiledFoodLore));
        }

        spoiledFood.setItemMeta(meta);
        spoiledFood.setAmount(item.getAmount());

        return spoiledFood;
    }
}

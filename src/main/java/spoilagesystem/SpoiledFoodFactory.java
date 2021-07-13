package spoilagesystem;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collections;

public class SpoiledFoodFactory {

    private static SpoiledFoodFactory instance;

    private SpoiledFoodFactory() {

    }

    public static SpoiledFoodFactory getInstance() {
        if (instance == null) {
            instance = new SpoiledFoodFactory();
        }
        return instance;
    }

    public ItemStack createSpoiledFood(ItemStack item) {
        ItemStack spoiledFood = new ItemStack(Material.ROTTEN_FLESH);

        ItemMeta meta = spoiledFood.getItemMeta();

        if (meta != null) {
            meta.setDisplayName(ConfigManager.getInstance().spoiledFoodName);
            meta.setLore(Collections.singletonList(ChatColor.WHITE + ConfigManager.getInstance().spoiledFoodLore));
        }

        spoiledFood.setItemMeta(meta);
        spoiledFood.setAmount(item.getAmount());

        return spoiledFood;
    }
}

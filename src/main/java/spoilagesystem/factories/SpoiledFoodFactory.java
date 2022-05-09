package spoilagesystem.factories;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import spoilagesystem.config.LocalConfigService;

import java.util.Collections;

/**
 * @author Daniel McCoy Stephenson
 */
public final class SpoiledFoodFactory {

    private final LocalConfigService configService;

    public SpoiledFoodFactory(LocalConfigService configService) {
        this.configService = configService;
    }

    public ItemStack createSpoiledFood(ItemStack item) {
        ItemStack spoiledFood = new ItemStack(Material.ROTTEN_FLESH);

        ItemMeta meta = spoiledFood.getItemMeta();

        if (meta != null) {
            meta.setDisplayName(configService.getSpoiledFoodName());
            meta.setLore(Collections.singletonList(ChatColor.WHITE + configService.getSpoiledFoodLore()));
        }

        spoiledFood.setItemMeta(meta);
        spoiledFood.setAmount(item.getAmount());

        return spoiledFood;
    }
}
package spoilagesystem.listeners;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapelessRecipe;
import spoilagesystem.config.LocalConfigService;
import spoilagesystem.timestamp.LocalTimeStampService;

import static org.bukkit.Material.AIR;

/**
 * Listener that handles the waxing crafting recipe.
 * <p>
 * When a player combines a wax material (default: honeycomb) with an edible food item in a
 * crafting grid, the result is a "waxed" version of that food. Waxed food will never spoil
 * but cannot be eaten, making it suitable for preserving sentimental lore items.
 */
public final class WaxingCraftListener implements Listener {

    private final LocalConfigService configService;
    private final LocalTimeStampService timeStampService;
    private final Material waxMaterial;
    private final NamespacedKey waxingKey;

    public WaxingCraftListener(LocalConfigService configService, LocalTimeStampService timeStampService, Material waxMaterial, NamespacedKey waxingKey) {
        this.configService = configService;
        this.timeStampService = timeStampService;
        this.waxMaterial = waxMaterial;
        this.waxingKey = waxingKey;
    }

    @EventHandler
    public void onPrepareItemCraft(PrepareItemCraftEvent event) {
        if (!configService.isWaxingEnabled()) return;
        if (!(event.getRecipe() instanceof ShapelessRecipe shapeless) || !shapeless.getKey().equals(waxingKey)) return;

        CraftingInventory inv = event.getInventory();
        ItemStack[] matrix = inv.getMatrix();

        ItemStack foodItem = null;
        for (ItemStack item : matrix) {
            if (item == null || item.getType() == AIR) continue;
            if (item.getType() != waxMaterial && item.getType().isEdible() && item.getType() != Material.ROTTEN_FLESH) {
                foodItem = item;
                break;
            }
        }

        if (foodItem == null || timeStampService.isWaxed(foodItem)) {
            inv.setResult(null);
            return;
        }

        ItemStack result = foodItem.clone();
        result.setAmount(1);
        timeStampService.applyWax(result);
        inv.setResult(result);
    }

    @EventHandler
    public void onCraftItem(CraftItemEvent event) {
        if (!configService.isWaxingEnabled()) return;
        if (!(event.getRecipe() instanceof ShapelessRecipe shapeless) || !shapeless.getKey().equals(waxingKey)) return;

        ItemStack result = event.getCurrentItem();
        if (result == null || !timeStampService.isWaxed(result)) return;

        if (event.isShiftClick()) {
            event.setCancelled(true);

            CraftingInventory inv = event.getInventory();
            ItemStack[] matrix = inv.getMatrix();

            // Calculate max crafts based on the smallest ingredient stack
            int maxCrafts = Integer.MAX_VALUE;
            for (ItemStack item : matrix) {
                if (item != null && item.getType() != AIR) {
                    maxCrafts = Math.min(maxCrafts, item.getAmount());
                }
            }
            if (maxCrafts == Integer.MAX_VALUE || maxCrafts <= 0) return;

            // Limit by available inventory space
            var crafter = event.getWhoClicked();
            int spacesFree = 0;
            for (ItemStack invItem : crafter.getInventory().getStorageContents()) {
                if (invItem == null || invItem.getType() == AIR) {
                    spacesFree += result.getType().getMaxStackSize();
                } else if (invItem.isSimilar(result)) {
                    spacesFree += invItem.getType().getMaxStackSize() - invItem.getAmount();
                }
            }
            maxCrafts = Math.min(maxCrafts, spacesFree);
            if (maxCrafts <= 0) return;

            // Consume ingredients
            ItemStack[] newMatrix = new ItemStack[matrix.length];
            for (int i = 0; i < matrix.length; i++) {
                if (matrix[i] == null || matrix[i].getType() == AIR) {
                    newMatrix[i] = null;
                    continue;
                }
                ItemStack matrixItem = matrix[i].clone();
                matrixItem.setAmount(matrixItem.getAmount() - maxCrafts);
                newMatrix[i] = matrixItem.getAmount() <= 0 ? null : matrixItem;
            }
            inv.setMatrix(newMatrix);

            // Give crafted items to player
            ItemStack craftedResult = result.clone();
            craftedResult.setAmount(maxCrafts);
            crafter.getInventory().addItem(craftedResult)
                    .values().forEach(overflow -> crafter.getWorld()
                            .dropItem(crafter.getLocation(), overflow));
        }
    }
}

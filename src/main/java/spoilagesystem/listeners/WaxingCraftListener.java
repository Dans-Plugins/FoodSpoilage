package spoilagesystem.listeners;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;
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

    public WaxingCraftListener(LocalConfigService configService, LocalTimeStampService timeStampService, Material waxMaterial) {
        this.configService = configService;
        this.timeStampService = timeStampService;
        this.waxMaterial = waxMaterial;
    }

    @EventHandler
    public void onPrepareItemCraft(PrepareItemCraftEvent event) {
        if (!configService.isWaxingEnabled()) return;

        CraftingInventory inv = event.getInventory();
        ItemStack[] matrix = inv.getMatrix();

        ItemStack foodItem = null;
        boolean hasWax = false;
        int nonEmptyCount = 0;

        for (ItemStack item : matrix) {
            if (item == null || item.getType() == AIR) continue;
            nonEmptyCount++;
            if (item.getType() == waxMaterial && !hasWax) {
                hasWax = true;
            } else if (item.getType().isEdible() && item.getType() != Material.ROTTEN_FLESH && foodItem == null) {
                foodItem = item;
            }
        }

        if (nonEmptyCount != 2 || foodItem == null || !hasWax) return;
        if (timeStampService.isWaxed(foodItem)) {
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

        ItemStack result = event.getCurrentItem();
        if (result == null || !timeStampService.isWaxed(result)) return;

        // Verify this is a waxing recipe by checking the matrix
        CraftingInventory inv = event.getInventory();
        ItemStack[] matrix = inv.getMatrix();
        boolean hasWax = false;
        boolean hasFood = false;
        int nonEmptyCount = 0;

        for (ItemStack item : matrix) {
            if (item == null || item.getType() == AIR) continue;
            nonEmptyCount++;
            if (item.getType() == waxMaterial) hasWax = true;
            else if (item.getType().isEdible() && item.getType() != Material.ROTTEN_FLESH) hasFood = true;
        }

        if (nonEmptyCount != 2 || !hasWax || !hasFood) return;

        if (event.isShiftClick()) {
            event.setCancelled(true);

            ItemStack[] newMatrix = new ItemStack[matrix.length];
            for (int i = 0; i < matrix.length; i++) {
                if (matrix[i] == null || matrix[i].getType() == AIR) {
                    newMatrix[i] = null;
                    continue;
                }
                ItemStack matrixItem = matrix[i].clone();
                matrixItem.setAmount(matrixItem.getAmount() - 1);
                newMatrix[i] = matrixItem.getAmount() <= 0 ? null : matrixItem;
            }
            inv.setMatrix(newMatrix);

            event.getWhoClicked().getInventory().addItem(result.clone())
                    .values().forEach(overflow -> event.getWhoClicked().getWorld()
                            .dropItem(event.getWhoClicked().getLocation(), overflow));
        }
    }
}

package spoilagesystem.listeners;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;
import spoilagesystem.FoodSpoilage;
import spoilagesystem.config.LocalConfigService;
import spoilagesystem.config.SaltingRecipe;
import spoilagesystem.timestamp.LocalTimeStampService;

/**
 * Handles salting recipes during crafting.
 * Checks if a craft matches a salting recipe and applies the appropriate time modification.
 */
public final class SaltingCraftListener implements Listener {

    private final FoodSpoilage plugin;
    private final LocalConfigService configService;
    private final LocalTimeStampService timeStampService;

    public SaltingCraftListener(FoodSpoilage plugin, LocalConfigService configService, LocalTimeStampService timeStampService) {
        this.plugin = plugin;
        this.configService = configService;
        this.timeStampService = timeStampService;
    }

    @EventHandler
    public void onPrepareItemCraft(PrepareItemCraftEvent event) {
        CraftingInventory inventory = event.getInventory();
        ItemStack result = inventory.getResult();
        
        if (result == null || result.getType() == Material.AIR) {
            return;
        }

        // Check if this is a salting recipe
        SaltingRecipe recipe = checkForSaltingRecipe(inventory);
        if (recipe == null) {
            return;
        }

        plugin.getLogger().fine("Detected salting recipe for " + recipe.getFoodType());

        // Find the food item in the crafting grid
        ItemStack foodItem = findFoodItemInGrid(inventory, recipe.getFoodType());
        if (foodItem == null) {
            plugin.getLogger().fine("Could not find food item in crafting grid");
            return;
        }

        // Check if the food item has a timestamp
        if (!timeStampService.timeStampAssigned(foodItem)) {
            plugin.getLogger().fine("Food item does not have a timestamp assigned");
            // If no timestamp, just assign normal spoilage time
            inventory.setResult(timeStampService.assignTimeStamp(result));
            return;
        }

        // Check if the food has already spoiled
        if (timeStampService.timeReached(foodItem)) {
            plugin.getLogger().fine("Food item has already spoiled - cannot salt");
            // Cancel the craft by setting result to null
            inventory.setResult(null);
            return;
        }

        // Apply the salting recipe
        ItemStack saltedFood = result.clone();
        switch (recipe.getMode()) {
            case RESET:
                plugin.getLogger().fine("Resetting timestamp for " + recipe.getFoodType());
                saltedFood = timeStampService.resetTimeStamp(saltedFood);
                break;
            case EXTEND:
                plugin.getLogger().fine("Extending timestamp for " + recipe.getFoodType() + " by " + recipe.getTimeModifier());
                saltedFood = timeStampService.extendTimeStamp(saltedFood, recipe.getTimeModifier());
                break;
        }

        inventory.setResult(saltedFood);
    }

    /**
     * Checks if the crafting grid matches a salting recipe.
     * A salting recipe consists of 1 food item and N salt material items.
     * 
     * @param inventory The crafting inventory
     * @return The matching salting recipe, or null if none matches
     */
    private SaltingRecipe checkForSaltingRecipe(CraftingInventory inventory) {
        ItemStack[] matrix = inventory.getMatrix();
        
        // Count items by type
        Material foodType = null;
        Material saltType = null;
        int foodCount = 0;
        int saltCount = 0;

        for (ItemStack item : matrix) {
            if (item == null || item.getType() == Material.AIR) {
                continue;
            }
            
            // Check if this could be a food item with a salting recipe
            SaltingRecipe recipe = configService.getSaltingRecipe(item.getType());
            if (recipe != null) {
                if (foodType == null) {
                    foodType = item.getType();
                    foodCount++;
                } else if (foodType == item.getType()) {
                    foodCount++;
                } else {
                    // Multiple different food types - not a salting recipe
                    return null;
                }
            } else {
                if (saltType == null) {
                    saltType = item.getType();
                    saltCount++;
                } else if (saltType == item.getType()) {
                    saltCount++;
                } else {
                    // Multiple different salt types - not a salting recipe
                    return null;
                }
            }
        }

        // Check if we have exactly 1 food item and the correct amount of salt
        if (foodType != null && foodCount == 1 && saltType != null) {
            SaltingRecipe recipe = configService.getSaltingRecipe(foodType);
            if (recipe != null && recipe.getSaltMaterial() == saltType && saltCount == recipe.getSaltAmount()) {
                return recipe;
            }
        }

        return null;
    }

    /**
     * Finds the food item in the crafting grid.
     * 
     * @param inventory The crafting inventory
     * @param foodType The type of food to find
     * @return The food item, or null if not found
     */
    private ItemStack findFoodItemInGrid(CraftingInventory inventory, Material foodType) {
        ItemStack[] matrix = inventory.getMatrix();
        for (ItemStack item : matrix) {
            if (item != null && item.getType() == foodType) {
                return item;
            }
        }
        return null;
    }
}

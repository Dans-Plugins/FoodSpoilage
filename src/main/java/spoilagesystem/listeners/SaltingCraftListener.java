package spoilagesystem.listeners;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;
import spoilagesystem.FoodSpoilage;
import spoilagesystem.config.LocalConfigService;
import spoilagesystem.config.SaltingRecipe;
import spoilagesystem.timestamp.LocalTimeStampService;

import java.time.Duration;
import java.time.OffsetDateTime;

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

    @EventHandler(priority = EventPriority.HIGHEST)
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

        // Verify that the output type matches the recipe's food type
        if (result.getType() != recipe.getFoodType()) {
            plugin.getLogger().fine("Output type " + result.getType() + " does not match recipe food type " + recipe.getFoodType());
            return;
        }

        // Find the food item in the crafting grid
        ItemStack foodItem = findFoodItemInGrid(inventory, recipe.getFoodType());
        if (foodItem == null) {
            plugin.getLogger().fine("Could not find food item in crafting grid");
            return;
        }

        // Start from the crafting result as the base salted item
        ItemStack saltedFood = result.clone();

        // Check if the food item has a timestamp
        if (!timeStampService.timeStampAssigned(foodItem)) {
            plugin.getLogger().fine("Food item does not have a timestamp assigned");
            // If no timestamp, assign normal spoilage time as the base before applying the salting recipe
            saltedFood = timeStampService.assignTimeStamp(saltedFood);
            // Now apply the salting recipe to the freshly stamped item
            saltedFood = applySaltingRecipe(saltedFood, recipe);
            inventory.setResult(saltedFood);
            return;
        }

        // Check if the food has already spoiled
        if (timeStampService.timeReached(foodItem)) {
            plugin.getLogger().fine("Food item has already spoiled - cannot salt");
            // Cancel the craft by setting result to null
            inventory.setResult(null);
            return;
        }

        // Apply the salting recipe based on the input food's timestamp
        saltedFood = applySaltingRecipeFromInput(saltedFood, foodItem, recipe);
        inventory.setResult(saltedFood);
    }

    /**
     * Applies the salting recipe to an item that already has a timestamp.
     * 
     * @param output The output item to apply the recipe to
     * @param recipe The salting recipe to apply
     * @return The item with the recipe applied
     */
    private ItemStack applySaltingRecipe(ItemStack output, SaltingRecipe recipe) {
        switch (recipe.getMode()) {
            case RESET:
                plugin.getLogger().fine("Resetting timestamp for " + recipe.getFoodType());
                return timeStampService.resetTimeStamp(output);
            case EXTEND:
                plugin.getLogger().fine("Extending timestamp for " + recipe.getFoodType() + " by " + recipe.getTimeModifier());
                return timeStampService.extendTimeStamp(output, recipe.getTimeModifier());
            default:
                return output;
        }
    }

    /**
     * Applies the salting recipe based on the input food's expiry time.
     * For EXTEND mode, this computes the new expiry based on the input's remaining time.
     * 
     * @param output The output item to apply the recipe to
     * @param input The input food item with the original timestamp
     * @param recipe The salting recipe to apply
     * @return The item with the recipe applied
     */
    private ItemStack applySaltingRecipeFromInput(ItemStack output, ItemStack input, SaltingRecipe recipe) {
        switch (recipe.getMode()) {
            case RESET:
                plugin.getLogger().fine("Resetting timestamp for " + recipe.getFoodType());
                return timeStampService.resetTimeStamp(output);
            case EXTEND:
                plugin.getLogger().fine("Extending timestamp for " + recipe.getFoodType() + " by " + recipe.getTimeModifier());
                // Calculate the new expiry based on the input's existing expiry plus the modifier
                OffsetDateTime inputExpiry = timeStampService.getTimeStamp(input);
                if (inputExpiry == null) {
                    // Fallback to normal extend if we can't get the timestamp
                    return timeStampService.extendTimeStamp(output, recipe.getTimeModifier());
                }
                
                // Compute time from now until the new expiry (input's expiry + modifier)
                OffsetDateTime newExpiry = inputExpiry.plus(recipe.getTimeModifier());
                OffsetDateTime now = OffsetDateTime.now();
                Duration timeUntilNewExpiry = Duration.between(now, newExpiry);
                
                // Ensure we don't create a negative duration
                if (timeUntilNewExpiry.isNegative()) {
                    timeUntilNewExpiry = Duration.ZERO;
                }
                
                return timeStampService.assignTimeStamp(output, timeUntilNewExpiry);
            default:
                return output;
        }
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

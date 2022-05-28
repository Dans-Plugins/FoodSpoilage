package spoilagesystem.listeners;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.ItemStack;
import spoilagesystem.FoodSpoilage;
import spoilagesystem.config.LocalConfigService;
import spoilagesystem.factories.SpoiledFoodFactory;
import spoilagesystem.timestamp.LocalTimeStampService;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static org.bukkit.Material.AIR;

/**
 * @author Daniel McCoy Stephenson
 */
public final class CraftItemListener implements Listener {

    private final FoodSpoilage plugin;
    private final LocalConfigService configService;
    private final LocalTimeStampService timeStampService;
    private final SpoiledFoodFactory spoiledFoodFactory;

    public CraftItemListener(FoodSpoilage plugin,
                             LocalConfigService configService,
                             LocalTimeStampService timeStampService,
                             SpoiledFoodFactory spoiledFoodFactory) {
        this.plugin = plugin;
        this.configService = configService;
        this.timeStampService = timeStampService;
        this.spoiledFoodFactory = spoiledFoodFactory;
    }

    @EventHandler
    public void onCraftItem(CraftItemEvent event) {
        ItemStack item = event.getCurrentItem();
        if (item == null) {
            return;
        }
        Material type = item.getType();
        Duration time = configService.getTime(type);
        if (!type.isEdible()) {
            return;
        }
        if (!time.equals(Duration.ZERO)) {
            int amountCrafted = getAmountCrafted(event);
            int spoilAmt = configService.determineSpoiledAmount(type, amountCrafted);
            List<ItemStack> results = new ArrayList<>();
            int amount = item.getAmount();
            if (spoilAmt > 0) {
                amount = amountCrafted - spoilAmt;
                ItemStack spoiledFood = spoiledFoodFactory.createSpoiledFood(spoilAmt);
                results.add(spoiledFood);
            }
            if (amount > 0) {
                item.setAmount(amount);
                results.add(timeStampService.assignTimeStamp(item));
            }
            if (event.isShiftClick()) {
                event.setCancelled(true);
                ItemStack[] matrixItems = event.getInventory().getMatrix();
                ItemStack[] newMatrixItems = new ItemStack[9];
                for (int i = 0; i < 9; i++) {
                    ItemStack matrixItem = matrixItems[i];
                    if (matrixItem == null) {
                        continue;
                    }
                    matrixItem.setAmount(matrixItem.getAmount() - amountCrafted);
                    if (matrixItem.getAmount() <= 0) {
                        newMatrixItems[i] = null;
                    } else {
                        newMatrixItems[i] = matrixItem;
                    }
                }
                event.getInventory().setMatrix(newMatrixItems);
                event.getWhoClicked().getInventory().addItem(results.toArray(ItemStack[]::new)).values()
                        .forEach(droppedItem -> event.getWhoClicked().getWorld().dropItem(
                                event.getWhoClicked().getLocation(),
                                droppedItem
                        ));
            } else {
                results.stream().findAny().ifPresent(event::setCurrentItem);
            }
        }
    }

    private int getAmountCrafted(CraftItemEvent event) {
        ItemStack currentItem = event.getCurrentItem();
        if (currentItem == null || currentItem.getType() == AIR) {
            return 0;
        }
        ItemStack cursor = event.getCursor();
        int amount = event.getRecipe().getResult().getAmount();
        if (event.isShiftClick()) {
            int max = event.getInventory().getMaxStackSize();
            ItemStack[] matrix = event.getInventory().getMatrix();
            for (ItemStack item : matrix) {
                if (item != null && item.getType() != AIR) {
                    int ingredientAmount = item.getAmount();
                    if (ingredientAmount >= 1 && ingredientAmount <= max) {
                        max = ingredientAmount;
                    }
                }
            }
            amount *= max;
        } else {
            if (cursor != null) {
                if (cursor.getType() != AIR) {
                    return 0;
                }
            }
        }
        int spacesFree = 0;
        for (int i = 0; i < Arrays.stream(event.getWhoClicked().getInventory().getStorageContents()).filter(Objects::nonNull).toList().size(); i++) {
            spacesFree += event.getRecipe().getResult().getType().getMaxStackSize();
        }
        for (ItemStack item : event.getWhoClicked().getInventory().getStorageContents()) {
            if (item != null && item.isSimilar(event.getRecipe().getResult())) {
                spacesFree += item.getType().getMaxStackSize() - item.getAmount();
            }
        }
        if (spacesFree < amount) {
            amount = spacesFree;
        }
        return amount;
    }
}
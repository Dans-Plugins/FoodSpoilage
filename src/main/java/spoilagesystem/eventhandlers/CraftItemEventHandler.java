package spoilagesystem.eventhandlers;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.ItemStack;
import spoilagesystem.config.LocalConfigService;
import spoilagesystem.factories.SpoiledFoodFactory;
import spoilagesystem.timestamp.LocalTimeStampService;

import java.time.Duration;

/**
 * @author Daniel McCoy Stephenson
 */
public final class CraftItemEventHandler implements Listener {

    private final LocalConfigService configService;
    private final LocalTimeStampService timeStampService;
    private final SpoiledFoodFactory spoiledFoodFactory;

    public CraftItemEventHandler(LocalConfigService configService, LocalTimeStampService timeStampService, SpoiledFoodFactory spoiledFoodFactory) {
        this.configService = configService;
        this.timeStampService = timeStampService;
        this.spoiledFoodFactory = spoiledFoodFactory;
    }

    @EventHandler
    public void handle(CraftItemEvent event) {

        ItemStack item = event.getCurrentItem();
        if (item == null) return;
        Material type = item.getType();
        Duration time = configService.getTime(type);
        if (!time.equals(Duration.ZERO)) {
            cancelIfShiftClick(event);
            int spoilAmt = configService.getSpoilChance(type, item.getAmount());
            if (spoilAmt > 0) {
                item.setAmount(item.getAmount() - spoilAmt);
                ItemStack spoiled = item.clone();
                spoiled.setAmount(spoilAmt);
                ItemStack spoiledFood = spoiledFoodFactory.createSpoiledFood(spoiled);
                event.getWhoClicked().getInventory().addItem(spoiledFood);
            }
            event.setCurrentItem(timeStampService.assignTimeStamp(item, time));
        }
    }

    private void cancelIfShiftClick(CraftItemEvent event) {
        if (event.isShiftClick()) {
            event.setCancelled(true); //TODO: find better solution
        }
    }
}
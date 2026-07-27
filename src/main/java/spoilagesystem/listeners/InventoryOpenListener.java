package spoilagesystem.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.FurnaceInventory;
import org.bukkit.inventory.ItemStack;
import spoilagesystem.config.LocalConfigService;
import spoilagesystem.timestamp.LocalTimeStampService;

import java.util.Arrays;

public final class InventoryOpenListener implements Listener {

    private final LocalConfigService configService;
    private final LocalTimeStampService timeStampService;

    public InventoryOpenListener(LocalConfigService configService, LocalTimeStampService timeStampService) {
        this.configService = configService;
        this.timeStampService = timeStampService;
    }

    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent event) {
        // When timestamp-furnace-output is disabled (the default), skip the furnace result
        // slot (index 2) to avoid adding minecraft:custom_data (PDC) which would cause the
        // furnace's canBurn() check to fail in Minecraft 1.20.5+.
        // When enabled, all furnace slots including the result slot are stamped.
        boolean skipFurnaceOutput = event.getInventory() instanceof FurnaceInventory
                && !configService.isTimestampFurnaceOutput();
        ItemStack[] contents = event.getInventory().getContents();
        for (int i = 0; i < contents.length; i++) {
            if (skipFurnaceOutput && i == 2) continue; // index 2 is always the result/output slot
            timeStampService.stampIfEligible(contents[i]);
        }
        Arrays.stream(event.getPlayer().getInventory().getContents())
<<<<<<< HEAD
                .filter(Objects::nonNull)
                .forEach(this::stampIfNeeded);
    }

    private void stampIfNeeded(ItemStack item) {
        if (item != null && item.getType().isEdible() && item.getType() != Material.ROTTEN_FLESH
                && !timeStampService.timeStampAssigned(item) && !timeStampService.isWaxed(item)) {
            timeStampService.assignTimeStamp(item);
        }
=======
                .forEach(timeStampService::stampIfEligible);
>>>>>>> origin/develop
    }
}

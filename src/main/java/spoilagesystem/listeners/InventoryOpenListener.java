package spoilagesystem.listeners;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.FurnaceInventory;
import org.bukkit.inventory.ItemStack;
import spoilagesystem.timestamp.LocalTimeStampService;

import java.util.Arrays;
import java.util.Objects;

public final class InventoryOpenListener implements Listener {

    private final LocalTimeStampService timeStampService;

    public InventoryOpenListener(LocalTimeStampService timeStampService) {
        this.timeStampService = timeStampService;
    }

    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent event) {
        // Stamp edible items in the opened inventory, but skip the furnace result slot.
        // Stamping the furnace output would add minecraft:custom_data (PDC) to the item,
        // causing the furnace's canBurn() check to fail in Minecraft 1.20.5+ because the
        // strict data-component comparison sees the vanilla recipe result and the stamped
        // output item as non-similar, permanently stalling the furnace after one item.
        // Items in the furnace result slot are stamped lazily once they leave the furnace.
        if (event.getInventory() instanceof FurnaceInventory) {
            ItemStack[] contents = event.getInventory().getContents();
            for (int i = 0; i < contents.length; i++) {
                if (i == 2) continue; // index 2 is always the result/output slot
                stampIfNeeded(contents[i]);
            }
        } else {
            Arrays.stream(event.getInventory().getContents())
                    .filter(Objects::nonNull)
                    .forEach(this::stampIfNeeded);
        }
        Arrays.stream(event.getPlayer().getInventory().getContents())
                .filter(Objects::nonNull)
                .forEach(this::stampIfNeeded);
    }

    private void stampIfNeeded(ItemStack item) {
        if (item != null && item.getType().isEdible() && item.getType() != Material.ROTTEN_FLESH
                && !timeStampService.timeStampAssigned(item) && !timeStampService.isWaxed(item)) {
            timeStampService.assignTimeStamp(item);
        }
    }
}

package spoilagesystem.listeners;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;
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
        Arrays.stream(event.getInventory().getContents())
                .filter(Objects::nonNull)
                .filter(item -> item.getType().isEdible() && item.getType() != Material.ROTTEN_FLESH)
                .forEach(item -> {
                    if (!timeStampService.timeStampAssigned(item)) {
                        timeStampService.assignTimeStamp(item);
                    }
                });
        Arrays.stream(event.getPlayer().getInventory().getContents())
                .filter(Objects::nonNull)
                .filter(item -> item.getType().isEdible() && item.getType() != Material.ROTTEN_FLESH)
                .forEach(item -> {
                    if (!timeStampService.timeStampAssigned(item)) {
                        timeStampService.assignTimeStamp(item);
                    }
                });
    }
}

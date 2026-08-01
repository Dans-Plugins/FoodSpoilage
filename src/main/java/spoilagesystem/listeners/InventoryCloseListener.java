package spoilagesystem.listeners;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import spoilagesystem.timestamp.LocalTimeStampService;

import java.util.Arrays;
import java.util.Objects;

public final class InventoryCloseListener implements Listener {

    private final LocalTimeStampService timeStampService;

    public InventoryCloseListener(LocalTimeStampService timeStampService) {
        this.timeStampService = timeStampService;
    }

    /**
     * Stamps unstamped edible items in the player's inventory when they close a container.
     *
     * <p>When a player takes cooked food from a furnace output slot the item is unstamped
     * (the furnace output is intentionally left vanilla — see {@link BlockCookListener}).
     * {@code InventoryOpenEvent} fires for external containers, but pressing {@code E} to open
     * the player's own inventory does <em>not</em> fire that event in Bukkit. Stamping on
     * {@code InventoryCloseEvent} fills this gap: by the time the player closes the furnace
     * the cooked food is already in their inventory and will be stamped here.</p>
     */
    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        Arrays.stream(event.getPlayer().getInventory().getContents())
                .filter(Objects::nonNull)
                .forEach(this::stampIfNeeded);
    }

    private void stampIfNeeded(ItemStack item) {
        if (item.getType().isEdible() && item.getType() != Material.ROTTEN_FLESH
                && !timeStampService.timeStampAssigned(item) && !timeStampService.isWaxed(item)) {
            timeStampService.assignTimeStamp(item);
        }
    }
}

package spoilagesystem.listeners;

import java.time.OffsetDateTime;
import org.bukkit.block.Furnace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockCookEvent;
import org.bukkit.inventory.ItemStack;
import spoilagesystem.config.LocalConfigService;
import spoilagesystem.timestamp.LocalTimeStampService;

/**
 * @author Daniel McCoy Stephenson
 */
public final class BlockCookListener implements Listener {

    private final LocalConfigService configService;
    private final LocalTimeStampService timeStampService;

    public BlockCookListener(LocalConfigService configService, LocalTimeStampService timeStampService) {
        this.configService = configService;
        this.timeStampService = timeStampService;
    }

    @EventHandler
    public void onBlockCook(BlockCookEvent event) {
        if (!event.getResult().getType().isEdible()) {
            return;
        }

        // If the output slot already has an item of the same type, prefer to reuse its
        // meta so the newly cooked item can stack with what is already there. However,
        // only do this when the existing stack's expiry timestamp is effectively the
        // same as what would be assigned now. Otherwise, assign a fresh timestamp to
        // the new item so it spoils based on its actual cook time.
        // This behaviour can be disabled via 'furnace-output-stacking: false' in config.yml.
        if (configService.isFurnaceOutputStackingEnabled() && event.getBlock().getState() instanceof Furnace furnace) {
            ItemStack existingResult = furnace.getInventory().getResult();
            if (existingResult != null && existingResult.getType() == event.getResult().getType()) {
                // Compute what timestamp would be assigned now to a freshly cooked item.
                ItemStack freshlyStamped = timeStampService.assignTimeStamp(event.getResult().clone());

                OffsetDateTime existingTimeStamp = timeStampService.getTimeStamp(existingResult);
                OffsetDateTime newTimeStamp = timeStampService.getTimeStamp(freshlyStamped);

                if (existingTimeStamp != null && newTimeStamp != null && existingTimeStamp.equals(newTimeStamp)) {
                    // Timestamps are compatible: reuse existing meta so the items stack.
                    ItemStack result = event.getResult().clone();
                    result.setItemMeta(existingResult.getItemMeta());
                    event.setResult(result);
                } else {
                    // Timestamps differ meaningfully: keep the freshly stamped item so
                    // it spoils according to its own cook time.
                    event.setResult(freshlyStamped);
                }
                return;
            }
        }

        event.setResult(timeStampService.assignTimeStamp(event.getResult()));
    }
}
package spoilagesystem.listeners;

import org.bukkit.block.Furnace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockCookEvent;
import org.bukkit.inventory.ItemStack;
import spoilagesystem.timestamp.LocalTimeStampService;

/**
 * @author Daniel McCoy Stephenson
 */
public final class BlockCookListener implements Listener {

    private final LocalTimeStampService timeStampService;

    public BlockCookListener(LocalTimeStampService timeStampService) {
        this.timeStampService = timeStampService;
    }

    @EventHandler
    public void onBlockCook(BlockCookEvent event) {
        if (!event.getResult().getType().isEdible()) {
            return;
        }

        // If the output slot already has an item of the same type, reuse its meta so
        // the newly cooked item can stack with what is already there.  Generating a
        // fresh timestamp on every single cook operation causes each item to have a
        // unique expiry value, making them incompatible with each other and causing
        // the furnace to stop cooking after the first item in a stack.
        if (event.getBlock().getState() instanceof Furnace furnace) {
            ItemStack existingResult = furnace.getInventory().getResult();
            if (existingResult != null && existingResult.getType() == event.getResult().getType()) {
                ItemStack result = event.getResult().clone();
                result.setItemMeta(existingResult.getItemMeta());
                event.setResult(result);
                return;
            }
        }

        event.setResult(timeStampService.assignTimeStamp(event.getResult()));
    }
}
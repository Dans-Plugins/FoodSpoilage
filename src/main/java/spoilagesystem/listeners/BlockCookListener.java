package spoilagesystem.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockCookEvent;
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

    /**
     * Stamps cooked items with an expiry timestamp when the {@code timestamp-furnace-output}
     * config option is enabled. This is useful for older versions of Minecraft where furnaces
     * are not affected by custom data on items in the output slot.
     *
     * <p>When disabled (the default), items in the furnace output slot are left completely
     * vanilla so that the furnace's {@code canBurn()} check in Minecraft 1.20.5+ does not
     * stall. Items are instead stamped lazily when they reach a player:
     * {@link InventoryCloseListener} stamps the player's full inventory when they close
     * any container (including the furnace), and {@link InventoryOpenListener},
     * {@link PlayerJoinListener}, {@link EntityPickupItemListener}, and
     * {@link ItemSpawnListener} cover other acquisition paths.</p>
     */
    @EventHandler
    public void onBlockCook(BlockCookEvent event) {
        if (!configService.isTimestampFurnaceOutput()) {
            return;
        }
        timeStampService.stampIfEligible(event.getResult());
    }
}
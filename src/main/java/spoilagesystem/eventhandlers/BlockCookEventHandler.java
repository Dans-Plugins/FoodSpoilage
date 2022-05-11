package spoilagesystem.eventhandlers;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockCookEvent;
import org.bukkit.inventory.ItemStack;
import spoilagesystem.config.LocalConfigService;
import spoilagesystem.timestamp.LocalTimeStampService;

import java.time.Duration;

/**
 * @author Daniel McCoy Stephenson
 */
public final class BlockCookEventHandler implements Listener {

    private final LocalConfigService configService;
    private final LocalTimeStampService timeStampService;

    public BlockCookEventHandler(LocalConfigService configService, LocalTimeStampService timeStampService) {
        this.configService = configService;
        this.timeStampService = timeStampService;
    }

    @EventHandler
    public void handle(BlockCookEvent event) {
        ItemStack item = event.getResult();
        Material type = item.getType();
        Duration time = configService.getTime(type);

        if (!time.equals(Duration.ZERO)) {
            event.setResult(timeStampService.assignTimeStamp(item, time));
        }
    }
}
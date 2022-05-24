package spoilagesystem.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockCookEvent;
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
        event.setResult(timeStampService.assignTimeStamp(event.getResult()));
    }
}
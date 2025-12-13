package spoilagesystem.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import spoilagesystem.timestamp.LocalTimeStampService;

public final class PrepareItemCraftListener implements Listener {

    private final LocalTimeStampService timeStampService;

    public PrepareItemCraftListener(LocalTimeStampService timeStampService) {
        this.timeStampService = timeStampService;
    }

    @EventHandler
    public void onPrepareItemCraft(PrepareItemCraftEvent event) {
        // Recipe result is intentionally not modified here to prevent creating unlimited recipe variants
        // that would cause PacketPlayOutRecipeUpdate to exceed packet size limits.
        // Timestamps are assigned in CraftItemListener when items are actually crafted.
    }

}

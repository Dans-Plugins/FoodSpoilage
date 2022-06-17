package spoilagesystem.listeners;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import spoilagesystem.timestamp.LocalTimeStampService;

import java.util.Arrays;
import java.util.Objects;

public final class PlayerJoinListener implements Listener {

    private final LocalTimeStampService timeStampService;

    public PlayerJoinListener(LocalTimeStampService timeStampService) {
        this.timeStampService = timeStampService;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
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

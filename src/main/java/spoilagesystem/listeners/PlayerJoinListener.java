package spoilagesystem.listeners;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import spoilagesystem.config.LocalConfigService;
import spoilagesystem.timestamp.LocalTimeStampService;

import java.util.Arrays;
import java.util.Objects;

public final class PlayerJoinListener implements Listener {

    private final LocalConfigService configService;
    private final LocalTimeStampService timeStampService;

    public PlayerJoinListener(LocalConfigService configService, LocalTimeStampService timeStampService) {
        this.configService = configService;
        this.timeStampService = timeStampService;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (configService.isBypassPermissionsEnabled() && event.getPlayer().hasPermission("fs.bypass.timestamp")) {
            return;
        }

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

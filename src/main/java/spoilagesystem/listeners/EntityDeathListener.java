package spoilagesystem.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import spoilagesystem.timestamp.LocalTimeStampService;

import java.util.ArrayList;
import java.util.List;

public final class EntityDeathListener implements Listener {

    private final LocalTimeStampService timeStampService;

    public EntityDeathListener(LocalTimeStampService timeStampService) {
        this.timeStampService = timeStampService;
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        if (event.getEntity() instanceof Player) return;
        List<ItemStack> newDrops = new ArrayList<>();
        for (ItemStack drop : event.getDrops()) {
            if (drop.getType().isEdible()) {
                newDrops.add(timeStampService.assignTimeStamp(drop));
            } else {
                newDrops.add(drop);
            }
        }
        event.getDrops().clear();
        event.getDrops().addAll(newDrops);
    }

}

package spoilagesystem;

import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockCookEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.FurnaceSmeltEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.PluginManager;
import spoilagesystem.EventHandlers.*;

public class EventRegistry {

    private static EventRegistry instance;

    private EventRegistry() {

    }

    public static EventRegistry getInstance() {
        if (instance == null) {
            instance = new EventRegistry();
        }
        return instance;
    }

    public void registerEvents() {

        FoodSpoilage mainInstance = FoodSpoilage.getInstance();
        PluginManager manager = mainInstance.getServer().getPluginManager();

        manager.registerEvents(new CraftItemEventHandler(), mainInstance);
        manager.registerEvents(new InventoryDragEventHandler(), mainInstance);
        manager.registerEvents(new PlayerInteractEventHandler(), mainInstance);
        manager.registerEvents(new ItemSpawnEventHandler(), mainInstance);
        manager.registerEvents(new FurnaceSmeltEventHandler(), mainInstance);
        manager.registerEvents(new BlockCookEventHandler(), mainInstance);
        manager.registerEvents(new VillagerInventoryClickEvent(), mainInstance);

    }

}

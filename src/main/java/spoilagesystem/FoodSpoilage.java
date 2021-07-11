package spoilagesystem;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockCookEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.FurnaceSmeltEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;
import spoilagesystem.EventHandlers.*;
import spoilagesystem.bStats.Metrics;

import java.io.File;

public final class FoodSpoilage extends JavaPlugin {

    private static FoodSpoilage instance;

    private String version = "v1.10-beta-1";

    public static FoodSpoilage getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;

        StorageManager.getInstance().ensureSmoothTransitionBetweenVersions();

        // config creation/loading
        if (!(new File("./plugins/FoodSpoilage/config.yml").exists())) {
            StorageManager.getInstance().saveConfigDefaults();
        } else {
            StorageManager.getInstance().handleVersionMismatch();
            reloadConfig();
        }

        StorageManager.getInstance().loadValuesFromConfig();

        EventRegistry.getInstance().registerEvents();

        int pluginId = 8992;

        Metrics metrics = new Metrics(this, pluginId);
    }

    @Override
    public void onDisable() {

    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        return CommandInterpreter.getInstance().interpretCommand(sender, label, args);
    }

    public String getVersion() {
        return version;
    }
}

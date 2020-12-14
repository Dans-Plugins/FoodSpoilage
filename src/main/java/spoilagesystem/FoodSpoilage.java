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

public final class FoodSpoilage extends JavaPlugin implements Listener {

    private static FoodSpoilage instance;

    public String version = "v1.10";

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

        this.getServer().getPluginManager().registerEvents(this, this);

        int pluginId = 8992;

        Metrics metrics = new Metrics(this, pluginId);
    }

    @Override
    public void onDisable() {

    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        return CommandInterpreter.getInstance().interpretCommand(sender, label, args);
    }

    @EventHandler()
    public void onCraft(CraftItemEvent event) {
        CraftItemEventHandler handler = new CraftItemEventHandler();
        handler.handle(event);
    }

    @EventHandler()
    public void onInventoryClick(InventoryDragEvent event) {
        InventoryDragEventHandler handler = new InventoryDragEventHandler();
        handler.handle(event);
    }

    @EventHandler()
    public void onRightClick(PlayerInteractEvent event) {
        PlayerInteractEventHandler handler = new PlayerInteractEventHandler();
        handler.handle(event);
    }

    @EventHandler()
    public void onDrop(ItemSpawnEvent event) {
        ItemSpawnEventHandler handler = new ItemSpawnEventHandler();
        handler.handle(event);
    }

    @EventHandler()
    public void onFurnaceSmelt(FurnaceSmeltEvent event) {
        FurnaceSmeltEventHandler handler = new FurnaceSmeltEventHandler();
        handler.handle(event);
    }

    @EventHandler()
    public void onBlockCook(BlockCookEvent event) {
        BlockCookEventHandler handler = new BlockCookEventHandler();
        handler.handle(event);
    }
}

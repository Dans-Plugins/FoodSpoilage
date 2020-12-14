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

    public String version = "v1.10";

    // subsystems
    public TimeStamper timestamp = new TimeStamper(this);
    public StorageManager storage = new StorageManager(this);
    public Utilities utilities = new Utilities(this);

    @Override
    public void onEnable() {
        storage.ensureSmoothTransitionBetweenVersions();

        // config creation/loading
        if (!(new File("./plugins/FoodSpoilage/config.yml").exists())) {
            storage.saveConfigDefaults();
        } else {
            storage.handleVersionMismatch();
            reloadConfig();
        }

        storage.loadValuesFromConfig();

        this.getServer().getPluginManager().registerEvents(this, this);

        int pluginId = 8992;

        Metrics metrics = new Metrics(this, pluginId);
    }

    @Override
    public void onDisable() {

    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        CommandInterpreter commandInterpreter = new CommandInterpreter(this);
        return commandInterpreter.interpretCommand(sender, label, args);
    }

    @EventHandler()
    public void onCraft(CraftItemEvent event) {
        CraftItemEventHandler handler = new CraftItemEventHandler(this);
        handler.handle(event);
    }

    @EventHandler()
    public void onInventoryClick(InventoryDragEvent event) {
        InventoryDragEventHandler handler = new InventoryDragEventHandler(this);
        handler.handle(event);
    }

    @EventHandler()
    public void onRightClick(PlayerInteractEvent event) {
        PlayerInteractEventHandler handler = new PlayerInteractEventHandler(this);
        handler.handle(event);
    }

    @EventHandler()
    public void onDrop(ItemSpawnEvent event) {
        ItemSpawnEventHandler handler = new ItemSpawnEventHandler(this);
        handler.handle(event);
    }

    @EventHandler()
    public void onFurnaceSmelt(FurnaceSmeltEvent event) {
        FurnaceSmeltEventHandler handler = new FurnaceSmeltEventHandler(this);
        handler.handle(event);
    }

    @EventHandler()
    public void onBlockCook(BlockCookEvent event) {
        BlockCookEventHandler handler = new BlockCookEventHandler(this);
        handler.handle(event);
    }
}

package spoilagesystem;

import org.bstats.bukkit.Metrics;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.Plugin;
import preponderous.ponder.minecraft.bukkit.abs.PonderBukkitPlugin;
import preponderous.ponder.minecraft.bukkit.tools.EventHandlerRegistry;
import spoilagesystem.commands.DefaultCommand;
import spoilagesystem.commands.HelpCommand;
import spoilagesystem.commands.ReloadCommand;
import spoilagesystem.commands.TimeLeftCommand;
import spoilagesystem.config.LocalConfigService;
import spoilagesystem.factories.SpoiledFoodFactory;
import spoilagesystem.listeners.*;
import spoilagesystem.rpkit.FoodSpoilageRpkitExpiryService;
import spoilagesystem.timestamp.LocalTimeStampService;

import java.util.Arrays;
import java.util.List;

import static java.util.logging.Level.FINE;
import static org.bukkit.ChatColor.RED;

/**
 * @author Daniel McCoy Stephenson
 */
public final class FoodSpoilage extends PonderBukkitPlugin {

    private LocalConfigService configService;
    private LocalTimeStampService timeStampService;
    private SpoiledFoodFactory spoiledFoodFactory;

    /**
     * This runs when the server starts.
     */
    @Override
    public void onEnable() {
        if (getConfig().getBoolean("debug", false)) {
            getLogger().setLevel(FINE);
        }
        configService = new LocalConfigService(this);
        timeStampService = new LocalTimeStampService(this, configService);
        spoiledFoodFactory = new SpoiledFoodFactory(configService);
        registerEventHandlers();
        initializeCommands();
        handlebStatsIntegration();
        handleRpkitIntegration();
    }

    private void handlebStatsIntegration() {
        int pluginId = 8992;
        new Metrics(this, pluginId);
    }

    private void handleRpkitIntegration() {
        Plugin rpkFoodLib = getServer().getPluginManager().getPlugin("rpk-food-lib-bukkit");
        if (rpkFoodLib != null) {
            getLogger().info("RPKit Food Lib found, enabling integration");
            new FoodSpoilageRpkitExpiryService(this, timeStampService);
        }
    }

    /**
     * Registers the event handlers of the plugin using Ponder.
     */
    private void registerEventHandlers() {
        EventHandlerRegistry eventHandlerRegistry = new EventHandlerRegistry();
        eventHandlerRegistry.registerEventHandlers(List.of(
                new BlockCookListener(timeStampService),
                new CraftItemListener(this, configService, timeStampService, spoiledFoodFactory),
                new EntityDeathListener(timeStampService),
                new EntityPickupItemListener(timeStampService),
                new InventoryDragListener(this, timeStampService, spoiledFoodFactory),
                new InventoryOpenListener(timeStampService),
                new ItemSpawnListener(configService, timeStampService),
                new PlayerFishListener(timeStampService),
                new PlayerInteractListener(this, timeStampService, spoiledFoodFactory),
                new PlayerJoinListener(timeStampService),
                new PrepareItemCraftListener(timeStampService)
        ), this);
    }

    private void initializeCommands() {
        PluginCommand foodSpoilageCommand = getCommand("foodspoilage");
        if (foodSpoilageCommand != null) {
            DefaultCommand defaultCommand = new DefaultCommand(this);
            HelpCommand helpCommand = new HelpCommand();
            ReloadCommand reloadCommand = new ReloadCommand(this, configService);
            TimeLeftCommand timeLeftCommand = new TimeLeftCommand(configService, timeStampService);
            foodSpoilageCommand.setExecutor((sender, cmd, label, args) -> {
                if (args.length < 1) {
                    defaultCommand.onCommand(sender, cmd, label, new String[0]);
                    return true;
                }
                switch (args[0].toLowerCase()) {
                    case "help": return helpCommand.onCommand(sender, cmd, label, Arrays.stream(args).skip(1).toArray(String[]::new));
                    case "reload": return reloadCommand.onCommand(sender, cmd, label, Arrays.stream(args).skip(1).toArray(String[]::new));
                    case "timeleft": return timeLeftCommand.onCommand(sender, cmd, label, Arrays.stream(args).skip(1).toArray(String[]::new));
                    default: {
                        sender.sendMessage(RED + "That command wasn't found.");
                        return true;
                    }
                }
            });
        }
    }

}

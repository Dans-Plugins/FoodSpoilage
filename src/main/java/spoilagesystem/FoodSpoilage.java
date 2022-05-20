package spoilagesystem;

import org.bstats.bukkit.Metrics;
import org.bukkit.command.PluginCommand;
import preponderous.ponder.minecraft.bukkit.abs.PonderBukkitPlugin;
import preponderous.ponder.minecraft.bukkit.tools.EventHandlerRegistry;
import spoilagesystem.commands.DefaultCommand;
import spoilagesystem.commands.HelpCommand;
import spoilagesystem.commands.ReloadCommand;
import spoilagesystem.commands.TimeLeftCommand;
import spoilagesystem.config.LocalConfigService;
import spoilagesystem.eventhandlers.*;
import spoilagesystem.factories.SpoiledFoodFactory;
import spoilagesystem.timestamp.LocalTimeStampService;

import java.util.Arrays;
import java.util.List;

import static org.bukkit.ChatColor.RED;

import static java.util.logging.Level.FINE;

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
    }

    private void handlebStatsIntegration() {
        int pluginId = 8992;
        new Metrics(this, pluginId);
    }

    /**
     * Registers the event handlers of the plugin using Ponder.
     */
    private void registerEventHandlers() {
        EventHandlerRegistry eventHandlerRegistry = new EventHandlerRegistry();
        eventHandlerRegistry.registerEventHandlers(List.of(
                new BlockCookEventHandler(configService, timeStampService),
                new CraftItemEventHandler(configService, timeStampService, spoiledFoodFactory),
                new FurnaceSmeltEventHandler(configService, timeStampService),
                new InventoryDragEventHandler(this, timeStampService, spoiledFoodFactory),
                new ItemSpawnEventHandler(configService, timeStampService),
                new PlayerInteractEventHandler(this, timeStampService, spoiledFoodFactory)
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

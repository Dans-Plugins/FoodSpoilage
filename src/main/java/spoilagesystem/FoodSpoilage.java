package spoilagesystem;

import org.bstats.bukkit.Metrics;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;
import preponderous.ponder.minecraft.bukkit.abs.AbstractPluginCommand;
import preponderous.ponder.minecraft.bukkit.abs.PonderBukkitPlugin;
import preponderous.ponder.minecraft.bukkit.services.CommandService;
import preponderous.ponder.minecraft.bukkit.tools.EventHandlerRegistry;
import spoilagesystem.commands.DefaultCommand;
import spoilagesystem.commands.HelpCommand;
import spoilagesystem.commands.ReloadCommand;
import spoilagesystem.commands.TimeLeftCommand;
import spoilagesystem.config.LocalConfigService;
import spoilagesystem.eventhandlers.*;
import spoilagesystem.factories.SpoiledFoodFactory;
import spoilagesystem.timestamp.LocalTimeStampService;

import java.util.ArrayList;
import java.util.Arrays;

import static java.util.logging.Level.FINE;

/**
 * @author Daniel McCoy Stephenson
 */
public final class FoodSpoilage extends PonderBukkitPlugin {
    private CommandService commandService;
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
        commandService = new CommandService(getPonder());
        configService = new LocalConfigService(this);
        timeStampService = new LocalTimeStampService(this, configService);
        spoiledFoodFactory = new SpoiledFoodFactory(configService);
        registerEventHandlers();
        initializeCommandService();
        handlebStatsIntegration();
    }

    /**
     * This method handles commands sent to the minecraft server and interprets them if the label matches one of the core commands.
     * @param sender The sender of the command.
     * @param cmd The command that was sent. This is unused.
     * @param label The core command that has been invoked.
     * @param args Arguments of the core command. Often sub-commands.
     * @return A boolean indicating whether the execution of the command was successful.
     */
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        if (args.length == 0) {
            DefaultCommand defaultCommand = new DefaultCommand(this);
            return defaultCommand.execute(sender);
        }

        return commandService.interpretAndExecuteCommand(sender, label, args);
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
        ArrayList<Listener> listeners = new ArrayList<>(Arrays.asList(
                new BlockCookEventHandler(configService, timeStampService),
                new CraftItemEventHandler(configService, timeStampService, spoiledFoodFactory),
                new FurnaceSmeltEventHandler(configService, timeStampService),
                new InventoryDragEventHandler(this, timeStampService, spoiledFoodFactory),
                new ItemSpawnEventHandler(configService, timeStampService),
                new PlayerInteractEventHandler(this, timeStampService, spoiledFoodFactory)
        ));
        eventHandlerRegistry.registerEventHandlers(listeners, this);
    }

    /**
     * Initializes Ponder's command service with the plugin's commands.
     */
    private void initializeCommandService() {
        ArrayList<AbstractPluginCommand> commands = new ArrayList<>(Arrays.asList(
                new HelpCommand(),
                new TimeLeftCommand(configService, timeStampService),
                new ReloadCommand(this, configService, timeStampService)
        ));
        commandService.initialize(commands, "That command wasn't found.");
    }

}

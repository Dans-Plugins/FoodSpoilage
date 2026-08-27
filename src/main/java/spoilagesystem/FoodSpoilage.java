package spoilagesystem;

import org.bstats.bukkit.Metrics;
import org.bukkit.ChatColor;
import org.bukkit.Keyed;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.PluginCommand;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.ItemStack;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import static java.util.logging.Level.FINE;
import static java.util.logging.Level.INFO;
import static org.bukkit.ChatColor.RED;

/**
 * @author Daniel McCoy Stephenson
 */
public final class FoodSpoilage extends PonderBukkitPlugin {

    private LocalConfigService configService;
    private LocalTimeStampService timeStampService;
    private SpoiledFoodFactory spoiledFoodFactory;
    private NamespacedKey waxingRecipeKey;
    private boolean waxingRecipeRegistered;

    /**
     * This runs when the server starts.
     */
    @Override
    public void onEnable() {
        configService = new LocalConfigService(this);
        timeStampService = new LocalTimeStampService(this, configService);
        spoiledFoodFactory = new SpoiledFoodFactory(configService);
        waxingRecipeKey = new NamespacedKey(this, "waxing");
        applyLogLevel();
        registerEventHandlers();
        refreshWaxingRecipe();
        initializeCommands();
        handlebStatsIntegration();
        handleRpkitIntegration();
    }

    /**
     * Re-reads the configuration from disk and applies the settings that would otherwise stay at
     * the values captured while the plugin was starting up.
     */
    public void reload() {
        reloadConfig();
        applyLogLevel();
        refreshWaxingRecipe();
    }

    private void applyLogLevel() {
        getLogger().setLevel(configService.isDebugEnabled() ? FINE : INFO);
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
        List<org.bukkit.event.Listener> listeners = new ArrayList<>(List.of(
                new BlockCookListener(configService, timeStampService),
                new CraftItemListener(this, configService, timeStampService, spoiledFoodFactory),
                new EntityDeathListener(timeStampService),
                new EntityPickupItemListener(timeStampService),
                new InventoryCloseListener(timeStampService),
                new InventoryDragListener(this, timeStampService, spoiledFoodFactory),
                new InventoryOpenListener(configService, timeStampService),
                new ItemSpawnListener(configService, timeStampService),
                new PlayerFishListener(timeStampService),
                new PlayerInteractListener(this, timeStampService, spoiledFoodFactory),
                new PlayerJoinListener(timeStampService),
                // Registered whether or not waxing is currently enabled, so that the feature can be
                // switched on with /fs reload; the listener consults the config on every event.
                new WaxingCraftListener(configService, timeStampService, waxingRecipeKey)
        ));
        eventHandlerRegistry.registerEventHandlers(listeners, this);
    }

    /**
     * Unregisters the waxing recipe and registers it again from the current configuration, so that
     * both {@code enable-waxing} and {@code wax-material} take effect without a server restart.
     */
    private void refreshWaxingRecipe() {
        // Re-registering on top of a recipe that could not be removed would leave two recipes
        // sharing one key, so the old one staying put means this one is left alone.
        if (!removeWaxingRecipe()) return;

        if (!configService.isWaxingEnabled()) return;

        Material waxMaterial = configService.getWaxMaterial();
        if (waxMaterial == null) {
            getLogger().warning("Waxing material not found: " + configService.getWaxMaterialName() + ". Waxing feature disabled.");
            return;
        }

        List<Material> edibleMaterials = Arrays.stream(Material.values())
                .filter(Material::isEdible)
                .filter(m -> m != Material.ROTTEN_FLESH)
                .filter(m -> m != waxMaterial)
                .toList();

        if (edibleMaterials.isEmpty()) return;

        // Result is a placeholder; WaxingCraftListener overrides it via PrepareItemCraftEvent
        ItemStack placeholderResult = new ItemStack(edibleMaterials.get(0));
        var placeholderMeta = placeholderResult.getItemMeta();
        if (placeholderMeta != null) {
            placeholderMeta.setDisplayName(ChatColor.RESET + "Waxed Food (varies)");
            placeholderResult.setItemMeta(placeholderMeta);
        }
        ShapelessRecipe recipe = new ShapelessRecipe(waxingRecipeKey, placeholderResult);
        recipe.addIngredient(new RecipeChoice.MaterialChoice(waxMaterial));
        recipe.addIngredient(new RecipeChoice.MaterialChoice(edibleMaterials));
        getServer().addRecipe(recipe);
        waxingRecipeRegistered = true;
    }

    /**
     * Drops the waxing recipe from the server's recipe list, if this plugin put it there. Server
     * #removeRecipe is not part of the Spigot API version this plugin builds against, so the recipe
     * iterator is used instead; a server implementation whose iterator does not support removal is
     * reported rather than allowed to abort the reload.
     *
     * @return true when the recipe is known not to be registered afterwards
     */
    private boolean removeWaxingRecipe() {
        if (!waxingRecipeRegistered) return true;

        try {
            Iterator<Recipe> recipes = getServer().recipeIterator();
            while (recipes.hasNext()) {
                Recipe recipe = recipes.next();
                if (recipe instanceof Keyed keyed && keyed.getKey().equals(waxingRecipeKey)) {
                    recipes.remove();
                }
            }
            waxingRecipeRegistered = false;
            return true;
        } catch (UnsupportedOperationException | IllegalStateException exception) {
            getLogger().warning("Could not unregister the waxing recipe on this server implementation: "
                    + exception + ". Restart the server for a change to enable-waxing or wax-material to take full effect.");
            return false;
        }
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

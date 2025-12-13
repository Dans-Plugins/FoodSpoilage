package spoilagesystem.config;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import spoilagesystem.FoodSpoilage;
import spoilagesystem.config.migration.ConfigMigration;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * @author Daniel McCoy Stephenson
 */
public final class LocalConfigService {

    private final FoodSpoilage plugin;
    private final List<ConfigMigration> migrations;
    private final Map<Material, SaltingRecipe> saltingRecipes;

    public LocalConfigService(FoodSpoilage plugin) {
        this.plugin = plugin;
        this.migrations = List.of();
        this.random = new Random();
        this.saltingRecipes = new HashMap<>();
        runMigrations();
        plugin.saveDefaultConfig();
        loadSaltingRecipes();
    }

    private final Random random;

    /**
     * Method to obtain the Spoilage Time for the given Material.
     * 
     * @param type to obtain the spoilage time for.
     * @return int time.
     * @see org.bukkit.configuration.MemorySection#getInt(String)
     */
    public Duration getTime(Material type) {
        String durationString = plugin.getConfig().getString("spoil-time." + type.toString(), plugin.getConfig().getString("spoil-time.default"));
        if (durationString == null) return Duration.ZERO;
        Duration time = Duration.parse(durationString); // Get the time from the config.
        plugin.getLogger().fine("Time from configuration for " + type.name() + ":\t" + time);
        return time; // Return the key.
    }

    /**
     * Determines how much of a given material should spoil, given the amount that would be produced should spoiling
     * not have been present.
     *
     * @param type The type of the item
     * @param qty The quantity of the item that would be produced were none of the item to spoil
     * @return amount of the item that has spoiled
     */
    public int determineSpoiledAmount(Material type, int qty) {
        double chance = plugin.getConfig().getDouble("spoil-chance." + type.toString(), 0);
        if (chance <= 0) return 0;
        int amountSpoiled = 0;
        for (int i = 0; i < qty; i++) {
            if (random.nextDouble() <= chance) {
                amountSpoiled++;
            }
        }
        return amountSpoiled;
    }

    /**
     * Method to obtain the spoil-chance for the given Item.
     *
     * @param stack to reference
     * @return spoil chance.
     * @see #determineSpoiledAmount(Material, int)
     */
    public int determineSpoiledAmount(ItemStack stack) {
        return determineSpoiledAmount(stack.getType(), stack.getAmount());
    }

    public void runMigrations() {
        migrations.forEach(migration -> {
            if (migration.getPreviousVersion().equals(plugin.getConfig().getString("version"))) {
                migration.run();
                plugin.getConfig().set("version", migration.getNewVersion());
                plugin.saveConfig();
            }
        });
    }

    public List<String> getExpiryDateText() {
        return plugin.getConfig().getStringList("text.expiry-date-lore").stream()
                .map(line -> ChatColor.translateAlternateColorCodes('&', line))
                .toList();
    }

    public String getValuesLoadedText() {
        return ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("text.values-loaded"));
    }

    public String getNoPermsReloadText() {
        return ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("text.no-permission-reload"));
    }

    public String getSpoiledFoodName() {
        return ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("text.spoiled-food-name"));
    }

    public String getSpoiledFoodLore() {
        return ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("text.spoiled-food-lore"));
    }

    public String getNeverSpoilText() {
        return ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("text.never-spoil"));
    }

    public String getTimeLeftText() {
        return ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("text.time-left"));
    }

    public String getLessThanAnHour() {
        return ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("text.less-than-an-hour"));
    }

    public String getLessThanADay() {
        return ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("text.less-than-a-day"));
    }

    public String getNoTimeLeftText() {
        return ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("text.no-time-left"));
    }

    /**
     * Loads salting recipes from the configuration.
     */
    private void loadSaltingRecipes() {
        saltingRecipes.clear();
        if (!plugin.getConfig().contains("salting-recipes")) {
            return;
        }
        
        var saltingSection = plugin.getConfig().getConfigurationSection("salting-recipes");
        if (saltingSection == null) {
            return;
        }
        
        for (String foodTypeName : saltingSection.getKeys(false)) {
            try {
                Material foodType = Material.valueOf(foodTypeName);
                String saltMaterialName = saltingSection.getString(foodTypeName + ".salt-material");
                int saltAmount = saltingSection.getInt(foodTypeName + ".salt-amount", 1);
                String modeString = saltingSection.getString(foodTypeName + ".mode", "RESET");
                String timeModifierString = saltingSection.getString(foodTypeName + ".time-modifier", "PT0H");
                
                if (saltMaterialName == null) {
                    plugin.getLogger().warning("Invalid salting recipe configuration for " + foodTypeName + ": salt-material not specified. Expected format: 'salt-material: MATERIAL_NAME'");
                    continue;
                }
                
                Material saltMaterial = Material.valueOf(saltMaterialName);
                SaltingRecipe.SaltingMode mode = SaltingRecipe.SaltingMode.valueOf(modeString.toUpperCase());
                Duration timeModifier = Duration.parse(timeModifierString);
                
                SaltingRecipe recipe = new SaltingRecipe(foodType, saltMaterial, saltAmount, mode, timeModifier);
                saltingRecipes.put(foodType, recipe);
                plugin.getLogger().fine("Loaded salting recipe for " + foodTypeName);
            } catch (IllegalArgumentException e) {
                plugin.getLogger().warning("Invalid salting recipe configuration for " + foodTypeName + ": " + e.getMessage());
            }
        }
    }

    /**
     * Gets the salting recipe for a given food type.
     * 
     * @param foodType The food material type
     * @return The salting recipe, or null if none exists
     */
    public SaltingRecipe getSaltingRecipe(Material foodType) {
        return saltingRecipes.get(foodType);
    }

    /**
     * Reloads the configuration, including salting recipes.
     */
    public void reload() {
        plugin.reloadConfig();
        loadSaltingRecipes();
    }
}
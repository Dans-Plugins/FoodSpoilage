package spoilagesystem.config;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import spoilagesystem.FoodSpoilage;
import spoilagesystem.config.migration.ConfigMigration;

import java.time.Duration;
import java.util.List;
import java.util.Random;

/**
 * @author Daniel McCoy Stephenson
 */
public final class LocalConfigService {

    private final FoodSpoilage plugin;
    private final List<ConfigMigration> migrations;

    public LocalConfigService(FoodSpoilage plugin) {
        this.plugin = plugin;
        this.migrations = List.of();
        this.random = new Random();
        runMigrations();
        plugin.saveDefaultConfig();
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
        String durationString = plugin.getConfig().getString("spoil-time." + type.toString(), plugin.getConfig().getString("default"));
        if (durationString == null) return Duration.ZERO;
        Duration time = Duration.parse(durationString); // Get the time from the config.
        plugin.getLogger().fine("Time from configuration for " + type.name() + ":\t" + time);
        return time; // Return the key.
    }

    /**
     * Method to obtain the spoil-chance for the given material->int parameters.
     *
     * @param type of the item.
     * @param qty or quantity of the item.
     * @return spoil chance.
     */
    public int getSpoilChance(Material type, int qty) {
        double chance = plugin.getConfig().getDouble("spoil-chance." + type.toString(), 0);
        if (chance <= 0) return 0;
        double roll = random.nextDouble();
        return roll <= chance ? (int) (roll * qty) : 0;
    }

    /**
     * Method to obtain the spoil-chance for the given Item.
     *
     * @param stack to reference
     * @return spoil chance.
     * @see #getSpoilChance(Material, int)
     */
    public int getSpoilChance(ItemStack stack) {
        return getSpoilChance(stack.getType(), stack.getAmount());
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
}
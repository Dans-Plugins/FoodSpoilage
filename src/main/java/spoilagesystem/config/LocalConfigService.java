package spoilagesystem.config;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import spoilagesystem.FoodSpoilage;
import spoilagesystem.config.migration.ConfigMigration;

import java.time.Duration;
import java.time.format.DateTimeParseException;
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

    public LocalConfigService(FoodSpoilage plugin) {
        this.plugin = plugin;
        this.migrations = List.of();
        this.random = new Random();
        runMigrations();
        plugin.saveDefaultConfig();
    }

    private final Random random;
    private final Map<Material, Duration> timeCache = new HashMap<>();

    /**
     * Method to obtain the Spoilage Time for the given Material.
     * Parses an ISO-8601 duration string (e.g. "PT24H") from the config.
     * Returns {@link Duration#ZERO} if the value is null, "0", or malformed.
     * 
     * @param type the material to obtain the spoilage time for.
     * @return the spoilage duration for the given material.
     */
    public Duration getTime(Material type) {
        if (timeCache.containsKey(type)) return timeCache.get(type);
        String durationString = plugin.getConfig().getString("spoil-time." + type.toString(), plugin.getConfig().getString("spoil-time.default"));
        if (durationString == null) {
            timeCache.put(type, Duration.ZERO);
            return Duration.ZERO;
        }
        durationString = durationString.trim();
        if (durationString.equals("0")) {
            timeCache.put(type, Duration.ZERO);
            return Duration.ZERO;
        }
        try {
            Duration time = Duration.parse(durationString);
            plugin.getLogger().fine("Time from configuration for " + type.name() + ":\t" + time);
            timeCache.put(type, time);
            return time;
        } catch (DateTimeParseException e) {
            plugin.getLogger().warning("Invalid spoil-time format for " + type.name() + ": '" + durationString + "'. Expected ISO-8601 duration (e.g. PT24H). Defaulting to zero (no spoilage).");
            timeCache.put(type, Duration.ZERO);
            return Duration.ZERO;
        }
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

    /**
     * Clears the cached duration values. Should be called when the config is reloaded.
     */
    public void clearTimeCache() {
        timeCache.clear();
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
package spoilagesystem.config;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import spoilagesystem.FoodSpoilage;
import spoilagesystem.config.migration.ConfigMigration;

import java.time.Duration;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Random;

/**
 * @author Daniel McCoy Stephenson
 */
public final class LocalConfigService {

    /**
     * Pattern used for expiry dates when {@code expiry-date-format} is absent from the config, and
     * the fallback used when the configured pattern cannot be parsed.
     */
    public static final String DEFAULT_EXPIRY_DATE_FORMAT = "MM/dd/yyyy";

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
     * Parses an ISO-8601 duration string (e.g. "PT24H") from the config.
     * Falls back to {@link Duration#ZERO} (no spoilage) if the value is missing,
     * "0", or cannot be parsed as a duration.
     *
     * @param type to obtain the spoilage time for.
     * @return the spoilage duration for the given material.
     * @see org.bukkit.configuration.MemorySection#getInt(String)
     */
    public Duration getTime(Material type) {
        String durationString = plugin.getConfig().getString("spoil-time." + type.toString(), plugin.getConfig().getString("spoil-time.default"));
        if (durationString == null || durationString.trim().equals("0")) return Duration.ZERO;
        try {
            Duration time = Duration.parse(durationString); // Get the time from the config.
            plugin.getLogger().fine("Time from configuration for " + type.name() + ":\t" + time);
            return time; // Return the key.
        } catch (DateTimeParseException e) {
            plugin.getLogger().warning("Invalid spoil-time format for " + type.name() + ": '" + durationString + "'. Expected ISO-8601 duration (e.g. PT24H). Defaulting to no spoilage.");
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

    public boolean isDebugEnabled() {
        return plugin.getConfig().getBoolean("debug", false);
    }

    public String getExpiryDateFormat() {
        return plugin.getConfig().getString("expiry-date-format", DEFAULT_EXPIRY_DATE_FORMAT);
    }

    public boolean isWaxingEnabled() {
        return plugin.getConfig().getBoolean("enable-waxing", true);
    }

    public String getWaxMaterialName() {
        return plugin.getConfig().getString("wax-material", "HONEYCOMB");
    }

    /**
     * Resolves the configured {@code wax-material} name to a {@link Material}.
     *
     * @return the configured waxing material, or null when the name does not match one
     */
    public Material getWaxMaterial() {
        return Material.matchMaterial(getWaxMaterialName());
    }

    public List<String> getWaxedFoodLore() {
        return plugin.getConfig().getStringList("text.waxed-food-lore").stream()
                .map(line -> ChatColor.translateAlternateColorCodes('&', line))
                .toList();
    }

    public boolean isTimestampFurnaceOutput() {
        return plugin.getConfig().getBoolean("timestamp-furnace-output", false);
    }
}
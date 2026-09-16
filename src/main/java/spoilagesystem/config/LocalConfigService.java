package spoilagesystem.config;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.FileConfiguration;
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

    /**
     * Material food turns into when {@code spoiled-food-material} is absent from the config, and
     * the fallback used when the configured name does not resolve to an obtainable item.
     */
    public static final Material DEFAULT_SPOILED_FOOD_MATERIAL = Material.ROTTEN_FLESH;

    /**
     * Trace server usage events are sent to when {@code usage-reporting.endpoint} is absent from
     * both the config on disk and the bundled defaults.
     */
    public static final String DEFAULT_USAGE_REPORTING_ENDPOINT = "https://trace.danielstephenson.dev";

    private static final String USAGE_REPORTING_ENABLED_KEY = "usage-reporting.enabled";
    private static final String USAGE_REPORTING_ENDPOINT_KEY = "usage-reporting.endpoint";
    private static final String USAGE_REPORTING_KEY_KEY = "usage-reporting.key";

    private final FoodSpoilage plugin;
    private final List<ConfigMigration> migrations;

    public LocalConfigService(FoodSpoilage plugin) {
        this.plugin = plugin;
        this.migrations = List.of();
        this.random = new Random();
        runMigrations();
        plugin.saveDefaultConfig();
        ensureUsageReportingBlockOnDisk();
    }

    private final Random random;

    private String spoiledFoodMaterialName;
    private Material spoiledFoodMaterial;

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

    /**
     * Writes the {@code usage-reporting} block into config.yml when the file on disk lacks it, so
     * that the opt-out is visible on a server upgraded from a version before the block existed.
     * {@code saveDefaultConfig()} never touches an existing file, and the migration list is gated
     * on the config {@code version}, which such a server may hold at any earlier value; checking
     * for the block itself is what covers every upgraded installation. The values written are the
     * bundled defaults, not new literals, so the file says exactly what the jar says.
     *
     * <p>{@code isSet} is deliberate: unlike {@code contains} it ignores the bundled defaults and
     * answers only for the file on disk.</p>
     */
    void ensureUsageReportingBlockOnDisk() {
        FileConfiguration config = plugin.getConfig();
        if (config.isSet("usage-reporting")) return;
        Configuration defaults = config.getDefaults();
        if (defaults == null) return;
        for (String key : List.of(USAGE_REPORTING_ENABLED_KEY, USAGE_REPORTING_ENDPOINT_KEY, USAGE_REPORTING_KEY_KEY)) {
            config.set(key, defaults.get(key));
        }
        plugin.saveConfig();
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

    public String getSpoiledFoodMaterialName() {
        return plugin.getConfig().getString("spoiled-food-material", DEFAULT_SPOILED_FOOD_MATERIAL.name());
    }

    /**
     * Resolves the configured {@code spoiled-food-material} name to a {@link Material}. Unlike
     * {@code wax-material}, which simply disables waxing when it cannot be resolved, spoiled food
     * has to be made of something, so an unusable name falls back to
     * {@link #DEFAULT_SPOILED_FOOD_MATERIAL} rather than returning null.
     *
     * <p>The resolved material is cached against the name it came from, so that the warning below
     * is written once per configured value instead of on every item that spoils, while a new value
     * supplied through {@code /fs reload} is still picked up.</p>
     *
     * @return the material spoiled food is made of, never null
     */
    public Material getSpoiledFoodMaterial() {
        String name = getSpoiledFoodMaterialName();
        if (!name.equals(spoiledFoodMaterialName)) {
            spoiledFoodMaterial = resolveSpoiledFoodMaterial(name);
            spoiledFoodMaterialName = name;
        }
        return spoiledFoodMaterial;
    }

    private Material resolveSpoiledFoodMaterial(String name) {
        Material material = Material.matchMaterial(name);
        // A material that is not an item (a block-only material such as WATER) cannot be put in an
        // ItemStack, so it is rejected here rather than being allowed to throw on the spoilage path.
        if (material == null || !material.isItem()) {
            plugin.getLogger().warning("Invalid spoiled-food-material: '" + name
                    + "'. Expected an obtainable Bukkit material name (e.g. "
                    + DEFAULT_SPOILED_FOOD_MATERIAL.name() + "). Falling back to the default.");
            return DEFAULT_SPOILED_FOOD_MATERIAL;
        }
        return material;
    }

    public List<String> getWaxedFoodLore() {
        return plugin.getConfig().getStringList("text.waxed-food-lore").stream()
                .map(line -> ChatColor.translateAlternateColorCodes('&', line))
                .toList();
    }

    public boolean isTimestampFurnaceOutput() {
        return plugin.getConfig().getBoolean("timestamp-furnace-output", false);
    }

    // The one-argument getters, deliberately. Bukkit registers the jar's config.yml
    // as the defaults for the file on disk, and the one-argument getters fall
    // through to them for any key the file lacks -- the two-argument getters
    // return their explicit fallback instead, which for the key would be "" and
    // would read as "off". ensureUsageReportingBlockOnDisk() writes the block for
    // an upgraded server, so on a normal enable the file has the keys; the
    // fall-through only matters if that write failed. Verified against
    // YamlConfiguration, not assumed.

    public boolean isUsageReportingEnabled() {
        return plugin.getConfig().getBoolean(USAGE_REPORTING_ENABLED_KEY);
    }

    public String getUsageReportingEndpoint() {
        String endpoint = plugin.getConfig().getString(USAGE_REPORTING_ENDPOINT_KEY);
        return endpoint != null ? endpoint : DEFAULT_USAGE_REPORTING_ENDPOINT;
    }

    /** Empty when no key is configured or bundled, which the client treats as "off". */
    public String getUsageReportingKey() {
        String key = plugin.getConfig().getString(USAGE_REPORTING_KEY_KEY);
        return key != null ? key : "";
    }
}
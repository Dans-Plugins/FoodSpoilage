package spoilagesystem.services;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import spoilagesystem.FoodSpoilage;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static org.bukkit.Material.WHEAT;
import static org.bukkit.Material.values;

public class LocalConfigService {

    private static LocalConfigService instance;

    private LocalConfigService() {
        reloaded = 0;
        this.random = new Random(System.currentTimeMillis()); // Time as seed.
    }

    public static LocalConfigService getInstance() {
        return instance == null ? instance = new LocalConfigService() : instance;
    }

    private static final Map<Material, Float> SPOIL_CHANCE = new HashMap<Material, Float>() {{
        put(WHEAT, 0.3f);
    }};

    private static final Map<String, Integer> DEFAULT_SPOIL_TIMES = new HashMap<String, Integer>() {{
        put("BREAD", 24);
        put("POTATO", 48);
        put("CARROT", 48);
        put("BEETROOT", 48);
        put("BEEF", 24);
        put("PORKCHOP", 24);
        put("CHICKEN", 24);
        put("COD", 24);
        put("SALMON", 24);
        put("MUTTON", 24);
        put("RABBIT", 24);
        put("TROPICAL_FISH", 24);
        put("PUFFERFISH", 24);
        put("MUSHROOM_STEW", 72);
        put("RABBIT_STEW", 96);
        put("BEETROOT_SOUP", 72);
        put("COOKED_BEEF", 72);
        put("COOKED_PORKCHOP", 72);
        put("COOKED_CHICKEN", 72);
        put("COOKED_SALMON", 72);
        put("COOKED_MUTTON", 72);
        put("COOKED_RABBIT", 72);
        put("COOKED_COD", 72);
        put("WHEAT", 48);
        put("HAY_BLOCK", 48);
        put("MELON", 48);
        put("PUMPKIN", 48);
        put("BROWN_MUSHROOM", 48);
        put("RED_MUSHROOM", 48);
        put("NETHER_WART", 168);
        put("MELON_SLICE", 24);
        put("CAKE", 24);
        put("PUMPKIN_PIE", 24);
        put("SUGAR", 72);
        put("EGG", 72);
        put("SUGAR_CANE", 48);
        put("APPLE", 48);
        put("COOKIE", 94);
        put("POISONOUS_POTATO", 24);
        put("CHORUS_FRUIT", 94);
        put("DRIED_KELP", 72);
        put("BAKED_POTATO", 94);
        put("SWEET_BERRIES", 48);
    }};

    /**
     * Use this to see the spoil-time caching in real-time (for testing purposes only).
     */
    private static final boolean debug = false;
    private final Random random;
    private static long reloaded;
    private static long last_cached = 0;
    private static final HashMap<Material, Integer> cache = new HashMap<>();

    public String expiryDateText = "Expiry Date:";
    public String valuesLoadedText = "Values Loaded!";
    public String noPermsText = "Sorry! In order to use this command, you need the following permission: 'fs.reload'";
    public String spoiledFoodName = "Spoiled Food";
    public String spoiledFoodLore = "This food has gone bad.";
    public String neverSpoilText = "This item will never spoil.";
    public String timeLeftText = "This will expire in %s.";
    public String lessThanAnHour = "This will expire in less than an hour.";
    public String lessThanADay = "This will expire in less than a day.";

    /**
     * Method to obtain the Spoilage Time for the given Material.
     * <p>
     *     Due to the nature of how you have currently used this method, returning {@code 0} seems to a valid 'error'
     *     response from this method, this method uses {@link #cache}, {@link #last_cached} and {@link #reloaded} to
     *     create optimal lookup times for each Material type.
     *
     *     <br><br>API - Call #1
     *      <br>[00:11:16 INFO]: Getting the spoil-time for COOKED_BEEF
     *      <br>[00:11:16 INFO]: First-Time collecting information, initialising Cache.
     *      <br>[00:11:16 INFO]: Either Cache was restarted or Type had not been cached yet.
     *      <br>[00:11:16 INFO]: Time from configuration for COOKED_BEEF:       72
     *      <br>[00:11:16 INFO]: As time isn't 0, caching for the next time.
     *
     *      <br><br>API - Call #2+
     *      <br>[00:11:26 INFO]: Getting the spoil-time for COOKED_BEEF
     *      <br>[00:11:26 INFO]: Cache is still valid, returning spoil-time from map.
     *
     *      <br><br>API - Call after Reload
     *      <br>[00:11:46 INFO]: Getting the spoil-time for COOKED_BEEF
     *      <br>[00:11:46 INFO]: Reload has occurred since last collection, clearing and restarting Cache.
     *      <br>[00:11:46 INFO]: Either Cache was restarted or Type had not been cached yet.
     *      <br>[00:11:46 INFO]: Time from configuration for COOKED_BEEF:       72
     *      <br>[00:11:46 INFO]: As time isn't 0, caching for the next time.<br>
     *
     * </p>
     *
     * @param type to obtain the spoilage time for.
     * @return int time.
     * @see org.bukkit.configuration.MemorySection#getInt(String)
     */
    public int getTime(Material type) {
        if (debug) {
            System.out.println("Getting the spoil-time for " + type.name());
        }
        if (last_cached <= reloaded) { // Cover the initial boot, they will both be 0 when the first launch occurs.
            if (debug) {
                if (last_cached == reloaded && last_cached == 0) {
                    System.out.println("First-Time collecting information, initialising Cache.");
                } else {
                    System.out.println("Reload has occurred since last collection, clearing and restarting Cache.");
                }
            }
            last_cached = System.currentTimeMillis(); // Override the 'last_cached' value to right now.
            cache.clear(); // Clear the Cache if they reload.
        } else { // if the cache is still valid.
            if (debug) {
                System.out.println("Cache is still valid, returning spoil-time from map.");
            }
            if (cache.containsKey(type)) { // If we've already cached the key, then return the value.
                return cache.get(type); // Cache with working reload mechanisms!
            }
        }
        if (debug) {
            System.out.println("Either Cache was restarted or Type had not been cached yet.");
        }
        final int time = FoodSpoilage.getInstance().getConfig().getInt(type.name()); // Get the time from the config.
        if (debug) {
            System.out.println("Time from configuration for " + type.name() + ":\t" + time);
        }
        if (time != 0) { // If it isn't 0, then its valid, then we wanna cache it until the next reload.
            if (debug) {
                System.out.println("As time isn't 0, caching for the next time.");
            }
            cache.put(type, time);
        }
        return time; // Return the key.
    }

    /**
     * Method to obtain the spoil-chance for the given material->int parameters.
     *
     * @param type of the item.
     * @param Qty or quantity of the item.
     * @return spoil chance.
     */
    public int getSpoilChance(Material type, int Qty) {
        final float chance = SPOIL_CHANCE.getOrDefault(type, 0f);
        if (type == null || chance <= 0) return 0;
        final float roll = random.nextFloat();
        return roll <= chance ? (int) (roll * Qty) : 0;
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

    /**
     * Method to obtain <em>all</em> spoil times.
     * <p>
     *      Passing reference to the cache could be unsafe, so only returning a copy is easier.
     *      <br>Plus, as we're looping through all the Material values, all values will be cached.
     *      <br>Technically, to reduce lag (minuscule due to the new caching mechanism), this could
     *      be called either on a task or at configuration reload to load all of the values into the cache,
     *      but this is not required at all.
     * </p>
     *
     * @return Spoil Times map->(material:int).
     */
    public Map<Material, Integer> getSpoilTimes() {
        final HashMap<Material, Integer> spoilTimes = new HashMap<>();
        for (Material value : values()) { // Loop through all the materials.
            int time = getTime(value); // get the time.
            if (time != 0) spoilTimes.put(value, time); // if it isn't 0, add it
        }
        return spoilTimes; // Return output.
    }

    public void ensureSmoothTransitionBetweenVersions() {
        File saveFolder = new File("./plugins/Food-Spoilage/");

        // if old save files present
        if (saveFolder.exists()) {
            System.out.println("[ALERT] Old save folder name (pre v1.9) detected. Updating for compatibility.");
            // legacy load
            LocalLegacyStorageService.getInstance().legacyLoadValuesFromConfig();
            LocalLegacyStorageService.getInstance().legacyLoadCustomText();

            // Pull old data to the new configuration without passing reference
            // to the (this class) spoil-times map.
            LocalLegacyStorageService.getInstance().spoilTimes.forEach((k, v) -> {
                FoodSpoilage.getInstance().getConfig().addDefault(k.name(), v);
            });

            // save newer config
            FoodSpoilage.getInstance().getConfig().addDefault("version", FoodSpoilage.getInstance().getVersion());
            FoodSpoilage.getInstance().getConfig().addDefault("expiryDateText", expiryDateText);
            FoodSpoilage.getInstance().getConfig().addDefault("valuesLoadedText", valuesLoadedText);
            FoodSpoilage.getInstance().getConfig().addDefault("noPermsText", noPermsText);
            FoodSpoilage.getInstance().getConfig().addDefault("spoiledFoodName", spoiledFoodName);
            FoodSpoilage.getInstance().getConfig().addDefault("spoiledFoodLore", spoiledFoodLore);
            FoodSpoilage.getInstance().getConfig().addDefault("thisItemWillNeverSpoilText", neverSpoilText);
            FoodSpoilage.getInstance().getConfig().addDefault("timeLeftText", timeLeftText);
            FoodSpoilage.getInstance().getConfig().addDefault("lessThanAnHour", lessThanAnHour);

            FoodSpoilage.getInstance().getConfig().options().copyDefaults(true);
            FoodSpoilage.getInstance().saveConfig();

            // delete old directory
            LocalLegacyStorageService.getInstance().deleteLegacyFiles(saveFolder);
        }
    }

    public void handleVersionMismatch() {
        // set version
        final FoodSpoilage foodSpoilage = FoodSpoilage.getInstance();
        final FileConfiguration config = foodSpoilage.getConfig();
        if (!config.isString("version")) {
            config.addDefault("version", foodSpoilage.getVersion());
        } else {
            config.set("version", foodSpoilage.getVersion());
            foodSpoilage.saveConfig(); // Save the config after you update the version value.
        }

        // add defaults if they don't exist
        DEFAULT_SPOIL_TIMES.forEach((key, value) -> {
            if (!config.isInt(key)) {
                config.addDefault(key, value);
            }
        });

        if (!config.isString("expiryDateText")) config.addDefault("expiryDateText", expiryDateText);
        if (!config.isString("valuesLoadedText")) config.addDefault("valuesLoadedText", valuesLoadedText);
        if (!config.isString("noPermsText")) config.addDefault("noPermsText", noPermsText);
        if (!config.isString("spoiledFoodName")) config.addDefault("spoiledFoodName", spoiledFoodName);
        if (!config.isString("spoiledFoodLore")) config.addDefault("spoiledFoodLore", spoiledFoodLore);
        if (!config.isString("thisItemWillNeverSpoilText")) config.addDefault("thisItemWillNeverSpoilText", neverSpoilText);
        if (!config.isString("timeLeftText")) config.addDefault("timeLeftText", timeLeftText);
        if (!config.isString("lessThanAnHour")) config.addDefault("lessThanAnHour", lessThanAnHour);
        if (!config.isString("lessThanADay")) config.addDefault("lessThanADay", lessThanADay);
    }

    public void reload() {
        reloaded = System.currentTimeMillis();
        FoodSpoilage.getInstance().reloadConfig();
        final FileConfiguration config = FoodSpoilage.getInstance().getConfig();
        expiryDateText = config.getString("expiryDateText");
        valuesLoadedText = config.getString("valuesLoadedText");
        noPermsText = config.getString("noPermsText");
        spoiledFoodName = config.getString("spoiledFoodName");
        spoiledFoodLore = config.getString("spoiledFoodLore");
        timeLeftText = config.getString("timeLeftText");
        lessThanAnHour = config.getString("lessThanAnHour");
        lessThanADay = config.getString("lessThanADay");
    }

    public void create() {
        final FileConfiguration config = FoodSpoilage.getInstance().getConfig();
        config.set("version", FoodSpoilage.getInstance().getVersion());
        DEFAULT_SPOIL_TIMES.forEach(config::set);
        config.set("expiryDateText", expiryDateText);
        config.set("valuesLoadedText", valuesLoadedText);
        config.set("noPermsText", noPermsText);
        config.set("spoiledFoodName", spoiledFoodName);
        config.set("spoiledFoodLore", spoiledFoodLore);
        config.set("timeLeftText", timeLeftText);
        config.set("lessThanAnHour", lessThanAnHour);
        config.set("lessThanADay", lessThanADay);
        FoodSpoilage.getInstance().saveConfig();
    }
}
package spoilagesystem;

import org.bukkit.Material;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static org.bukkit.Material.*;

public class ConfigManager {

    private static ConfigManager instance;

    private ConfigManager() {

    }

    public static ConfigManager getInstance() {
        if (instance == null) {
            instance = new ConfigManager();
        }
        return instance;
    }

    private static final Map<Material, Float> SPOIL_CHANCE = new HashMap<Material, Float>() {{
        put(WHEAT, 0.3f);
    }};

    private static final Map<Material, Integer> SPOIL_TIMES = new HashMap<Material, Integer>() {{
        put(BREAD, 24);
        put(POTATO, 48);
        put(CARROT, 48);
        put(BEETROOT, 48);
        put(BEEF, 24);
        put(PORKCHOP, 24);
        put(CHICKEN, 24);
        put(COD, 24);
        put(SALMON, 24);
        put(MUTTON, 24);
        put(RABBIT, 24);
        put(TROPICAL_FISH, 24);
        put(PUFFERFISH, 24);
        put(MUSHROOM_STEW, 72);
        put(RABBIT_STEW, 96);
        put(BEETROOT_SOUP, 72);
        put(COOKED_BEEF, 72);
        put(COOKED_PORKCHOP, 72);
        put(COOKED_CHICKEN, 72);
        put(COOKED_SALMON, 72);
        put(COOKED_MUTTON, 72);
        put(COOKED_RABBIT, 72);
        put(COOKED_COD, 72);
        put(WHEAT, 48);
        put(HAY_BLOCK, 48);
        put(MELON, 48);
        put(PUMPKIN, 48);
        put(BROWN_MUSHROOM, 48);
        put(RED_MUSHROOM, 48);
        put(NETHER_WART, 168);
        put(MELON_SLICE, 24);
        put(CAKE, 24);
        put(PUMPKIN_PIE, 24);
        put(SUGAR, 72);
        put(EGG, 72);
        put(SUGAR_CANE, 48);
        put(APPLE, 48);
        put(COOKIE, 94);
        put(POISONOUS_POTATO, 24);
        put(CHORUS_FRUIT, 94);
        put(DRIED_KELP, 72);
        put(BAKED_POTATO, 94);
        put(SWEET_BERRIES, 48);
    }};

    public String expiryDateText = "Expiry Date:";
    public String valuesLoadedText = "Values Loaded!";
    public String noPermsText = "Sorry! In order to use this command, you need the following permission: 'fs.reload'";
    public String spoiledFoodName = "Spoiled Food";
    public String spoiledFoodLore = "This food has gone bad.";
    public String thisItemWillNeverSpoilText = "This item will never spoil.";
    public String timeLeftText = "This will expire in %s.";

    public int getTime(Material type) {
        Integer time = SPOIL_TIMES.get(type);
        return time != null ? time : 0;
    }

    public int getSpoilChance(Material type, int Qty) {
        Float chance = SPOIL_CHANCE.get(type);
        if (type != null) {
            if (chance != null) {
                Random rand = new Random();
                float roll = rand.nextFloat();
                if (roll <= chance) {
                    return (int)(roll * Qty);
                }
            }
        }
        return 0;
    }

    public Map<Material, Integer> getSpoilTimes() {
        return SPOIL_TIMES;
    }

    public void ensureSmoothTransitionBetweenVersions() {
        File saveFolder = new File("./plugins/Food-Spoilage/");

        // if old save files present
        if (saveFolder.exists()) {
            System.out.println("[ALERT] Old save folder name (pre v1.9) detected. Updating for compatibility.");
            // legacy load
            LegacyStorageManager.getInstance().legacyLoadValuesFromConfig();
            LegacyStorageManager.getInstance().legacyLoadCustomText();

            // save config
            saveConfigDefaults();

            // delete old directory
            LegacyStorageManager.getInstance().deleteLegacyFiles(saveFolder);
        }
    }

    public void handleVersionMismatch() {
        // set version
        if (!FoodSpoilage.getInstance().getConfig().isString("version")) {
            FoodSpoilage.getInstance().getConfig().addDefault("version", FoodSpoilage.getInstance().getVersion());
        }
        else {
            FoodSpoilage.getInstance().getConfig().set("version", FoodSpoilage.getInstance().getVersion());
        }

        // add defaults if they don't exist
        SPOIL_TIMES.forEach((key, value) -> {
            if (!FoodSpoilage.getInstance().getConfig().isInt(key.toString())) {
                FoodSpoilage.getInstance().getConfig().addDefault(key.toString(), value);
            }
        });

        if (!FoodSpoilage.getInstance().getConfig().isString("expiryDateText")) {
            FoodSpoilage.getInstance().getConfig().addDefault("expiryDateText", expiryDateText);
        }
        if (!FoodSpoilage.getInstance().getConfig().isString("valuesLoadedText")) {
            FoodSpoilage.getInstance().getConfig().addDefault("valuesLoadedText", valuesLoadedText);
        }
        if (!FoodSpoilage.getInstance().getConfig().isString("noPermsText")) {
            FoodSpoilage.getInstance().getConfig().addDefault("noPermsText", noPermsText);
        }
        if (!FoodSpoilage.getInstance().getConfig().isString("spoiledFoodName")) {
            FoodSpoilage.getInstance().getConfig().addDefault("spoiledFoodName", spoiledFoodName);
        }
        if (!FoodSpoilage.getInstance().getConfig().isString("spoiledFoodLore")) {
            FoodSpoilage.getInstance().getConfig().addDefault("spoiledFoodLore", spoiledFoodLore);
        }
        if (!FoodSpoilage.getInstance().getConfig().isString("thisItemWillNeverSpoilText")) {
            FoodSpoilage.getInstance().getConfig().addDefault("thisItemWillNeverSpoilText", thisItemWillNeverSpoilText);
        }
        if (!FoodSpoilage.getInstance().getConfig().isString("timeLeftText")) {
            FoodSpoilage.getInstance().getConfig().addDefault("timeLeftText", timeLeftText);
        }
    }

    public void saveConfigDefaults() {
        FoodSpoilage.getInstance().getConfig().addDefault("version", FoodSpoilage.getInstance().getVersion());

        SPOIL_TIMES.forEach((key, value) -> FoodSpoilage.getInstance().getConfig().addDefault(key.toString(), value));

        FoodSpoilage.getInstance().getConfig().addDefault("expiryDateText", expiryDateText);
        FoodSpoilage.getInstance().getConfig().addDefault("valuesLoadedText", valuesLoadedText);
        FoodSpoilage.getInstance().getConfig().addDefault("noPermsText", noPermsText);
        FoodSpoilage.getInstance().getConfig().addDefault("spoiledFoodName", spoiledFoodName);
        FoodSpoilage.getInstance().getConfig().addDefault("spoiledFoodLore", spoiledFoodLore);
        FoodSpoilage.getInstance().getConfig().addDefault("thisItemWillNeverSpoilText", thisItemWillNeverSpoilText);
        FoodSpoilage.getInstance().getConfig().addDefault("timeLeftText", timeLeftText);

        FoodSpoilage.getInstance().getConfig().options().copyDefaults(true);
        FoodSpoilage.getInstance().saveConfig();
    }

    public void loadValuesFromConfig() {
        SPOIL_TIMES.put(BREAD, FoodSpoilage.getInstance().getConfig().getInt("BREAD"));
        SPOIL_TIMES.put(POTATO, FoodSpoilage.getInstance().getConfig().getInt("POTATO"));
        SPOIL_TIMES.put(CARROT, FoodSpoilage.getInstance().getConfig().getInt("CARROT"));
        SPOIL_TIMES.put(BEETROOT, FoodSpoilage.getInstance().getConfig().getInt("BEETROOT"));
        SPOIL_TIMES.put(BEEF, FoodSpoilage.getInstance().getConfig().getInt("BEEF"));
        SPOIL_TIMES.put(PORKCHOP, FoodSpoilage.getInstance().getConfig().getInt("PORKCHOP"));
        SPOIL_TIMES.put(CHICKEN, FoodSpoilage.getInstance().getConfig().getInt("CHICKEN"));
        SPOIL_TIMES.put(COD, FoodSpoilage.getInstance().getConfig().getInt("COD"));
        SPOIL_TIMES.put(SALMON, FoodSpoilage.getInstance().getConfig().getInt("SALMON"));
        SPOIL_TIMES.put(MUTTON, FoodSpoilage.getInstance().getConfig().getInt("MUTTON"));
        SPOIL_TIMES.put(RABBIT, FoodSpoilage.getInstance().getConfig().getInt("RABBIT"));
        SPOIL_TIMES.put(TROPICAL_FISH, FoodSpoilage.getInstance().getConfig().getInt("TROPICAL_FISH"));
        SPOIL_TIMES.put(PUFFERFISH, FoodSpoilage.getInstance().getConfig().getInt("PUFFERFISH"));
        SPOIL_TIMES.put(MUSHROOM_STEW, FoodSpoilage.getInstance().getConfig().getInt("MUSHROOM_STEW"));
        SPOIL_TIMES.put(RABBIT_STEW, FoodSpoilage.getInstance().getConfig().getInt("RABBIT_STEW"));
        SPOIL_TIMES.put(BEETROOT_SOUP, FoodSpoilage.getInstance().getConfig().getInt("BEETROOT_SOUP"));
        SPOIL_TIMES.put(COOKED_BEEF, FoodSpoilage.getInstance().getConfig().getInt("COOKED_BEEF"));
        SPOIL_TIMES.put(COOKED_PORKCHOP, FoodSpoilage.getInstance().getConfig().getInt("COOKED_PORKCHOP"));
        SPOIL_TIMES.put(COOKED_CHICKEN, FoodSpoilage.getInstance().getConfig().getInt("COOKED_CHICKEN"));
        SPOIL_TIMES.put(COOKED_SALMON, FoodSpoilage.getInstance().getConfig().getInt("COOKED_SALMON"));
        SPOIL_TIMES.put(COOKED_MUTTON, FoodSpoilage.getInstance().getConfig().getInt("COOKED_MUTTON"));
        SPOIL_TIMES.put(COOKED_RABBIT, FoodSpoilage.getInstance().getConfig().getInt("COOKED_RABBIT"));
        SPOIL_TIMES.put(COOKED_COD, FoodSpoilage.getInstance().getConfig().getInt("COOKED_COD"));
        SPOIL_TIMES.put(WHEAT, FoodSpoilage.getInstance().getConfig().getInt("WHEAT"));
        SPOIL_TIMES.put(MELON, FoodSpoilage.getInstance().getConfig().getInt("MELON"));
        SPOIL_TIMES.put(PUMPKIN, FoodSpoilage.getInstance().getConfig().getInt("PUMPKIN"));
        SPOIL_TIMES.put(BROWN_MUSHROOM, FoodSpoilage.getInstance().getConfig().getInt("BROWN_MUSHROOM"));
        SPOIL_TIMES.put(RED_MUSHROOM, FoodSpoilage.getInstance().getConfig().getInt("RED_MUSHROOM"));
        SPOIL_TIMES.put(NETHER_WART, FoodSpoilage.getInstance().getConfig().getInt("NETHER_WART"));
        SPOIL_TIMES.put(MELON_SLICE, FoodSpoilage.getInstance().getConfig().getInt("MELON_SLICE"));
        SPOIL_TIMES.put(CAKE, FoodSpoilage.getInstance().getConfig().getInt("CAKE"));
        SPOIL_TIMES.put(PUMPKIN_PIE, FoodSpoilage.getInstance().getConfig().getInt("PUMPKIN_PIE"));
        SPOIL_TIMES.put(SUGAR, FoodSpoilage.getInstance().getConfig().getInt("SUGAR"));
        SPOIL_TIMES.put(EGG, FoodSpoilage.getInstance().getConfig().getInt("EGG"));
        SPOIL_TIMES.put(SUGAR_CANE, FoodSpoilage.getInstance().getConfig().getInt("SUGAR_CANE"));
        SPOIL_TIMES.put(APPLE, FoodSpoilage.getInstance().getConfig().getInt("APPLE"));
        SPOIL_TIMES.put(COOKIE, FoodSpoilage.getInstance().getConfig().getInt("COOKIE"));
        SPOIL_TIMES.put(POISONOUS_POTATO, FoodSpoilage.getInstance().getConfig().getInt("POISONOUS_POTATO"));
        SPOIL_TIMES.put(CHORUS_FRUIT, FoodSpoilage.getInstance().getConfig().getInt("CHORUS_FRUIT"));
        SPOIL_TIMES.put(DRIED_KELP, FoodSpoilage.getInstance().getConfig().getInt("DRIED_KELP"));
        SPOIL_TIMES.put(BAKED_POTATO, FoodSpoilage.getInstance().getConfig().getInt("BAKED_POTATO"));
        SPOIL_TIMES.put(SWEET_BERRIES, FoodSpoilage.getInstance().getConfig().getInt("SWEET_BERRIES"));
        SPOIL_TIMES.put(HAY_BLOCK, FoodSpoilage.getInstance().getConfig().getInt("HAY_BLOCK"));

        expiryDateText = FoodSpoilage.getInstance().getConfig().getString("expiryDateText");
        valuesLoadedText = FoodSpoilage.getInstance().getConfig().getString("valuesLoadedText");
        noPermsText = FoodSpoilage.getInstance().getConfig().getString("noPermsText");
        spoiledFoodName = FoodSpoilage.getInstance().getConfig().getString("spoiledFoodName");
        spoiledFoodLore = FoodSpoilage.getInstance().getConfig().getString("spoiledFoodLore");
    }

    public void reloadValuesFromConfig() {
        SPOIL_TIMES.replace(BREAD, FoodSpoilage.getInstance().getConfig().getInt("BREAD"));
        SPOIL_TIMES.replace(POTATO, FoodSpoilage.getInstance().getConfig().getInt("POTATO"));
        SPOIL_TIMES.replace(CARROT, FoodSpoilage.getInstance().getConfig().getInt("CARROT"));
        SPOIL_TIMES.replace(BEETROOT, FoodSpoilage.getInstance().getConfig().getInt("BEETROOT"));
        SPOIL_TIMES.replace(BEEF, FoodSpoilage.getInstance().getConfig().getInt("BEEF"));
        SPOIL_TIMES.replace(PORKCHOP, FoodSpoilage.getInstance().getConfig().getInt("PORKCHOP"));
        SPOIL_TIMES.replace(CHICKEN, FoodSpoilage.getInstance().getConfig().getInt("CHICKEN"));
        SPOIL_TIMES.replace(COD, FoodSpoilage.getInstance().getConfig().getInt("COD"));
        SPOIL_TIMES.replace(SALMON, FoodSpoilage.getInstance().getConfig().getInt("SALMON"));
        SPOIL_TIMES.replace(MUTTON, FoodSpoilage.getInstance().getConfig().getInt("MUTTON"));
        SPOIL_TIMES.replace(RABBIT, FoodSpoilage.getInstance().getConfig().getInt("RABBIT"));
        SPOIL_TIMES.replace(TROPICAL_FISH, FoodSpoilage.getInstance().getConfig().getInt("TROPICAL_FISH"));
        SPOIL_TIMES.replace(PUFFERFISH, FoodSpoilage.getInstance().getConfig().getInt("PUFFERFISH"));
        SPOIL_TIMES.replace(MUSHROOM_STEW, FoodSpoilage.getInstance().getConfig().getInt("MUSHROOM_STEW"));
        SPOIL_TIMES.replace(RABBIT_STEW, FoodSpoilage.getInstance().getConfig().getInt("RABBIT_STEW"));
        SPOIL_TIMES.replace(BEETROOT_SOUP, FoodSpoilage.getInstance().getConfig().getInt("BEETROOT_SOUP"));
        SPOIL_TIMES.replace(COOKED_BEEF, FoodSpoilage.getInstance().getConfig().getInt("COOKED_BEEF"));
        SPOIL_TIMES.replace(COOKED_PORKCHOP, FoodSpoilage.getInstance().getConfig().getInt("COOKED_PORKCHOP"));
        SPOIL_TIMES.replace(COOKED_CHICKEN, FoodSpoilage.getInstance().getConfig().getInt("COOKED_CHICKEN"));
        SPOIL_TIMES.replace(COOKED_SALMON, FoodSpoilage.getInstance().getConfig().getInt("COOKED_SALMON"));
        SPOIL_TIMES.replace(COOKED_MUTTON, FoodSpoilage.getInstance().getConfig().getInt("COOKED_MUTTON"));
        SPOIL_TIMES.replace(COOKED_RABBIT, FoodSpoilage.getInstance().getConfig().getInt("COOKED_RABBIT"));
        SPOIL_TIMES.replace(COOKED_COD, FoodSpoilage.getInstance().getConfig().getInt("COOKED_COD"));
        SPOIL_TIMES.replace(WHEAT, FoodSpoilage.getInstance().getConfig().getInt("WHEAT"));
        SPOIL_TIMES.replace(MELON, FoodSpoilage.getInstance().getConfig().getInt("MELON"));
        SPOIL_TIMES.replace(PUMPKIN, FoodSpoilage.getInstance().getConfig().getInt("PUMPKIN"));
        SPOIL_TIMES.replace(BROWN_MUSHROOM, FoodSpoilage.getInstance().getConfig().getInt("BROWN_MUSHROOM"));
        SPOIL_TIMES.replace(RED_MUSHROOM, FoodSpoilage.getInstance().getConfig().getInt("RED_MUSHROOM"));
        SPOIL_TIMES.replace(NETHER_WART, FoodSpoilage.getInstance().getConfig().getInt("NETHER_WART"));
        SPOIL_TIMES.replace(MELON_SLICE, FoodSpoilage.getInstance().getConfig().getInt("MELON_SLICE"));
        SPOIL_TIMES.replace(CAKE, FoodSpoilage.getInstance().getConfig().getInt("CAKE"));
        SPOIL_TIMES.replace(PUMPKIN_PIE, FoodSpoilage.getInstance().getConfig().getInt("PUMPKIN_PIE"));
        SPOIL_TIMES.replace(SUGAR, FoodSpoilage.getInstance().getConfig().getInt("SUGAR"));
        SPOIL_TIMES.replace(EGG, FoodSpoilage.getInstance().getConfig().getInt("EGG"));
        SPOIL_TIMES.replace(SUGAR_CANE, FoodSpoilage.getInstance().getConfig().getInt("SUGAR_CANE"));
        SPOIL_TIMES.replace(APPLE, FoodSpoilage.getInstance().getConfig().getInt("APPLE"));
        SPOIL_TIMES.replace(COOKIE, FoodSpoilage.getInstance().getConfig().getInt("COOKIE"));
        SPOIL_TIMES.replace(POISONOUS_POTATO, FoodSpoilage.getInstance().getConfig().getInt("POISONOUS_POTATO"));
        SPOIL_TIMES.replace(CHORUS_FRUIT, FoodSpoilage.getInstance().getConfig().getInt("CHORUS_FRUIT"));
        SPOIL_TIMES.replace(DRIED_KELP, FoodSpoilage.getInstance().getConfig().getInt("DRIED_KELP"));
        SPOIL_TIMES.replace(BAKED_POTATO, FoodSpoilage.getInstance().getConfig().getInt("BAKED_POTATO"));
        SPOIL_TIMES.replace(SWEET_BERRIES, FoodSpoilage.getInstance().getConfig().getInt("SWEET_BERRIES"));
        SPOIL_TIMES.replace(HAY_BLOCK, FoodSpoilage.getInstance().getConfig().getInt("HAY_BLOCK"));

        expiryDateText = FoodSpoilage.getInstance().getConfig().getString("expiryDateText");
        valuesLoadedText = FoodSpoilage.getInstance().getConfig().getString("valuesLoadedText");
        noPermsText = FoodSpoilage.getInstance().getConfig().getString("noPermsText");
        spoiledFoodName = FoodSpoilage.getInstance().getConfig().getString("spoiledFoodName");
        spoiledFoodLore = FoodSpoilage.getInstance().getConfig().getString("spoiledFoodLore");
    }
}
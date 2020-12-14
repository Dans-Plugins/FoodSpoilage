package spoilagesystem;

import org.bukkit.Material;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import static org.bukkit.Material.*;

public class StorageManager {

    FoodSpoilage foodSpoilage = null;

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

    public String createdText = "Created:";
    public String expiryDateText = "Expiry Date:";
    public String valuesLoadedText = "Values Loaded!";
    public String noPermsText = "Sorry! In order to use this command, you need the following permission: 'fs.reload'";
    public String spoiledFoodName = "Spoiled Food";
    public String spoiledFoodLore = "This food has gone bad.";

    public StorageManager(FoodSpoilage plugin) {
        foodSpoilage = plugin;
    }

    public int getTime(Material type) {
        Integer time = SPOIL_TIMES.get(type);
        return time != null ? time : 0;
    }

    public void ensureSmoothTransitionBetweenVersions() {
        File saveFolder = new File("./plugins/Food-Spoilage/");

        // if old save files present
        if (saveFolder.exists()) {
            System.out.println("[ALERT] Old save folder name (pre v1.9) detected. Updating for compatibility.");
            // legacy load
            legacyLoadValuesFromConfig();
            legacyLoadCustomText();

            // save config
            saveConfigDefaults();

            // delete old directory
            deleteLegacyFiles(saveFolder);
        }
    }

    // Recursive file delete from https://www.baeldung.com/java-delete-directory
    boolean deleteLegacyFiles(File directoryToBeDeleted) {
        File[] allContents = directoryToBeDeleted.listFiles();
        if (allContents != null) {
            for (File file : allContents) {
                deleteLegacyFiles(file);
            }
        }
        if (directoryToBeDeleted.getAbsolutePath().contains("config.yml")) {
            return true;
        }
        return directoryToBeDeleted.delete();
    }

    public void handleVersionMismatch() {

        if (!foodSpoilage.version.equalsIgnoreCase(foodSpoilage.getConfig().getString("version"))) {
            System.out.println("[ALERT] Version mismatch! Saving old config as config.yml.old and loading in the default values.");
            renameConfigToConfigDotOldAndSaveDefaults();
        }

    }

    private void renameConfigToConfigDotOldAndSaveDefaults() {

        // save old config as config.yml.old
        File saveFile = new File("./plugins/FoodSpoilage/config.yml");
        if (saveFile.exists()) {

            // rename file
            if (saveFile.renameTo(new File("./plugins/FoodSpoilage/config.yml.old"))) {

                // save defaults
                saveConfigDefaults();
            } else {
                System.out.println("Failed to rename config file!");
            }

        }

    }

    public void saveConfigDefaults() {
        foodSpoilage.getConfig().addDefault("version", foodSpoilage.version);

        SPOIL_TIMES.forEach((key, value) -> foodSpoilage.getConfig().addDefault(key.toString(), value));

        foodSpoilage.getConfig().addDefault("createdText", createdText);
        foodSpoilage.getConfig().addDefault("expiryDateText", expiryDateText);
        foodSpoilage.getConfig().addDefault("valuesLoadedText", valuesLoadedText);
        foodSpoilage.getConfig().addDefault("noPermsText", noPermsText);
        foodSpoilage.getConfig().addDefault("spoiledFoodName", spoiledFoodName);
        foodSpoilage.getConfig().addDefault("spoiledFoodLore", spoiledFoodLore);

        foodSpoilage.getConfig().options().copyDefaults(true);
        foodSpoilage.saveConfig();
    }

    public void loadValuesFromConfig() {
        SPOIL_TIMES.put(BREAD, foodSpoilage.getConfig().getInt("Bread"));
        SPOIL_TIMES.put(POTATO, foodSpoilage.getConfig().getInt("Potato"));
        SPOIL_TIMES.put(CARROT, foodSpoilage.getConfig().getInt("Carrot"));
        SPOIL_TIMES.put(BEETROOT, foodSpoilage.getConfig().getInt("Beetroot"));
        SPOIL_TIMES.put(BEEF, foodSpoilage.getConfig().getInt("Beef"));
        SPOIL_TIMES.put(PORKCHOP, foodSpoilage.getConfig().getInt("Porkchop"));
        SPOIL_TIMES.put(CHICKEN, foodSpoilage.getConfig().getInt("Chicken"));
        SPOIL_TIMES.put(COD, foodSpoilage.getConfig().getInt("Cod"));
        SPOIL_TIMES.put(SALMON, foodSpoilage.getConfig().getInt("Salmon"));
        SPOIL_TIMES.put(MUTTON, foodSpoilage.getConfig().getInt("Mutton"));
        SPOIL_TIMES.put(RABBIT, foodSpoilage.getConfig().getInt("Rabbit"));
        SPOIL_TIMES.put(TROPICAL_FISH, foodSpoilage.getConfig().getInt("Tropical_Fish"));
        SPOIL_TIMES.put(PUFFERFISH, foodSpoilage.getConfig().getInt("Pufferfish"));
        SPOIL_TIMES.put(MUSHROOM_STEW, foodSpoilage.getConfig().getInt("Mushroom_Stew"));
        SPOIL_TIMES.put(RABBIT_STEW, foodSpoilage.getConfig().getInt("Rabbit_Stew"));
        SPOIL_TIMES.put(BEETROOT_SOUP, foodSpoilage.getConfig().getInt("Beetroot_Soup"));
        SPOIL_TIMES.put(COOKED_BEEF, foodSpoilage.getConfig().getInt("Cooked_Beef"));
        SPOIL_TIMES.put(COOKED_PORKCHOP, foodSpoilage.getConfig().getInt("Cooked_Porkchop"));
        SPOIL_TIMES.put(COOKED_CHICKEN, foodSpoilage.getConfig().getInt("Cooked_Chicken"));
        SPOIL_TIMES.put(COOKED_SALMON, foodSpoilage.getConfig().getInt("Cooked_Salmon"));
        SPOIL_TIMES.put(COOKED_MUTTON, foodSpoilage.getConfig().getInt("Cooked_Mutton"));
        SPOIL_TIMES.put(COOKED_RABBIT, foodSpoilage.getConfig().getInt("Cooked_Rabbit"));
        SPOIL_TIMES.put(COOKED_COD, foodSpoilage.getConfig().getInt("Cooked_Cod"));
        SPOIL_TIMES.put(WHEAT, foodSpoilage.getConfig().getInt("Wheat"));
        SPOIL_TIMES.put(MELON, foodSpoilage.getConfig().getInt("Melon"));
        SPOIL_TIMES.put(PUMPKIN, foodSpoilage.getConfig().getInt("Pumpkin"));
        SPOIL_TIMES.put(BROWN_MUSHROOM, foodSpoilage.getConfig().getInt("Brown_Mushroom"));
        SPOIL_TIMES.put(RED_MUSHROOM, foodSpoilage.getConfig().getInt("Red_Mushroom"));
        SPOIL_TIMES.put(NETHER_WART, foodSpoilage.getConfig().getInt("Nether_Wart"));
        SPOIL_TIMES.put(MELON_SLICE, foodSpoilage.getConfig().getInt("Melon_Slice"));
        SPOIL_TIMES.put(CAKE, foodSpoilage.getConfig().getInt("Cake"));
        SPOIL_TIMES.put(PUMPKIN_PIE, foodSpoilage.getConfig().getInt("Pumpkin_Pie"));
        SPOIL_TIMES.put(SUGAR, foodSpoilage.getConfig().getInt("Sugar"));
        SPOIL_TIMES.put(EGG, foodSpoilage.getConfig().getInt("Egg"));
        SPOIL_TIMES.put(SUGAR_CANE, foodSpoilage.getConfig().getInt("Sugar_Cane"));
        SPOIL_TIMES.put(APPLE, foodSpoilage.getConfig().getInt("Apple"));
        SPOIL_TIMES.put(COOKIE, foodSpoilage.getConfig().getInt("Cookie"));
        SPOIL_TIMES.put(POISONOUS_POTATO, foodSpoilage.getConfig().getInt("Poisonous_Potato"));
        SPOIL_TIMES.put(CHORUS_FRUIT, foodSpoilage.getConfig().getInt("Chorus_Fruit"));
        SPOIL_TIMES.put(DRIED_KELP, foodSpoilage.getConfig().getInt("Dried_Kelp"));
        SPOIL_TIMES.put(BAKED_POTATO, foodSpoilage.getConfig().getInt("Baked_Potato"));
        SPOIL_TIMES.put(SWEET_BERRIES, foodSpoilage.getConfig().getInt("Sweet_Berries"));

        createdText = foodSpoilage.getConfig().getString("createdText");
        expiryDateText = foodSpoilage.getConfig().getString("expiryDateText");
        valuesLoadedText = foodSpoilage.getConfig().getString("valuesLoadedText");
        noPermsText = foodSpoilage.getConfig().getString("noPermsText");
        spoiledFoodName = foodSpoilage.getConfig().getString("spoiledFoodName");
        spoiledFoodLore = foodSpoilage.getConfig().getString("spoiledFoodLore");
    }

    public void legacyLoadValuesFromConfig() {

        try {
            System.out.println("Attempting to load food spoilage times...");
            Scanner loadReader = new Scanner(new File("./plugins/Food-Spoilage/" + "food-spoilage-times.txt"));

            // actual loading

            // ignore version
            /*String temp1 = */
            loadReader.nextLine();

            // ignore second line
            /*String temp2 = */
            loadReader.nextLine();

            int value = -1;
            if (loadReader.hasNextLine()) {
                // get value from each config line and set it to corresponding field
                value = getValueFromConfigLine(loadReader.nextLine()); // line 2
                if (value != -1) {
                    SPOIL_TIMES.put(BREAD, value);
                }
            }

            if (loadReader.hasNextLine()) {
                value = getValueFromConfigLine(loadReader.nextLine()); // line 3
                if (value != -1) {
                    SPOIL_TIMES.put(POTATO, value);
                }
            }

            if (loadReader.hasNextLine()) {
                value = getValueFromConfigLine(loadReader.nextLine()); // line 4
                if (value != -1) {
                    SPOIL_TIMES.put(CARROT, value);
                }
            }

            if (loadReader.hasNextLine()) {
                value = getValueFromConfigLine(loadReader.nextLine()); // line 5
                if (value != -1) {
                    SPOIL_TIMES.put(BEETROOT, value);
                }
            }

            if (loadReader.hasNextLine()) {
                value = getValueFromConfigLine(loadReader.nextLine());
                if (value != -1) {
                    SPOIL_TIMES.put(BEEF, value);
                }
            }

            if (loadReader.hasNextLine()) {
                value = getValueFromConfigLine(loadReader.nextLine());
                if (value != -1) {
                    SPOIL_TIMES.put(PORKCHOP, value);
                }
            }

            if (loadReader.hasNextLine()) {
                value = getValueFromConfigLine(loadReader.nextLine());
                if (value != -1) {
                    SPOIL_TIMES.put(CHICKEN, value);
                }
            }

            if (loadReader.hasNextLine()) {
                value = getValueFromConfigLine(loadReader.nextLine());
                if (value != -1) {
                    SPOIL_TIMES.put(COD, value);
                }
            }

            if (loadReader.hasNextLine()) {
                value = getValueFromConfigLine(loadReader.nextLine());
                if (value != -1) {
                    SPOIL_TIMES.put(SALMON, value);
                }
            }

            if (loadReader.hasNextLine()) {
                value = getValueFromConfigLine(loadReader.nextLine());
                if (value != -1) {
                    SPOIL_TIMES.put(MUTTON, value);
                }
            }

            if (loadReader.hasNextLine()) {
                value = getValueFromConfigLine(loadReader.nextLine());
                if (value != -1) {
                    SPOIL_TIMES.put(RABBIT, value);
                }
            }

            if (loadReader.hasNextLine()) {
                value = getValueFromConfigLine(loadReader.nextLine());
                if (value != -1) {
                    SPOIL_TIMES.put(TROPICAL_FISH, value);
                }
            }

            if (loadReader.hasNextLine()) {
                value = getValueFromConfigLine(loadReader.nextLine());
                if (value != -1) {
                    SPOIL_TIMES.put(PUFFERFISH, value);
                }
            }

            if (loadReader.hasNextLine()) {
                value = getValueFromConfigLine(loadReader.nextLine());
                if (value != -1) {
                    SPOIL_TIMES.put(MUSHROOM_STEW, value);
                }
            }

            if (loadReader.hasNextLine()) {
                value = getValueFromConfigLine(loadReader.nextLine());
                if (value != -1) {
                    SPOIL_TIMES.put(RABBIT_STEW, value);
                }
            }

            if (loadReader.hasNextLine()) {
                value = getValueFromConfigLine(loadReader.nextLine());
                if (value != -1) {
                    SPOIL_TIMES.put(BEETROOT_SOUP, value);
                }
            }

            if (loadReader.hasNextLine()) {
                value = getValueFromConfigLine(loadReader.nextLine());
                if (value != -1) {
                    SPOIL_TIMES.put(COOKED_BEEF, value);
                }
            }

            if (loadReader.hasNextLine()) {
                value = getValueFromConfigLine(loadReader.nextLine());
                if (value != -1) {
                    SPOIL_TIMES.put(COOKED_PORKCHOP, value);
                }

            }

            if (loadReader.hasNextLine()) {
                value = getValueFromConfigLine(loadReader.nextLine());
                if (value != -1) {
                    SPOIL_TIMES.put(COOKED_CHICKEN, value);
                }
            }

            if (loadReader.hasNextLine()) {
                value = getValueFromConfigLine(loadReader.nextLine());
                if (value != -1) {
                    SPOIL_TIMES.put(COOKED_SALMON, value);
                }
            }

            if (loadReader.hasNextLine()) {
                value = getValueFromConfigLine(loadReader.nextLine());
                if (value != -1) {
                    SPOIL_TIMES.put(COOKED_MUTTON, value);
                }
            }

            if (loadReader.hasNextLine()) {
                value = getValueFromConfigLine(loadReader.nextLine());
                if (value != -1) {
                    SPOIL_TIMES.put(COOKED_RABBIT, value);
                }
            }

            if (loadReader.hasNextLine()) {
                value = getValueFromConfigLine(loadReader.nextLine());
                if (value != -1) {
                    SPOIL_TIMES.put(COOKED_COD, value);
                }
            }

            if (loadReader.hasNextLine()) {
                value = getValueFromConfigLine(loadReader.nextLine());
                if (value != -1) {
                    SPOIL_TIMES.put(WHEAT, value);
                }
            }

            if (loadReader.hasNextLine()) {
                value = getValueFromConfigLine(loadReader.nextLine());
                if (value != -1) {
                    SPOIL_TIMES.put(MELON, value);
                }
            }

            if (loadReader.hasNextLine()) {
                value = getValueFromConfigLine(loadReader.nextLine());
                if (value != -1) {
                    SPOIL_TIMES.put(PUMPKIN, value);
                }
            }

            if (loadReader.hasNextLine()) {
                value = getValueFromConfigLine(loadReader.nextLine());
                if (value != -1) {
                    SPOIL_TIMES.put(BROWN_MUSHROOM, value);
                }
            }

            if (loadReader.hasNextLine()) {
                value = getValueFromConfigLine(loadReader.nextLine());
                if (value != -1) {
                    SPOIL_TIMES.put(RED_MUSHROOM, value);
                }
            }

            if (loadReader.hasNextLine()) {
                value = getValueFromConfigLine(loadReader.nextLine());
                if (value != -1) {
                    SPOIL_TIMES.put(NETHER_WART, value);
                }
            }

            if (loadReader.hasNextLine()) {
                value = getValueFromConfigLine(loadReader.nextLine());
                if (value != -1) {
                    SPOIL_TIMES.put(MELON, value);
                }
            }

            if (loadReader.hasNextLine()) {
                value = getValueFromConfigLine(loadReader.nextLine());
                if (value != -1) {
                    SPOIL_TIMES.put(CAKE, value);
                }
            }

            if (loadReader.hasNextLine()) {
                value = getValueFromConfigLine(loadReader.nextLine());
                if (value != -1) {
                    SPOIL_TIMES.put(PUMPKIN, value);
                }
            }

            if (loadReader.hasNextLine()) {
                value = getValueFromConfigLine(loadReader.nextLine());
                if (value != -1) {
                    SPOIL_TIMES.put(SUGAR, value);
                }
            }

            if (loadReader.hasNextLine()) {
                value = getValueFromConfigLine(loadReader.nextLine());
                if (value != -1) {
                    SPOIL_TIMES.put(EGG, value);
                }
            }

            if (loadReader.hasNextLine()) {
                value = getValueFromConfigLine(loadReader.nextLine());
                if (value != -1) {
                    SPOIL_TIMES.put(SUGAR_CANE, value);
                }
            }

            if (loadReader.hasNextLine()) {
                value = getValueFromConfigLine(loadReader.nextLine());
                if (value != -1) {
                    SPOIL_TIMES.put(APPLE, value);
                }
            }

            if (loadReader.hasNextLine()) {
                value = getValueFromConfigLine(loadReader.nextLine());
                if (value != -1) {
                    SPOIL_TIMES.put(COOKIE, value);
                }
            }

            if (loadReader.hasNextLine()) {
                value = getValueFromConfigLine(loadReader.nextLine());
                if (value != -1) {
                    SPOIL_TIMES.put(POISONOUS_POTATO, value);
                }
            }

            if (loadReader.hasNextLine()) {
                value = getValueFromConfigLine(loadReader.nextLine());
                if (value != -1) {
                    SPOIL_TIMES.put(CHORUS_FRUIT, value);
                }
            }

            if (loadReader.hasNextLine()) {
                value = getValueFromConfigLine(loadReader.nextLine());
                if (value != -1) {
                    SPOIL_TIMES.put(DRIED_KELP, value);
                }
            }

            if (loadReader.hasNextLine()) {
                value = getValueFromConfigLine(loadReader.nextLine());
                if (value != -1) {
                    SPOIL_TIMES.put(BAKED_POTATO, value);
                }
            }

            loadReader.close();
            System.out.println("Food spoilage times successfully loaded.");
        } catch (FileNotFoundException e) {
            System.out.println("There was a problem loading the food spoilage times!");
        }
    }

    public int getValueFromConfigLine(String line) {

        // format: "FoodType: (value) hours"

        int firstSpaceIndex = line.indexOf(" ");
        int lastSpaceIndex = line.lastIndexOf(" ");

        int value = 0;
        try {
            value = Integer.parseInt(line.substring(firstSpaceIndex + 1, lastSpaceIndex));
        } catch (Exception e) {
            System.out.println("Something went wrong when getting a value from a config line.");
            return -1;
        }

//        System.out.println("Got value " + value + " from the config line!");
        return value;
    }

    public boolean foodSpoilageFolderExists() {
        File saveFolder = new File("./plugins/Food-Spoilage/");
        if (saveFolder.exists()) {
            System.out.println("food-spoilage folder found!");
            return true;
        }
        System.out.println("food-spoilage folder not found!");
        return false;
    }

    public void legacyLoadCustomText() {
        try {
            System.out.println("Attempting to load food spoilage text...");
            File loadFile = new File("./plugins/Food-Spoilage/" + "food-spoilage-text.txt");
            Scanner loadReader = new Scanner(loadFile);

            // actual loading

            if (loadReader.hasNextLine()) {
                createdText = loadReader.nextLine();
            }
            if (loadReader.hasNextLine()) {
                expiryDateText = loadReader.nextLine();
            }
            if (loadReader.hasNextLine()) {
                valuesLoadedText = loadReader.nextLine();
            }
            if (loadReader.hasNextLine()) {
                noPermsText = loadReader.nextLine();
            }
            if (loadReader.hasNextLine()) {
                spoiledFoodName = loadReader.nextLine();
            }
            if (loadReader.hasNextLine()) {
                spoiledFoodLore = loadReader.nextLine();
            }


            loadReader.close();
            System.out.println("Food spoilage text successfully loaded.");
        } catch (FileNotFoundException e) {
            System.out.println("There was a problem loading the food spoilage text!");
        }
    }
}

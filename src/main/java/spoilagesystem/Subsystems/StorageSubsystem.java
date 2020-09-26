package spoilagesystem.Subsystems;

import org.bukkit.Material;
import spoilagesystem.Main;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import static org.bukkit.Material.*;

public class StorageSubsystem {

    Main main = null;

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

    public StorageSubsystem(Main plugin) {
        main = plugin;
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
        if (directoryToBeDeleted.getAbsolutePath().contains("config.yml")){
            return true;
        }
        return directoryToBeDeleted.delete();
    }

    public void handleVersionMismatch() {

        if (!main.getConfig().getString("version").equalsIgnoreCase(main.version)) {
            System.out.println("[ALERT] Verson mismatch! Saving old config as config.yml.old and loading in the default values.");
            renameConfigToConfigDotOldAndSaveDefaults();
        }

    }

    private void renameConfigToConfigDotOldAndSaveDefaults() {

        // save old config as config.yml.old
        File saveFile = new File("./plugins/FoodSpoilage/config.yml");
        if (saveFile.exists()) {

            // rename file
            File newSaveFile = new File("./plugins/FoodSpoilage/config.yml.old");
            saveFile.renameTo(newSaveFile);

            // save defaults
            saveConfigDefaults();

        }

    }

    public void saveConfigDefaults() {
        main.getConfig().addDefault("version", main.version);

        SPOIL_TIMES.forEach((key, value) -> main.getConfig().addDefault(key.toString(), value));

        main.getConfig().addDefault("createdText", createdText);
        main.getConfig().addDefault("expiryDateText", expiryDateText);
        main.getConfig().addDefault("valuesLoadedText", valuesLoadedText);
        main.getConfig().addDefault("noPermsText", noPermsText);
        main.getConfig().addDefault("spoiledFoodName", spoiledFoodName);
        main.getConfig().addDefault("spoiledFoodLore", spoiledFoodLore);

        main.getConfig().options().copyDefaults(true);
        main.saveConfig();
    }

    public void loadValuesFromConfig() {
        SPOIL_TIMES.put(BREAD, main.getConfig().getInt("Bread"));
        SPOIL_TIMES.put(POTATO, main.getConfig().getInt("Potato"));
        SPOIL_TIMES.put(CARROT, main.getConfig().getInt("Carrot"));
        SPOIL_TIMES.put(BEETROOT, main.getConfig().getInt("Beetroot"));
        SPOIL_TIMES.put(BEEF, main.getConfig().getInt("Beef"));
        SPOIL_TIMES.put(PORKCHOP, main.getConfig().getInt("Porkchop"));
        SPOIL_TIMES.put(CHICKEN, main.getConfig().getInt("Chicken"));
        SPOIL_TIMES.put(COD, main.getConfig().getInt("Cod"));
        SPOIL_TIMES.put(SALMON, main.getConfig().getInt("Salmon"));
        SPOIL_TIMES.put(MUTTON, main.getConfig().getInt("Mutton"));
        SPOIL_TIMES.put(RABBIT, main.getConfig().getInt("Rabbit"));
        SPOIL_TIMES.put(TROPICAL_FISH, main.getConfig().getInt("Tropical_Fish"));
        SPOIL_TIMES.put(PUFFERFISH, main.getConfig().getInt("Pufferfish"));
        SPOIL_TIMES.put(MUSHROOM_STEW, main.getConfig().getInt("Mushroom_Stew"));
        SPOIL_TIMES.put(RABBIT_STEW, main.getConfig().getInt("Rabbit_Stew"));
        SPOIL_TIMES.put(BEETROOT_SOUP, main.getConfig().getInt("Beetroot_Soup"));
        SPOIL_TIMES.put(COOKED_BEEF, main.getConfig().getInt("Cooked_Beef"));
        SPOIL_TIMES.put(COOKED_PORKCHOP, main.getConfig().getInt("Cooked_Porkchop"));
        SPOIL_TIMES.put(COOKED_CHICKEN, main.getConfig().getInt("Cooked_Chicken"));
        SPOIL_TIMES.put(COOKED_SALMON, main.getConfig().getInt("Cooked_Salmon"));
        SPOIL_TIMES.put(COOKED_MUTTON, main.getConfig().getInt("Cooked_Mutton"));
        SPOIL_TIMES.put(COOKED_RABBIT, main.getConfig().getInt("Cooked_Rabbit"));
        SPOIL_TIMES.put(COOKED_COD, main.getConfig().getInt("Cooked_Cod"));
        SPOIL_TIMES.put(WHEAT, main.getConfig().getInt("Wheat"));
        SPOIL_TIMES.put(MELON, main.getConfig().getInt("Melon"));
        SPOIL_TIMES.put(PUMPKIN, main.getConfig().getInt("Pumpkin"));
        SPOIL_TIMES.put(BROWN_MUSHROOM, main.getConfig().getInt("Brown_Mushroom"));
        SPOIL_TIMES.put(RED_MUSHROOM, main.getConfig().getInt("Red_Mushroom"));
        SPOIL_TIMES.put(NETHER_WART, main.getConfig().getInt("Nether_Wart"));
        SPOIL_TIMES.put(MELON_SLICE, main.getConfig().getInt("Melon_Slice"));
        SPOIL_TIMES.put(CAKE, main.getConfig().getInt("Cake"));
        SPOIL_TIMES.put(PUMPKIN_PIE, main.getConfig().getInt("Pumpkin_Pie"));
        SPOIL_TIMES.put(SUGAR, main.getConfig().getInt("Sugar"));
        SPOIL_TIMES.put(EGG, main.getConfig().getInt("Egg"));
        SPOIL_TIMES.put(SUGAR_CANE, main.getConfig().getInt("Sugar_Cane"));
        SPOIL_TIMES.put(APPLE, main.getConfig().getInt("Apple"));
        SPOIL_TIMES.put(COOKIE, main.getConfig().getInt("Cookie"));
        SPOIL_TIMES.put(POISONOUS_POTATO, main.getConfig().getInt("Poisonous_Potato"));
        SPOIL_TIMES.put(CHORUS_FRUIT, main.getConfig().getInt("Chorus_Fruit"));
        SPOIL_TIMES.put(DRIED_KELP, main.getConfig().getInt("Dried_Kelp"));
        SPOIL_TIMES.put(BAKED_POTATO, main.getConfig().getInt("Baked_Potato"));
        SPOIL_TIMES.put(SWEET_BERRIES, main.getConfig().getInt("Sweet_Berries"));

        createdText = main.getConfig().getString("createdText");
        expiryDateText = main.getConfig().getString("expiryDateText");
        valuesLoadedText = main.getConfig().getString("valuesLoadedText");
        noPermsText = main.getConfig().getString("noPermsText");
        spoiledFoodName = main.getConfig().getString("spoiledFoodName");
        spoiledFoodLore = main.getConfig().getString("spoiledFoodLore");
    }

    public void legacyLoadValuesFromConfig() {

        try {
            System.out.println("Attempting to load food spoilage times...");
            Scanner loadReader = new Scanner(new File("./plugins/Food-Spoilage/" + "food-spoilage-times.txt"));

            // actual loading

            // ignore version
            String temp1 = loadReader.nextLine();

            // ignore second line
            String temp2 = loadReader.nextLine();

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
        } catch(Exception e) {
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

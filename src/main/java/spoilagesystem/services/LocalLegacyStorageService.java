package spoilagesystem.services;

import org.bukkit.Material;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Scanner;

import static org.bukkit.Material.*;

public class LocalLegacyStorageService {

    private static LocalLegacyStorageService instance;

    private LocalLegacyStorageService() {

    }

    public static LocalLegacyStorageService getInstance() {
        if (instance == null) {
            instance = new LocalLegacyStorageService();
        }
        return instance;
    }

    public final HashMap<Material, Integer> spoilTimes = new HashMap<>();

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
                    spoilTimes.put(BREAD, value);
                }
            }

            if (loadReader.hasNextLine()) {
                value = getValueFromConfigLine(loadReader.nextLine()); // line 3
                if (value != -1) {
                    spoilTimes.put(POTATO, value);
                }
            }

            if (loadReader.hasNextLine()) {
                value = getValueFromConfigLine(loadReader.nextLine()); // line 4
                if (value != -1) {
                    spoilTimes.put(CARROT, value);
                }
            }

            if (loadReader.hasNextLine()) {
                value = getValueFromConfigLine(loadReader.nextLine()); // line 5
                if (value != -1) {
                    spoilTimes.put(BEETROOT, value);
                }
            }

            if (loadReader.hasNextLine()) {
                value = getValueFromConfigLine(loadReader.nextLine());
                if (value != -1) {
                    spoilTimes.put(BEEF, value);
                }
            }

            if (loadReader.hasNextLine()) {
                value = getValueFromConfigLine(loadReader.nextLine());
                if (value != -1) {
                    spoilTimes.put(PORKCHOP, value);
                }
            }

            if (loadReader.hasNextLine()) {
                value = getValueFromConfigLine(loadReader.nextLine());
                if (value != -1) {
                    spoilTimes.put(CHICKEN, value);
                }
            }

            if (loadReader.hasNextLine()) {
                value = getValueFromConfigLine(loadReader.nextLine());
                if (value != -1) {
                    spoilTimes.put(COD, value);
                }
            }

            if (loadReader.hasNextLine()) {
                value = getValueFromConfigLine(loadReader.nextLine());
                if (value != -1) {
                    spoilTimes.put(SALMON, value);
                }
            }

            if (loadReader.hasNextLine()) {
                value = getValueFromConfigLine(loadReader.nextLine());
                if (value != -1) {
                    spoilTimes.put(MUTTON, value);
                }
            }

            if (loadReader.hasNextLine()) {
                value = getValueFromConfigLine(loadReader.nextLine());
                if (value != -1) {
                    spoilTimes.put(RABBIT, value);
                }
            }

            if (loadReader.hasNextLine()) {
                value = getValueFromConfigLine(loadReader.nextLine());
                if (value != -1) {
                    spoilTimes.put(TROPICAL_FISH, value);
                }
            }

            if (loadReader.hasNextLine()) {
                value = getValueFromConfigLine(loadReader.nextLine());
                if (value != -1) {
                    spoilTimes.put(PUFFERFISH, value);
                }
            }

            if (loadReader.hasNextLine()) {
                value = getValueFromConfigLine(loadReader.nextLine());
                if (value != -1) {
                    spoilTimes.put(MUSHROOM_STEW, value);
                }
            }

            if (loadReader.hasNextLine()) {
                value = getValueFromConfigLine(loadReader.nextLine());
                if (value != -1) {
                    spoilTimes.put(RABBIT_STEW, value);
                }
            }

            if (loadReader.hasNextLine()) {
                value = getValueFromConfigLine(loadReader.nextLine());
                if (value != -1) {
                    spoilTimes.put(BEETROOT_SOUP, value);
                }
            }

            if (loadReader.hasNextLine()) {
                value = getValueFromConfigLine(loadReader.nextLine());
                if (value != -1) {
                    spoilTimes.put(COOKED_BEEF, value);
                }
            }

            if (loadReader.hasNextLine()) {
                value = getValueFromConfigLine(loadReader.nextLine());
                if (value != -1) {
                    spoilTimes.put(COOKED_PORKCHOP, value);
                }

            }

            if (loadReader.hasNextLine()) {
                value = getValueFromConfigLine(loadReader.nextLine());
                if (value != -1) {
                    spoilTimes.put(COOKED_CHICKEN, value);
                }
            }

            if (loadReader.hasNextLine()) {
                value = getValueFromConfigLine(loadReader.nextLine());
                if (value != -1) {
                    spoilTimes.put(COOKED_SALMON, value);
                }
            }

            if (loadReader.hasNextLine()) {
                value = getValueFromConfigLine(loadReader.nextLine());
                if (value != -1) {
                    spoilTimes.put(COOKED_MUTTON, value);
                }
            }

            if (loadReader.hasNextLine()) {
                value = getValueFromConfigLine(loadReader.nextLine());
                if (value != -1) {
                    spoilTimes.put(COOKED_RABBIT, value);
                }
            }

            if (loadReader.hasNextLine()) {
                value = getValueFromConfigLine(loadReader.nextLine());
                if (value != -1) {
                    spoilTimes.put(COOKED_COD, value);
                }
            }

            if (loadReader.hasNextLine()) {
                value = getValueFromConfigLine(loadReader.nextLine());
                if (value != -1) {
                    spoilTimes.put(WHEAT, value);
                }
            }

            if (loadReader.hasNextLine()) {
                value = getValueFromConfigLine(loadReader.nextLine());
                if (value != -1) {
                    spoilTimes.put(MELON, value);
                }
            }

            if (loadReader.hasNextLine()) {
                value = getValueFromConfigLine(loadReader.nextLine());
                if (value != -1) {
                    spoilTimes.put(PUMPKIN, value);
                }
            }

            if (loadReader.hasNextLine()) {
                value = getValueFromConfigLine(loadReader.nextLine());
                if (value != -1) {
                    spoilTimes.put(BROWN_MUSHROOM, value);
                }
            }

            if (loadReader.hasNextLine()) {
                value = getValueFromConfigLine(loadReader.nextLine());
                if (value != -1) {
                    spoilTimes.put(RED_MUSHROOM, value);
                }
            }

            if (loadReader.hasNextLine()) {
                value = getValueFromConfigLine(loadReader.nextLine());
                if (value != -1) {
                    spoilTimes.put(NETHER_WART, value);
                }
            }

            if (loadReader.hasNextLine()) {
                value = getValueFromConfigLine(loadReader.nextLine());
                if (value != -1) {
                    spoilTimes.put(MELON, value);
                }
            }

            if (loadReader.hasNextLine()) {
                value = getValueFromConfigLine(loadReader.nextLine());
                if (value != -1) {
                    spoilTimes.put(CAKE, value);
                }
            }

            if (loadReader.hasNextLine()) {
                value = getValueFromConfigLine(loadReader.nextLine());
                if (value != -1) {
                    spoilTimes.put(PUMPKIN, value);
                }
            }

            if (loadReader.hasNextLine()) {
                value = getValueFromConfigLine(loadReader.nextLine());
                if (value != -1) {
                    spoilTimes.put(SUGAR, value);
                }
            }

            if (loadReader.hasNextLine()) {
                value = getValueFromConfigLine(loadReader.nextLine());
                if (value != -1) {
                    spoilTimes.put(EGG, value);
                }
            }

            if (loadReader.hasNextLine()) {
                value = getValueFromConfigLine(loadReader.nextLine());
                if (value != -1) {
                    spoilTimes.put(SUGAR_CANE, value);
                }
            }

            if (loadReader.hasNextLine()) {
                value = getValueFromConfigLine(loadReader.nextLine());
                if (value != -1) {
                    spoilTimes.put(APPLE, value);
                }
            }

            if (loadReader.hasNextLine()) {
                value = getValueFromConfigLine(loadReader.nextLine());
                if (value != -1) {
                    spoilTimes.put(COOKIE, value);
                }
            }

            if (loadReader.hasNextLine()) {
                value = getValueFromConfigLine(loadReader.nextLine());
                if (value != -1) {
                    spoilTimes.put(POISONOUS_POTATO, value);
                }
            }

            if (loadReader.hasNextLine()) {
                value = getValueFromConfigLine(loadReader.nextLine());
                if (value != -1) {
                    spoilTimes.put(CHORUS_FRUIT, value);
                }
            }

            if (loadReader.hasNextLine()) {
                value = getValueFromConfigLine(loadReader.nextLine());
                if (value != -1) {
                    spoilTimes.put(DRIED_KELP, value);
                }
            }

            if (loadReader.hasNextLine()) {
                value = getValueFromConfigLine(loadReader.nextLine());
                if (value != -1) {
                    spoilTimes.put(BAKED_POTATO, value);
                }
            }

            loadReader.close();
            System.out.println("Food spoilage times successfully loaded.");
        } catch (FileNotFoundException e) {
            System.out.println("There was a problem loading the food spoilage times!");
        }
    }

    public void legacyLoadCustomText() {
        try {
            System.out.println("Attempting to load food spoilage text...");
            File loadFile = new File("./plugins/Food-Spoilage/" + "food-spoilage-text.txt");
            Scanner loadReader = new Scanner(loadFile);

            // actual loading

            if (loadReader.hasNextLine()) {
                LocalConfigService.getInstance().expiryDateText = loadReader.nextLine();
            }
            if (loadReader.hasNextLine()) {
                LocalConfigService.getInstance().valuesLoadedText = loadReader.nextLine();
            }
            if (loadReader.hasNextLine()) {
                LocalConfigService.getInstance().noPermsText = loadReader.nextLine();
            }
            if (loadReader.hasNextLine()) {
                LocalConfigService.getInstance().spoiledFoodName = loadReader.nextLine();
            }
            if (loadReader.hasNextLine()) {
                LocalConfigService.getInstance().spoiledFoodLore = loadReader.nextLine();
            }


            loadReader.close();
            System.out.println("Food spoilage text successfully loaded.");
        } catch (FileNotFoundException e) {
            System.out.println("There was a problem loading the food spoilage text!");
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
}
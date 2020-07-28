package spoilagesystem.Subsystems;

import org.bukkit.Material;
import spoilagesystem.Main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class StorageSubsystem {

    Main main = null;

    // values
    private int Bread = 24;
    private int Potato = 48;
    private int Carrot = 48;
    private int Beetroot = 48;
    private int Beef = 24;
    private int Porkchop = 24;
    private int Chicken = 24;
    private int Cod = 24;
    private int Salmon = 24;
    private int Mutton = 24;
    private int Rabbit = 24;
    private int Tropical_Fish = 24;
    private int Pufferfish = 24;
    private int Mushroom_Stew = 72;
    private int Rabbit_Stew = 96;
    private int Beetroot_Soup = 72;
    private int Cooked_Beef = 72;
    private int Cooked_Porkchop = 72;
    private int Cooked_Chicken = 72;
    private int Cooked_Salmon = 72;
    private int Cooked_Mutton = 72;
    private int Cooked_Rabbit = 72;
    private int Cooked_Cod = 72;
    private int Wheat = 48;
    private int Melon = 48;
    private int Pumpkin = 48;
    private int Brown_Mushroom = 48;
    private int Red_Mushroom = 48;
    private int Nether_Wart = 168;
    private int Melon_Slice = 24;
    private int Cake = 24;
    private int Pumpkin_Pie = 24;
    private int Sugar = 72;
    private int Egg = 72;
    private int Sugar_Cane = 48;
    private int Apple = 48;
    private int Cookie = 94;
    private int Poisonous_Potato = 24;
    private int Chorus_Fruit = 94;
    private int Dried_Kelp = 72;
    private int Baked_Potato = 94;

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
        switch(type) {
            case BREAD:
                return Bread;
            case POTATO:
                return Potato;
            case CARROT:
                return Carrot;
            case BEETROOT:
                return Beetroot;
            case BEEF:
                return Beef;
            case PORKCHOP:
                return Porkchop;
            case CHICKEN:
                return Chicken;
            case COD:
                return Cod;
            case SALMON:
                return Salmon;
            case MUTTON:
                return Mutton;
            case RABBIT:
                return Rabbit;
            case TROPICAL_FISH:
                return Tropical_Fish;
            case PUFFERFISH:
                return Pufferfish;
            case MUSHROOM_STEW:
                return Mushroom_Stew;
            case RABBIT_STEW:
                return Rabbit_Stew;
            case BEETROOT_SOUP:
                return Beetroot_Soup;
            case COOKED_BEEF:
                return Cooked_Beef;
            case COOKED_PORKCHOP:
                return Cooked_Porkchop;
            case COOKED_CHICKEN:
                return Cooked_Chicken;
            case COOKED_SALMON:
                return Cooked_Salmon;
            case COOKED_MUTTON:
                return Cooked_Mutton;
            case COOKED_COD:
                return Cooked_Cod;
            case WHEAT:
                return Wheat;
            case MELON:
                return Melon;
            case PUMPKIN:
                return Pumpkin;
            case BROWN_MUSHROOM:
                return Brown_Mushroom;
            case RED_MUSHROOM:
                return Red_Mushroom;
            case NETHER_WART:
                return Nether_Wart;
            case MELON_SLICE:
                return Melon_Slice;
            case CAKE:
                return Cake;
            case PUMPKIN_PIE:
                return Pumpkin_Pie;
            case SUGAR:
                return Sugar;
            case EGG:
                return Egg;
            case SUGAR_CANE:
                return Sugar_Cane;
            case APPLE:
                return Apple;
            case COOKIE:
                return Cookie;
            case POISONOUS_POTATO:
                return Poisonous_Potato;
            case CHORUS_FRUIT:
                return Chorus_Fruit;
            case DRIED_KELP:
                return Dried_Kelp;
            case BAKED_POTATO:
                return Baked_Potato;
        }
        return 0;
    }

    public void ensureSmoothTransitionBetweenVersions() {
        File saveFolder = new File("./plugins/Food-Spoilage/");

        // if old save files present
        if (saveFolder.exists()) {
            System.out.println("[ALERT] Old save folder name (pre v1.9) detected. Updating for compatibility.");
            // legacy load
            legacyLoadValuesFromConfig();
            legacyLoadCustomText();

            // rename directory
            File newSaveFolder = new File("./plugins/MedievalFactions/");
            saveFolder.renameTo(newSaveFolder);

            // save config
            saveConfigDefaults();
        }
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
        main.getConfig().addDefault("Bread", Bread);
        main.getConfig().addDefault("Potato", Potato);
        main.getConfig().addDefault("Carrot", Carrot);
        main.getConfig().addDefault("Beetroot", Beetroot);
        main.getConfig().addDefault("Beef", Beef);
        main.getConfig().addDefault("Porkchop", Porkchop);
        main.getConfig().addDefault("Chicken", Chicken);
        main.getConfig().addDefault("Cod", Cod);
        main.getConfig().addDefault("Salmon", Salmon);
        main.getConfig().addDefault("Mutton", Mutton);
        main.getConfig().addDefault("Rabbit", Rabbit);
        main.getConfig().addDefault("Tropical_Fish", Tropical_Fish);
        main.getConfig().addDefault("Pufferfish", Pufferfish);
        main.getConfig().addDefault("Mushroom_Stew", Mushroom_Stew);
        main.getConfig().addDefault("Rabbit_Stew", Rabbit_Stew);
        main.getConfig().addDefault("Beetroot_Soup", Beetroot_Soup);
        main.getConfig().addDefault("Cooked_Beef", Cooked_Beef);
        main.getConfig().addDefault("Cooked_Porkchop", Cooked_Porkchop);
        main.getConfig().addDefault("Cooked_Chicken", Cooked_Chicken);
        main.getConfig().addDefault("Cooked_Salmon", Cooked_Salmon);
        main.getConfig().addDefault("Cooked_Mutton", Cooked_Mutton);
        main.getConfig().addDefault("Cooked_Rabbit", Cooked_Rabbit);
        main.getConfig().addDefault("Cooked_Cod", Cooked_Cod);
        main.getConfig().addDefault("Wheat", Wheat);
        main.getConfig().addDefault("Melon", Melon);
        main.getConfig().addDefault("Pumpkin", Pumpkin);
        main.getConfig().addDefault("Brown_Mushroom", Brown_Mushroom);
        main.getConfig().addDefault("Red_Mushroom", Red_Mushroom);
        main.getConfig().addDefault("Nether_Wart", Nether_Wart);
        main.getConfig().addDefault("Melon_Slice", Melon_Slice);
        main.getConfig().addDefault("Cake", Cake);
        main.getConfig().addDefault("Pumpkin_Pie", Pumpkin_Pie);
        main.getConfig().addDefault("Sugar", Sugar);
        main.getConfig().addDefault("Egg", Egg);
        main.getConfig().addDefault("Sugar_Cane", Sugar_Cane);
        main.getConfig().addDefault("Apple", Apple);
        main.getConfig().addDefault("Cookie", Cookie);
        main.getConfig().addDefault("Poisonous_Potato", Poisonous_Potato);
        main.getConfig().addDefault("Chorus_Fruit", Chorus_Fruit);
        main.getConfig().addDefault("Dried_Kelp", Dried_Kelp);
        main.getConfig().addDefault("Baked_Potato", Baked_Potato);
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
        Bread = main.getConfig().getInt("Bread");
        Potato = main.getConfig().getInt("Potato");
        Carrot = main.getConfig().getInt("Carrot");
        Beetroot = main.getConfig().getInt("Beetroot");
        Beef = main.getConfig().getInt("Beef");
        Porkchop = main.getConfig().getInt("Porkchop");
        Chicken = main.getConfig().getInt("Porkchop");
        Cod = main.getConfig().getInt("Cod");
        Salmon = main.getConfig().getInt("Salmon");
        Mutton = main.getConfig().getInt("Mutton");
        Rabbit = main.getConfig().getInt("Rabbit");
        Tropical_Fish = main.getConfig().getInt("Tropical_Fish");
        Pufferfish = main.getConfig().getInt("Pufferfish");
        Mushroom_Stew = main.getConfig().getInt("Mushroom_Stew");
        Rabbit_Stew = main.getConfig().getInt("Rabbit_Stew");
        Beetroot_Soup = main.getConfig().getInt("Beetroot_Soup");
        Cooked_Beef = main.getConfig().getInt("Cooked_Beef");
        Cooked_Porkchop = main.getConfig().getInt("Cooked_Porkchop");
        Cooked_Chicken = main.getConfig().getInt("Cooked_Chicken");
        Cooked_Salmon = main.getConfig().getInt("Cooked_Salmon");
        Cooked_Mutton = main.getConfig().getInt("Cooked_Mutton");
        Cooked_Rabbit = main.getConfig().getInt("Cooked_Rabbit");
        Cooked_Cod = main.getConfig().getInt("Cooked_Cod");
        Wheat = main.getConfig().getInt("Wheat");
        Melon = main.getConfig().getInt("Melon");
        Pumpkin = main.getConfig().getInt("Pumpkin");
        Brown_Mushroom = main.getConfig().getInt("Brown_Mushroom");
        Red_Mushroom = main.getConfig().getInt("Red_Mushroom");
        Nether_Wart = main.getConfig().getInt("Nether_Wart");
        Melon_Slice = main.getConfig().getInt("Melon_Slice");
        Cake = main.getConfig().getInt("Cake");
        Pumpkin_Pie = main.getConfig().getInt("Pumpkin_Pie");
        Sugar = main.getConfig().getInt("Sugar");
        Egg = main.getConfig().getInt("Egg");
        Sugar_Cane = main.getConfig().getInt("Sugar_Cane");
        Apple = main.getConfig().getInt("Apple");
        Cookie = main.getConfig().getInt("Cookie");
        Poisonous_Potato = main.getConfig().getInt("Poisonous_Potato");
        Chorus_Fruit = main.getConfig().getInt("Chorus_Fruit");
        Dried_Kelp = main.getConfig().getInt("Dried_Kelp");
        Baked_Potato = main.getConfig().getInt("Baked_Potato");
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
            File loadFile = new File("./plugins/Food-Spoilage/" + "food-spoilage-times.txt");
            Scanner loadReader = new Scanner(loadFile);

            // actual loading

            // check version
            if (!loadReader.nextLine().equalsIgnoreCase(main.version)) {
                System.out.println("[ALERT] Mismatched version found in './plugins/Food-Spoilage/food-spoilage-times.txt'!");
                System.out.println("[ALERT] Values will not be loaded. Defaults will be used.");
                System.out.println("[ALERT] To fix this, please delete the following folder: './plugins/Food-Spoilage/'");
                return;
            }

            // ignore second line
            String temp = loadReader.nextLine();

            int value = -1;
            if (loadReader.hasNextLine()) {
                // get value from each config line and set it to corresponding field
                value = getValueFromConfigLine(loadReader.nextLine()); // line 2
                if (value != -1) {
                    Bread = value;
                }
            }

            if (loadReader.hasNextLine()) {
                value = getValueFromConfigLine(loadReader.nextLine()); // line 3
                if (value != -1) {
                    Potato = value;
                }
            }

            if (loadReader.hasNextLine()) {
                value = getValueFromConfigLine(loadReader.nextLine()); // line 4
                if (value != -1) {
                    Carrot = value;
                }
            }

            if (loadReader.hasNextLine()) {
                value = getValueFromConfigLine(loadReader.nextLine()); // line 5
                if (value != -1) {
                    Beetroot = value;
                }
            }

            if (loadReader.hasNextLine()) {
                value = getValueFromConfigLine(loadReader.nextLine());
                if (value != -1) {
                    Beef = value;
                }
            }

            if (loadReader.hasNextLine()) {
                value = getValueFromConfigLine(loadReader.nextLine());
                if (value != -1) {
                    Porkchop = value;
                }
            }

            if (loadReader.hasNextLine()) {
                value = getValueFromConfigLine(loadReader.nextLine());
                if (value != -1) {
                    Chicken = value;
                }
            }

            if (loadReader.hasNextLine()) {
                value = getValueFromConfigLine(loadReader.nextLine());
                if (value != -1) {
                    Cod = value;
                }
            }

            if (loadReader.hasNextLine()) {
                value = getValueFromConfigLine(loadReader.nextLine());
                if (value != -1) {
                    Salmon = value;
                }
            }

            if (loadReader.hasNextLine()) {
                value = getValueFromConfigLine(loadReader.nextLine());
                if (value != -1) {
                    Mutton = value;
                }
            }

            if (loadReader.hasNextLine()) {
                value = getValueFromConfigLine(loadReader.nextLine());
                if (value != -1) {
                    Rabbit = value;
                }
            }

            if (loadReader.hasNextLine()) {
                value = getValueFromConfigLine(loadReader.nextLine());
                if (value != -1) {
                    Tropical_Fish = value;
                }
            }

            if (loadReader.hasNextLine()) {
                value = getValueFromConfigLine(loadReader.nextLine());
                if (value != -1) {
                    Pufferfish = value;
                }
            }

            if (loadReader.hasNextLine()) {
                value = getValueFromConfigLine(loadReader.nextLine());
                if (value != -1) {
                    Mushroom_Stew = value;
                }
            }

            if (loadReader.hasNextLine()) {
                value = getValueFromConfigLine(loadReader.nextLine());
                if (value != -1) {
                    Rabbit_Stew = value;
                }
            }

            if (loadReader.hasNextLine()) {
                value = getValueFromConfigLine(loadReader.nextLine());
                if (value != -1) {
                    Beetroot_Soup = value;
                }
            }

            if (loadReader.hasNextLine()) {
                value = getValueFromConfigLine(loadReader.nextLine());
                if (value != -1) {
                    Cooked_Beef = value;
                }
            }

            if (loadReader.hasNextLine()) {
                value = getValueFromConfigLine(loadReader.nextLine());
                if (value != -1) {
                    Cooked_Porkchop = value;
                }

            }

            if (loadReader.hasNextLine()) {
                value = getValueFromConfigLine(loadReader.nextLine());
                if (value != -1) {
                    Cooked_Chicken = value;
                }
            }

            if (loadReader.hasNextLine()) {
                value = getValueFromConfigLine(loadReader.nextLine());
                if (value != -1) {
                    Cooked_Salmon = value;
                }
            }

            if (loadReader.hasNextLine()) {
                value = getValueFromConfigLine(loadReader.nextLine());
                if (value != -1) {
                    Cooked_Mutton = value;
                }
            }

            if (loadReader.hasNextLine()) {
                value = getValueFromConfigLine(loadReader.nextLine());
                if (value != -1) {
                    Cooked_Rabbit = value;
                }
            }

            if (loadReader.hasNextLine()) {
                value = getValueFromConfigLine(loadReader.nextLine());
                if (value != -1) {
                    Cooked_Cod = value;
                }
            }

            if (loadReader.hasNextLine()) {
                value = getValueFromConfigLine(loadReader.nextLine());
                if (value != -1) {
                    Wheat = value;
                }
            }

            if (loadReader.hasNextLine()) {
                value = getValueFromConfigLine(loadReader.nextLine());
                if (value != -1) {
                    Melon = value;
                }
            }

            if (loadReader.hasNextLine()) {
                value = getValueFromConfigLine(loadReader.nextLine());
                if (value != -1) {
                    Pumpkin = value;
                }
            }

            if (loadReader.hasNextLine()) {
                value = getValueFromConfigLine(loadReader.nextLine());
                if (value != -1) {
                    Brown_Mushroom = value;
                }
            }

            if (loadReader.hasNextLine()) {
                value = getValueFromConfigLine(loadReader.nextLine());
                if (value != -1) {
                    Red_Mushroom = value;
                }
            }

            if (loadReader.hasNextLine()) {
                value = getValueFromConfigLine(loadReader.nextLine());
                if (value != -1) {
                    Nether_Wart = value;
                }
            }

            if (loadReader.hasNextLine()) {
                value = getValueFromConfigLine(loadReader.nextLine());
                if (value != -1) {
                    Melon_Slice = value;
                }
            }

            if (loadReader.hasNextLine()) {
                value = getValueFromConfigLine(loadReader.nextLine());
                if (value != -1) {
                    Cake = value;
                }
            }

            if (loadReader.hasNextLine()) {
                value = getValueFromConfigLine(loadReader.nextLine());
                if (value != -1) {
                    Pumpkin_Pie = value;
                }
            }

            if (loadReader.hasNextLine()) {
                value = getValueFromConfigLine(loadReader.nextLine());
                if (value != -1) {
                    Sugar = value;
                }
            }

            if (loadReader.hasNextLine()) {
                value = getValueFromConfigLine(loadReader.nextLine());
                if (value != -1) {
                    Egg = value;
                }
            }

            if (loadReader.hasNextLine()) {
                value = getValueFromConfigLine(loadReader.nextLine());
                if (value != -1) {
                    Sugar_Cane = value;
                }
            }

            if (loadReader.hasNextLine()) {
                value = getValueFromConfigLine(loadReader.nextLine());
                if (value != -1) {
                    Apple = value;
                }
            }

            if (loadReader.hasNextLine()) {
                value = getValueFromConfigLine(loadReader.nextLine());
                if (value != -1) {
                    Cookie = value;
                }
            }

            if (loadReader.hasNextLine()) {
                value = getValueFromConfigLine(loadReader.nextLine());
                if (value != -1) {
                    Poisonous_Potato = value;
                }
            }

            if (loadReader.hasNextLine()) {
                value = getValueFromConfigLine(loadReader.nextLine());
                if (value != -1) {
                    Chorus_Fruit = value;
                }
            }

            if (loadReader.hasNextLine()) {
                value = getValueFromConfigLine(loadReader.nextLine());
                if (value != -1) {
                    Dried_Kelp = value;
                }
            }

            if (loadReader.hasNextLine()) {
                value = getValueFromConfigLine(loadReader.nextLine());
                if (value != -1) {
                    Baked_Potato = value;
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

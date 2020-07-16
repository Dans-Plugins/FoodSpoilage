package spoilagesystem.Subsystems;

import spoilagesystem.Main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class StorageSubsystem {

    Main main = null;

    // values
    public int Bread = 24;
    public int Potato = 48;
    public int Carrot = 48;
    public int Beetroot = 48;
    public int Beef = 24;
    public int Porkchop = 24;
    public int Chicken = 24;
    public int Cod = 24;
    public int Salmon = 24;
    public int Mutton = 24;
    public int Rabbit = 24;
    public int Tropical_Fish = 24;
    public int Pufferfish = 24;
    public int Mushroom_Stew = 72;
    public int Rabbit_Stew = 96;
    public int Beetroot_Soup = 72;

    public StorageSubsystem(Main plugin) {
        main = plugin;
    }

    public void saveValuesToConfig() {
        try {
            File saveFolder = new File("./plugins/Food-Spoilage/");
            if (!saveFolder.exists()) {
                saveFolder.mkdir();
            }
            File saveFile = new File("./plugins/Food-Spoilage/" + "food-spoilage-times.txt");
            if (saveFile.createNewFile()) {
                System.out.println("Save file for food spoilage times created.");
            } else {
                System.out.println("Save file for food spoilage times already exists. Overwriting.");
            }

            FileWriter saveWriter = new FileWriter(saveFile);

            // actual saving takes place here
            saveWriter.write(main.version);
            saveWriter.write("== Food Spoilage Times ==\n");
            saveWriter.write("Bread: " + Bread + " hours\n");
            saveWriter.write("Potato: " + Potato + " hours\n");
            saveWriter.write("Carrot: " + Carrot + " hours\n");
            saveWriter.write("Beetroot: " + Beetroot + " hours\n");
            saveWriter.write("Beef: " + Beef + " hours\n");
            saveWriter.write("Porkchop: " + Porkchop + " hours\n");
            saveWriter.write("Chicken: " + Chicken + " hours\n");
            saveWriter.write("Cod: " + Cod + " hours\n");
            saveWriter.write("Salmon: " + Salmon + " hours\n");
            saveWriter.write("Mutton: " + Mutton + " hours\n");
            saveWriter.write("Rabbit: " + Rabbit + " hours\n");
            saveWriter.write("Tropical_Fish: " + Tropical_Fish + " hours\n");
            saveWriter.write("Pufferfish: " + Pufferfish + " hours\n");
            saveWriter.write("Mushroom_Stew: " + Mushroom_Stew + " hours\n");
            saveWriter.write("Rabbit_Stew: " + Rabbit_Stew + " hours\n");
            saveWriter.write("Rabbit_Stew: " + Beetroot_Soup + " hours\n");

            saveWriter.close();

        } catch (IOException e) {
            System.out.println("An error occurred while saving food spoilage times.");
        }

    }

    public void loadValuesFromConfig() {

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
            // get value from each config line and set it to corresponding field
            value = getValueFromConfigLine(loadReader.nextLine()); // line 2
            if (value != -1) {
                Bread = value;
            }

            value = getValueFromConfigLine(loadReader.nextLine()); // line 3
            if (value != -1) {
                Potato = value;
            }

            value = getValueFromConfigLine(loadReader.nextLine()); // line 4
            if (value != -1) {
                Carrot = value;
            }

            value = getValueFromConfigLine(loadReader.nextLine()); // line 5
            if (value != -1) {
                Beetroot = value;
            }

            value = getValueFromConfigLine(loadReader.nextLine());
            if (value != -1) {
                Beef = value;
            }

            value = getValueFromConfigLine(loadReader.nextLine());
            if (value != -1) {
                Porkchop = value;
            }
            value = getValueFromConfigLine(loadReader.nextLine());
            if (value != -1) {
                Chicken = value;
            }

            value = getValueFromConfigLine(loadReader.nextLine());
            if (value != -1) {
                Cod = value;
            }

            value = getValueFromConfigLine(loadReader.nextLine());
            if (value != -1) {
                Salmon = value;
            }

            value = getValueFromConfigLine(loadReader.nextLine());
            if (value != -1) {
                Mutton = value;
            }

            value = getValueFromConfigLine(loadReader.nextLine());
            if (value != -1) {
                Rabbit = value;
            }

            value = getValueFromConfigLine(loadReader.nextLine());
            if (value != -1) {
                Tropical_Fish = value;
            }

            value = getValueFromConfigLine(loadReader.nextLine());
            if (value != -1) {
                Pufferfish = value;
            }

            value = getValueFromConfigLine(loadReader.nextLine());
            if (value != -1) {
                Mushroom_Stew = value;
            }

            value = getValueFromConfigLine(loadReader.nextLine());
            if (value != -1) {
                Rabbit_Stew = value;
            }

            value = getValueFromConfigLine(loadReader.nextLine());
            if (value != -1) {
                Beetroot_Soup = value;
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




}

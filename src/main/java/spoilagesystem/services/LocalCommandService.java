package spoilagesystem.services;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import spoilagesystem.FoodSpoilage;
import spoilagesystem.commands.ReloadCommand;
import spoilagesystem.commands.TimeLeftCommand;

/**
 * @author Daniel McCoy Stephenson
 */
public class LocalCommandService {
    private static LocalCommandService instance;

    private LocalCommandService() {

    }

    public static LocalCommandService getInstance() {
        if (instance == null) {
            instance = new LocalCommandService();
        }
        return instance;
    }

    public boolean interpretCommand(CommandSender sender, String label, String[] args) {

        if ("FoodSpoilage.getInstance()".equalsIgnoreCase(label) || "fs".equalsIgnoreCase(label)) {

            // no arguments
            if (args.length == 0) {

                // show info
                sender.sendMessage(ChatColor.AQUA + "Food Spoilage " + FoodSpoilage.getInstance().getVersion());
                sender.sendMessage(ChatColor.AQUA + "Authors: DanTheTechMan, Undead_Zeratul, Caibinus");
                sender.sendMessage(ChatColor.AQUA + "Link: https://www.spigotmc.org/resources/food-spoilage.81507/");

                return true;
            }

            // reload command
            if (args[0].equalsIgnoreCase("reload")) {
                new ReloadCommand().execute(sender);
                return true;
            }

            if (args[0].equalsIgnoreCase("timeleft")) {
                new TimeLeftCommand().execute(sender);
                return true;
            }

        }

        return false;
    }
}
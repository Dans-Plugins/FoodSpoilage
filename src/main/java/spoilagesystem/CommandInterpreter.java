package spoilagesystem;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import spoilagesystem.Commands.ReloadCommand;
import spoilagesystem.Commands.TimeLeftCommand;

public class CommandInterpreter {

    private static CommandInterpreter instance;

    private CommandInterpreter() {

    }

    public static CommandInterpreter getInstance() {
        if (instance == null) {
            instance = new CommandInterpreter();
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
                new ReloadCommand().reload(sender);
                return true;
            }

            if (args[0].equalsIgnoreCase("timeleft")) {
                new TimeLeftCommand().sendTimeLeft(sender);
                return true;
            }

        }

        return false;
    }
}

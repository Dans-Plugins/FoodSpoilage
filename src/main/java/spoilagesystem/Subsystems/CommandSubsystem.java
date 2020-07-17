package spoilagesystem.Subsystems;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import spoilagesystem.Commands.ReloadCommand;
import spoilagesystem.Main;

public class CommandSubsystem {

    Main main = null;

    public CommandSubsystem(Main plugin) {
        main = plugin;
    }

    public boolean interpretCommand(CommandSender sender, String label, String[] args) {

        if (label.equalsIgnoreCase("foodspoilage") || label.equalsIgnoreCase("fs")) {

            // no arguments
            if (args.length == 0) {

                // show info
                sender.sendMessage(ChatColor.AQUA + "Food Spoilage " + main.version);
                sender.sendMessage(ChatColor.AQUA + "Author: DanTheTechMan");
                sender.sendMessage(ChatColor.AQUA + "Link: https://www.spigotmc.org/resources/food-spoilage.81507/");

                return true;
            }

            // reload command
            if (args[0].equalsIgnoreCase("reload")) {
                ReloadCommand command = new ReloadCommand(main);
                command.reload(sender);
                return true;
            }

        }

        return false;
    }
}

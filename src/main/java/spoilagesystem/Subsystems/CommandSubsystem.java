package spoilagesystem.Subsystems;

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

            // reload command
            if (args[0].equalsIgnoreCase("reload")) {
                ReloadCommand command = new ReloadCommand(main);
                command.reload(sender);
            }

        }

        return false;
    }
}

package spoilagesystem.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import static org.bukkit.ChatColor.AQUA;
import static org.bukkit.ChatColor.RED;

/**
 * @author Daniel McCoy Stephenson
 */
public final class HelpCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("fs.help")) {
            sender.sendMessage(RED + "In order to use this command, you need one of the following permission: 'fs.help'");
            return true;
        }
        sender.sendMessage(new String[] {
                AQUA + "=== FoodSpoilage Commands ===",
                AQUA + "/fs help - View a list of helpful commands.",
                AQUA + "/fs timeleft - View how much time is left before an item expires.",
                AQUA + "/fs reload - Reload the plugin's configuration/save files."
        });
        return true;
    }
}
package spoilagesystem.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import preponderous.ponder.minecraft.bukkit.abs.AbstractPluginCommand;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author Daniel McCoy Stephenson
 */
public class HelpCommand extends AbstractPluginCommand {

    public HelpCommand() {
        super(new ArrayList<>(Arrays.asList("help")), new ArrayList<>(Arrays.asList("fs.help")));
    }

    @Override
    public boolean execute(CommandSender commandSender) {
        commandSender.sendMessage(ChatColor.AQUA + "=== FoodSpoilage Commands ===");
        commandSender.sendMessage(ChatColor.AQUA + "/fs help - View a list of helpful commands.");
        commandSender.sendMessage(ChatColor.AQUA + "/fs timeleft - View how much time is left before an item expires.");
        commandSender.sendMessage(ChatColor.AQUA + "/fs reload - Reload the plugin's configuration/save files.");
        return true;
    }

    @Override
    public boolean execute(CommandSender commandSender, String[] strings) {
        return execute(commandSender);
    }
}
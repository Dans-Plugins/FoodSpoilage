package spoilagesystem.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import preponderous.ponder.minecraft.bukkit.abs.AbstractPluginCommand;
import spoilagesystem.FoodSpoilage;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author Daniel McCoy Stephenson
 */
public class DefaultCommand extends AbstractPluginCommand {

    public DefaultCommand() {
        super(new ArrayList<>(Arrays.asList("default")), new ArrayList<>(Arrays.asList("fs.default")));
    }

    @Override
    public boolean execute(CommandSender commandSender) {
        commandSender.sendMessage(ChatColor.AQUA + "FoodSpoilage " + FoodSpoilage.getInstance().getVersion());
        commandSender.sendMessage(ChatColor.AQUA + "Developed by: Daniel McCoy Stephenson");
        commandSender.sendMessage(ChatColor.AQUA + "Wiki: https://github.com/Dans-Plugins/FoodSpoilage/wiki");
        return true;
    }

    @Override
    public boolean execute(CommandSender commandSender, String[] strings) {
        return execute(commandSender);
    }
}
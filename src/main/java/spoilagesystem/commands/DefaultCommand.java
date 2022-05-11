package spoilagesystem.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import preponderous.ponder.minecraft.bukkit.abs.AbstractPluginCommand;
import spoilagesystem.FoodSpoilage;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Daniel McCoy Stephenson
 */
public final class DefaultCommand extends AbstractPluginCommand {

    private final FoodSpoilage plugin;

    public DefaultCommand(FoodSpoilage plugin) {
        super(new ArrayList<>(List.of("default")), new ArrayList<>(List.of("fs.default")));
        this.plugin = plugin;
    }

    @Override
    public boolean execute(CommandSender commandSender) {
        commandSender.sendMessage(ChatColor.AQUA + plugin.getName() + " v" + plugin.getDescription().getVersion());
        commandSender.sendMessage(ChatColor.AQUA + "Developed by: Daniel McCoy Stephenson");
        commandSender.sendMessage(ChatColor.AQUA + "Wiki: https://github.com/Dans-Plugins/FoodSpoilage/wiki");
        return true;
    }

    @Override
    public boolean execute(CommandSender commandSender, String[] strings) {
        return execute(commandSender);
    }
}
package spoilagesystem.commands;

import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import preponderous.ponder.minecraft.bukkit.abs.AbstractPluginCommand;
import spoilagesystem.services.LocalConfigService;

/**
 * @author Daniel McCoy Stephenson
 */
public class ReloadCommand extends AbstractPluginCommand {

    public ReloadCommand() {
        super(new ArrayList<>(Arrays.asList("reload")), new ArrayList<>(Arrays.asList("fs.reload")));
    }

    @Override
    public boolean execute(CommandSender sender) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (player.hasPermission("fs.reload") || player.hasPermission("fs.admin")) {
                // ConfigManager.getInstance().reloadValuesFromConfig();
                LocalConfigService.getInstance().reload();
                player.sendMessage(ChatColor.GREEN + LocalConfigService.getInstance().valuesLoadedText);
                return true;
            }
            else {
                player.sendMessage(ChatColor.RED + LocalConfigService.getInstance().noPermsText);
                return false;
            }
        }
        else {
            // ConfigManager.getInstance().reloadValuesFromConfig();
            LocalConfigService.getInstance().reload();
            System.out.println(LocalConfigService.getInstance().valuesLoadedText);
            return true;
        }
    }

    @Override
    public boolean execute(CommandSender commandSender, String[] strings) {
        return execute(commandSender);
    }
}
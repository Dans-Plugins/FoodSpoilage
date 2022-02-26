package spoilagesystem.Commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import spoilagesystem.services.LocalConfigService;

public class ReloadCommand {

    public void reload(CommandSender sender) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (player.hasPermission("fs.reload") || player.hasPermission("fs.admin")) {
                // ConfigManager.getInstance().reloadValuesFromConfig();
                LocalConfigService.getInstance().reload();
                player.sendMessage(ChatColor.GREEN + LocalConfigService.getInstance().valuesLoadedText);
            }
            else {
                player.sendMessage(ChatColor.RED + LocalConfigService.getInstance().noPermsText);
            }
        }
        else {
            // ConfigManager.getInstance().reloadValuesFromConfig();
            LocalConfigService.getInstance().reload();
            System.out.println(LocalConfigService.getInstance().valuesLoadedText);
        }
    }

}

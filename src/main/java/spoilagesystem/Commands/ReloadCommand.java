package spoilagesystem.Commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import spoilagesystem.ConfigManager;

public class ReloadCommand {

    public void reload(CommandSender sender) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (player.hasPermission("fs.reload") || player.hasPermission("fs.admin")) {
                // ConfigManager.getInstance().reloadValuesFromConfig();
                ConfigManager.getInstance().reload();
                player.sendMessage(ChatColor.GREEN + ConfigManager.getInstance().valuesLoadedText);
            }
            else {
                player.sendMessage(ChatColor.RED + ConfigManager.getInstance().noPermsText);
            }
        }
        else {
            // ConfigManager.getInstance().reloadValuesFromConfig();
            ConfigManager.getInstance().reload();
            System.out.println(ConfigManager.getInstance().valuesLoadedText);
        }
    }

}

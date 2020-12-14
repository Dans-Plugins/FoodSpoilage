package spoilagesystem.Commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import spoilagesystem.FoodSpoilage;
import spoilagesystem.StorageManager;

public class ReloadCommand {

    public void reload(CommandSender sender) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (player.hasPermission("fs.reload") || player.hasPermission("fs.admin")) {
                StorageManager.getInstance().loadValuesFromConfig();
                player.sendMessage(ChatColor.GREEN + StorageManager.getInstance().valuesLoadedText);
            }
            else {
                player.sendMessage(ChatColor.RED + StorageManager.getInstance().noPermsText);
            }
        }
        else {
            StorageManager.getInstance().loadValuesFromConfig();
            System.out.println(StorageManager.getInstance().valuesLoadedText);
        }
    }

}

package spoilagesystem.Commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import spoilagesystem.FoodSpoilage;

public class ReloadCommand {

    public void reload(CommandSender sender) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (player.hasPermission("fs.reload") || player.hasPermission("fs.admin")) {
                FoodSpoilage.getInstance().storage.loadValuesFromConfig();
                player.sendMessage(ChatColor.GREEN + FoodSpoilage.getInstance().storage.valuesLoadedText);
            }
            else {
                player.sendMessage(ChatColor.RED + FoodSpoilage.getInstance().storage.noPermsText);
            }
        }
        else {
            FoodSpoilage.getInstance().storage.loadValuesFromConfig();
            System.out.println(FoodSpoilage.getInstance().storage.valuesLoadedText);
        }
    }

}

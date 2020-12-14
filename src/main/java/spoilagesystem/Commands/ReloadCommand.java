package spoilagesystem.Commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import spoilagesystem.FoodSpoilage;

public class ReloadCommand {

    FoodSpoilage foodSpoilage = null;

    public ReloadCommand(FoodSpoilage plugin) {
        foodSpoilage = plugin;
    }

    public void reload(CommandSender sender) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (player.hasPermission("fs.reload") || player.hasPermission("fs.admin")) {
                foodSpoilage.storage.loadValuesFromConfig();
                player.sendMessage(ChatColor.GREEN + foodSpoilage.storage.valuesLoadedText);
            }
            else {
                player.sendMessage(ChatColor.RED + foodSpoilage.storage.noPermsText);
            }
        }
        else {
            foodSpoilage.storage.loadValuesFromConfig();
            System.out.println(foodSpoilage.storage.valuesLoadedText);
        }
    }

}

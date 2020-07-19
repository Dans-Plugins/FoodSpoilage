package spoilagesystem.Commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import spoilagesystem.Main;

public class ReloadCommand {

    Main main = null;

    public ReloadCommand(Main plugin) {
        main = plugin;
    }

    public void reload(CommandSender sender) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (player.hasPermission("fs.reload") || player.hasPermission("fs.admin")) {
                main.storage.loadValuesFromConfig();
                main.storage.loadCustomText();
                player.sendMessage(ChatColor.GREEN + "" + main.storage.valuesLoadedText);
            }
            else {
                player.sendMessage(ChatColor.RED + "" + main.storage.noPermsText + ": 'fs.reload'");
            }
        }
        else {
            main.storage.loadValuesFromConfig();
            main.storage.loadCustomText();
            System.out.println("Values loaded!");
        }
    }

}

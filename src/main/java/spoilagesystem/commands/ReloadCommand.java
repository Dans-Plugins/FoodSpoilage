package spoilagesystem.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import spoilagesystem.FoodSpoilage;
import spoilagesystem.config.LocalConfigService;

import static org.bukkit.ChatColor.GREEN;
import static org.bukkit.ChatColor.RED;

/**
 * @author Daniel McCoy Stephenson
 */
public final class ReloadCommand implements CommandExecutor {

    private final FoodSpoilage plugin;
    private final LocalConfigService configService;

    public ReloadCommand(FoodSpoilage plugin, LocalConfigService configService) {
        this.plugin = plugin;
        this.configService = configService;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("fs.reload") && !sender.hasPermission("fs.admin")) {
            sender.sendMessage(RED + configService.getNoPermsReloadText());
            return false;
        }
        plugin.reloadConfig();
        sender.sendMessage(GREEN + configService.getValuesLoadedText());
        return true;
    }
}
package spoilagesystem.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import spoilagesystem.FoodSpoilage;
import spoilagesystem.config.LocalConfigService;

import static org.bukkit.ChatColor.AQUA;
import static org.bukkit.ChatColor.RED;

/**
 * @author Daniel McCoy Stephenson
 */
public final class DefaultCommand implements CommandExecutor {

    private final FoodSpoilage plugin;
    private final LocalConfigService configService;

    public DefaultCommand(FoodSpoilage plugin, LocalConfigService configService) {
        this.plugin = plugin;
        this.configService = configService;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("fs.default")) {
            sender.sendMessage(RED + configService.getNoPermsDefaultText());
            return true;
        }
        sender.sendMessage(new String[] {
                AQUA + plugin.getName() + " v" + plugin.getDescription().getVersion(),
                AQUA + "Developed by: Daniel McCoy Stephenson",
                AQUA + "Wiki: https://github.com/Dans-Plugins/FoodSpoilage/wiki"
        });
        return true;
    }

}
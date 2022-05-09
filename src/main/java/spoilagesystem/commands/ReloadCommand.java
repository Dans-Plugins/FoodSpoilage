package spoilagesystem.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import preponderous.ponder.minecraft.bukkit.abs.AbstractPluginCommand;
import spoilagesystem.FoodSpoilage;
import spoilagesystem.config.LocalConfigService;
import spoilagesystem.timestamp.LocalTimeStampService;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Daniel McCoy Stephenson
 */
public final class ReloadCommand extends AbstractPluginCommand {

    private final FoodSpoilage plugin;
    private final LocalConfigService configService;

    public ReloadCommand(FoodSpoilage plugin, LocalConfigService configService, LocalTimeStampService timeStampService) {
        super(new ArrayList<>(List.of("reload")), new ArrayList<>(List.of("fs.reload")));
        this.plugin = plugin;
        this.configService = configService;
    }

    @Override
    public boolean execute(CommandSender sender) {
        if (sender instanceof Player player) {
            if (player.hasPermission("fs.reload") || player.hasPermission("fs.admin")) {
                plugin.reloadConfig();
                player.sendMessage(ChatColor.GREEN + configService.getValuesLoadedText());
                return true;
            }
            else {
                player.sendMessage(ChatColor.RED + configService.getNoPermsReloadText());
                return false;
            }
        }
        else {
            plugin.reloadConfig();
            System.out.println(configService.getValuesLoadedText());
            return true;
        }
    }

    @Override
    public boolean execute(CommandSender commandSender, String[] strings) {
        return execute(commandSender);
    }
}
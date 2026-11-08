package spoilagesystem.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import spoilagesystem.config.LocalConfigService;

import static org.bukkit.ChatColor.RED;

/**
 * @author Daniel McCoy Stephenson
 */
public final class HelpCommand implements CommandExecutor {

    private final LocalConfigService configService;

    public HelpCommand(LocalConfigService configService) {
        this.configService = configService;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("fs.help")) {
            sender.sendMessage(RED + configService.getNoPermsHelpText());
            return true;
        }
        sender.sendMessage(configService.getHelpText().toArray(new String[0]));
        return true;
    }
}

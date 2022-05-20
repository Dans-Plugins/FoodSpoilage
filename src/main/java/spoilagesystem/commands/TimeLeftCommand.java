package spoilagesystem.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import spoilagesystem.config.LocalConfigService;
import spoilagesystem.timestamp.LocalTimeStampService;

import static org.bukkit.ChatColor.RED;

/**
 * @author Daniel McCoy Stephenson
 */
public final class TimeLeftCommand implements CommandExecutor {

    private final LocalConfigService configService;
    private final LocalTimeStampService timeStampService;

    public TimeLeftCommand(LocalConfigService configService, LocalTimeStampService timeStampService) {
        this.configService = configService;
        this.timeStampService = timeStampService;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("fs.timeleft")) {
            sender.sendMessage(RED + "In order to use this command, you need one of the following permission: 'fs.timeleft'");
            return true;
        }
        if (!(sender instanceof Player player)) {
            return false;
        }

        ItemStack item = player.getInventory().getItemInMainHand();
        String timeLeft = timeStampService.getTimeLeft(item);
        if (timeLeft == null) {
            // this item will never spoil
            player.sendMessage(configService.getNeverSpoilText());
            return true;
        }
        player.sendMessage(timeLeft);
        return true;
    }
}
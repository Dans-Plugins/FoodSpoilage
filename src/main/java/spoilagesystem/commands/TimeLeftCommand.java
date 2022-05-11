package spoilagesystem.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import preponderous.ponder.minecraft.bukkit.abs.AbstractPluginCommand;
import spoilagesystem.config.LocalConfigService;
import spoilagesystem.timestamp.LocalTimeStampService;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Daniel McCoy Stephenson
 */
public final class TimeLeftCommand extends AbstractPluginCommand {

    private final LocalConfigService configService;
    private final LocalTimeStampService timeStampService;

    public TimeLeftCommand(LocalConfigService configService, LocalTimeStampService timeStampService) {
        super(new ArrayList<>(List.of("timeleft")), new ArrayList<>(List.of("fs.timeleft")));
        this.configService = configService;
        this.timeStampService = timeStampService;
    }

    @Override
    public boolean execute(CommandSender sender) {
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

    @Override
    public boolean execute(CommandSender commandSender, String[] strings) {
        return execute(commandSender);
    }
}
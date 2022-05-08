package spoilagesystem.commands;

import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import preponderous.ponder.minecraft.bukkit.abs.AbstractPluginCommand;
import spoilagesystem.services.LocalConfigService;
import spoilagesystem.services.LocalTimeStampService;

/**
 * @author Daniel McCoy Stephenson
 */
public class TimeLeftCommand extends AbstractPluginCommand {

    public TimeLeftCommand() {
        super(new ArrayList<>(Arrays.asList("timeleft")), new ArrayList<>(Arrays.asList("fs.timeleft")));
    }

    @Override
    public boolean execute(CommandSender sender) {
        if (!(sender instanceof Player)) {
            return false;
        }

        Player player = (Player) sender;

        ItemStack item = player.getInventory().getItemInMainHand();

        String timeLeft = LocalTimeStampService.getInstance().getTimeLeft(item);

        if (timeLeft == null) {
            // this item will never spoil
            player.sendMessage(LocalConfigService.getInstance().neverSpoilText);
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
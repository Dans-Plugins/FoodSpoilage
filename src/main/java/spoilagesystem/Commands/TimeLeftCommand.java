package spoilagesystem.Commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import spoilagesystem.services.LocalConfigService;
import spoilagesystem.services.LocalTimeStampService;

public class TimeLeftCommand {

    public void sendTimeLeft(CommandSender sender) {
        if (!(sender instanceof Player)) {
            return;
        }

        Player player = (Player) sender;

        ItemStack item = player.getInventory().getItemInMainHand();

        String timeLeft = LocalTimeStampService.getInstance().getTimeLeft(item);

        if (timeLeft == null) {
            // this item will never spoil
            player.sendMessage(LocalConfigService.getInstance().neverSpoilText);
            return;
        }

        player.sendMessage(timeLeft);
    }
}

package spoilagesystem.Commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import spoilagesystem.ConfigManager;
import spoilagesystem.TimeStamper;

public class TimeLeftCommand {

    public void sendTimeLeft(CommandSender sender) {
        if (!(sender instanceof Player)) {
            return;
        }

        Player player = (Player) sender;

        ItemStack item = player.getInventory().getItemInMainHand();

        String timeLeft = TimeStamper.getInstance().getTimeLeft(item);

        if (timeLeft == null) {
            // this item will never spoil
            player.sendMessage(ConfigManager.getInstance().thisItemWillNeverSpoilText);
            return;
        }

        player.sendMessage(String.format(ConfigManager.getInstance().timeLeftText, timeLeft));
    }
}

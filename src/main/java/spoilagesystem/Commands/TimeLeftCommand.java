package spoilagesystem.Commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import spoilagesystem.TimeStamper;

public class TimeLeftCommand {

    public void sendTimeLeft(CommandSender sender) {
        if (!(sender instanceof Player)) {
            // must be a player to use this command
            // TODO: use config message here
            return;
        }

        Player player = (Player) sender;

        ItemStack item = player.getInventory().getItemInMainHand();

        String timeLeft = TimeStamper.getInstance().getTimeLeft(item);

        if (timeLeft == null) {
            // this item will never spoil
            // TODO: use config message here
            return;
        }

        player.sendMessage(String.format("This will expire in %s", timeLeft)); // TODO: abstract out into config message
    }
}

package spoilagesystem.EventHandlers;

import org.bukkit.Material;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import spoilagesystem.Main;

public class ItemSpawnEventHandler {

    Main main = null;

    public ItemSpawnEventHandler(Main plugin) {
        main = plugin;
    }

    public void handle(ItemSpawnEvent event) {

        // crops

        if (event.getEntity().getItemStack().getType() == Material.POTATO) {
            event.getEntity().setItemStack(main.timestamp.assignTimeStamp(event.getEntity().getItemStack(), 2)); // spoils in 6 vanilla MC days
        }

        if (event.getEntity().getItemStack().getType() == Material.CARROT) {
            event.getEntity().setItemStack(main.timestamp.assignTimeStamp(event.getEntity().getItemStack(), 4)); // spoils in 12 vanilla MC days
        }

        if (event.getEntity().getItemStack().getType() == Material.BEETROOT) {
            event.getEntity().setItemStack(main.timestamp.assignTimeStamp(event.getEntity().getItemStack(), 5)); // spoils in 15 vanilla MC days
        }

        // raw meats

        if (event.getEntity().getItemStack().getType() == Material.BEEF) {
            event.getEntity().setItemStack(main.timestamp.assignTimeStamp(event.getEntity().getItemStack(), 2)); // spoils in 6 vanilla MC days
        }

        if (event.getEntity().getItemStack().getType() == Material.PORKCHOP) {
            event.getEntity().setItemStack(main.timestamp.assignTimeStamp(event.getEntity().getItemStack(), 2)); // spoils in 6 vanilla MC days
        }

        if (event.getEntity().getItemStack().getType() == Material.CHICKEN) {
            event.getEntity().setItemStack(main.timestamp.assignTimeStamp(event.getEntity().getItemStack(), 2)); // spoils in 6 vanilla MC days
        }

        if (event.getEntity().getItemStack().getType() == Material.COD) {
            event.getEntity().setItemStack(main.timestamp.assignTimeStamp(event.getEntity().getItemStack(), 2)); // spoils in 6 vanilla MC days
        }

        if (event.getEntity().getItemStack().getType() == Material.SALMON) {
            event.getEntity().setItemStack(main.timestamp.assignTimeStamp(event.getEntity().getItemStack(), 2)); // spoils in 6 vanilla MC days
        }

        if (event.getEntity().getItemStack().getType() == Material.MUTTON) {
            event.getEntity().setItemStack(main.timestamp.assignTimeStamp(event.getEntity().getItemStack(), 2)); // spoils in 6 vanilla MC days
        }

        if (event.getEntity().getItemStack().getType() == Material.RABBIT) {
            event.getEntity().setItemStack(main.timestamp.assignTimeStamp(event.getEntity().getItemStack(), 2)); // spoils in 6 vanilla MC days
        }

        if (event.getEntity().getItemStack().getType() == Material.TROPICAL_FISH) {
            event.getEntity().setItemStack(main.timestamp.assignTimeStamp(event.getEntity().getItemStack(), 2)); // spoils in 6 vanilla MC days
        }

        if (event.getEntity().getItemStack().getType() == Material.PUFFERFISH) {
            event.getEntity().setItemStack(main.timestamp.assignTimeStamp(event.getEntity().getItemStack(), 2)); // spoils in 6 vanilla MC days
        }

    }

}

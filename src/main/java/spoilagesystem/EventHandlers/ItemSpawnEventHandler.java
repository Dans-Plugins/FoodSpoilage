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
            event.getEntity().setItemStack(main.timestamp.assignTimeStamp(event.getEntity().getItemStack(), main.storage.Potato));
        }

        if (event.getEntity().getItemStack().getType() == Material.CARROT) {
            event.getEntity().setItemStack(main.timestamp.assignTimeStamp(event.getEntity().getItemStack(), main.storage.Carrot));
        }

        if (event.getEntity().getItemStack().getType() == Material.BEETROOT) {
            event.getEntity().setItemStack(main.timestamp.assignTimeStamp(event.getEntity().getItemStack(), main.storage.Beetroot));
        }

        // raw meats

        if (event.getEntity().getItemStack().getType() == Material.BEEF) {
            event.getEntity().setItemStack(main.timestamp.assignTimeStamp(event.getEntity().getItemStack(), main.storage.Beef));
        }

        if (event.getEntity().getItemStack().getType() == Material.PORKCHOP) {
            event.getEntity().setItemStack(main.timestamp.assignTimeStamp(event.getEntity().getItemStack(), main.storage.Porkchop));
        }

        if (event.getEntity().getItemStack().getType() == Material.CHICKEN) {
            event.getEntity().setItemStack(main.timestamp.assignTimeStamp(event.getEntity().getItemStack(), main.storage.Chicken));
        }

        if (event.getEntity().getItemStack().getType() == Material.COD) {
            event.getEntity().setItemStack(main.timestamp.assignTimeStamp(event.getEntity().getItemStack(), main.storage.Cod));
        }

        if (event.getEntity().getItemStack().getType() == Material.SALMON) {
            event.getEntity().setItemStack(main.timestamp.assignTimeStamp(event.getEntity().getItemStack(), main.storage.Salmon));
        }

        if (event.getEntity().getItemStack().getType() == Material.MUTTON) {
            event.getEntity().setItemStack(main.timestamp.assignTimeStamp(event.getEntity().getItemStack(), main.storage.Mutton));
        }

        if (event.getEntity().getItemStack().getType() == Material.RABBIT) {
            event.getEntity().setItemStack(main.timestamp.assignTimeStamp(event.getEntity().getItemStack(), main.storage.Rabbit));
        }

        if (event.getEntity().getItemStack().getType() == Material.TROPICAL_FISH) {
            event.getEntity().setItemStack(main.timestamp.assignTimeStamp(event.getEntity().getItemStack(), main.storage.Tropical_Fish));
        }

        if (event.getEntity().getItemStack().getType() == Material.PUFFERFISH) {
            event.getEntity().setItemStack(main.timestamp.assignTimeStamp(event.getEntity().getItemStack(), main.storage.Pufferfish));
        }

    }

}

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

        if (event.getEntity().getItemStack().getType() == Material.WHEAT) {
            event.getEntity().setItemStack(main.timestamp.assignTimeStamp(event.getEntity().getItemStack(), main.storage.Wheat));
        }

        if (event.getEntity().getItemStack().getType() == Material.MELON) {
            event.getEntity().setItemStack(main.timestamp.assignTimeStamp(event.getEntity().getItemStack(), main.storage.Melon));
        }

        if (event.getEntity().getItemStack().getType() == Material.PUMPKIN) {
            event.getEntity().setItemStack(main.timestamp.assignTimeStamp(event.getEntity().getItemStack(), main.storage.Pumpkin));
        }

        if (event.getEntity().getItemStack().getType() == Material.BROWN_MUSHROOM) {
            event.getEntity().setItemStack(main.timestamp.assignTimeStamp(event.getEntity().getItemStack(), main.storage.Brown_Mushroom));
        }

        if (event.getEntity().getItemStack().getType() == Material.RED_MUSHROOM) {
            event.getEntity().setItemStack(main.timestamp.assignTimeStamp(event.getEntity().getItemStack(), main.storage.Red_Mushroom));
        }

        if (event.getEntity().getItemStack().getType() == Material.NETHER_WART) {
            event.getEntity().setItemStack(main.timestamp.assignTimeStamp(event.getEntity().getItemStack(), main.storage.Nether_Wart));
        }

        if (event.getEntity().getItemStack().getType() == Material.MELON_SLICE) {
            event.getEntity().setItemStack(main.timestamp.assignTimeStamp(event.getEntity().getItemStack(), main.storage.Melon_Slice));
        }

        if (event.getEntity().getItemStack().getType() == Material.EGG) {
            event.getEntity().setItemStack(main.timestamp.assignTimeStamp(event.getEntity().getItemStack(), main.storage.Egg));
        }

        if (event.getEntity().getItemStack().getType() == Material.SUGAR_CANE) {
            event.getEntity().setItemStack(main.timestamp.assignTimeStamp(event.getEntity().getItemStack(), main.storage.Sugar_Cane));
        }

        if (event.getEntity().getItemStack().getType() == Material.APPLE) {
            event.getEntity().setItemStack(main.timestamp.assignTimeStamp(event.getEntity().getItemStack(), main.storage.Apple));
        }

        if (event.getEntity().getItemStack().getType() == Material.POISONOUS_POTATO) {
            event.getEntity().setItemStack(main.timestamp.assignTimeStamp(event.getEntity().getItemStack(), main.storage.Poisonous_Potato));
        }

        if (event.getEntity().getItemStack().getType() == Material.CHORUS_FRUIT) {
            event.getEntity().setItemStack(main.timestamp.assignTimeStamp(event.getEntity().getItemStack(), main.storage.Chorus_Fruit));
        }

    }

}

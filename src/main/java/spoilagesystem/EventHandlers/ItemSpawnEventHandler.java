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

        // if timestamp not already assigned
        if (!main.timestamp.timeStampAssigned(event.getEntity().getItemStack())) {

            if (event.getEntity().getItemStack().getType() == Material.POTATO && main.storage.Potato != 0) {
                event.getEntity().setItemStack(main.timestamp.assignTimeStamp(event.getEntity().getItemStack(), main.storage.Potato));
            }

            if (event.getEntity().getItemStack().getType() == Material.CARROT && main.storage.Carrot != 0) {
                event.getEntity().setItemStack(main.timestamp.assignTimeStamp(event.getEntity().getItemStack(), main.storage.Carrot));
            }

            if (event.getEntity().getItemStack().getType() == Material.BEETROOT && main.storage.Beetroot != 0) {
                event.getEntity().setItemStack(main.timestamp.assignTimeStamp(event.getEntity().getItemStack(), main.storage.Beetroot));
            }

            // raw meats

            if (event.getEntity().getItemStack().getType() == Material.BEEF && main.storage.Beef != 0) {
                event.getEntity().setItemStack(main.timestamp.assignTimeStamp(event.getEntity().getItemStack(), main.storage.Beef));
            }

            if (event.getEntity().getItemStack().getType() == Material.PORKCHOP && main.storage.Porkchop != 0) {
                event.getEntity().setItemStack(main.timestamp.assignTimeStamp(event.getEntity().getItemStack(), main.storage.Porkchop));
            }

            if (event.getEntity().getItemStack().getType() == Material.CHICKEN && main.storage.Chicken != 0) {
                event.getEntity().setItemStack(main.timestamp.assignTimeStamp(event.getEntity().getItemStack(), main.storage.Chicken));
            }

            if (event.getEntity().getItemStack().getType() == Material.COD && main.storage.Cod != 0) {
                event.getEntity().setItemStack(main.timestamp.assignTimeStamp(event.getEntity().getItemStack(), main.storage.Cod));
            }

            if (event.getEntity().getItemStack().getType() == Material.SALMON && main.storage.Salmon != 0) {
                event.getEntity().setItemStack(main.timestamp.assignTimeStamp(event.getEntity().getItemStack(), main.storage.Salmon));
            }

            if (event.getEntity().getItemStack().getType() == Material.MUTTON && main.storage.Mutton != 0) {
                event.getEntity().setItemStack(main.timestamp.assignTimeStamp(event.getEntity().getItemStack(), main.storage.Mutton));
            }

            if (event.getEntity().getItemStack().getType() == Material.RABBIT && main.storage.Rabbit != 0) {
                event.getEntity().setItemStack(main.timestamp.assignTimeStamp(event.getEntity().getItemStack(), main.storage.Rabbit));
            }

            if (event.getEntity().getItemStack().getType() == Material.TROPICAL_FISH && main.storage.Tropical_Fish != 0) {
                event.getEntity().setItemStack(main.timestamp.assignTimeStamp(event.getEntity().getItemStack(), main.storage.Tropical_Fish));
            }

            if (event.getEntity().getItemStack().getType() == Material.PUFFERFISH && main.storage.Pufferfish != 0) {
                event.getEntity().setItemStack(main.timestamp.assignTimeStamp(event.getEntity().getItemStack(), main.storage.Pufferfish));
            }

            if (event.getEntity().getItemStack().getType() == Material.WHEAT && main.storage.Wheat != 0) {
                event.getEntity().setItemStack(main.timestamp.assignTimeStamp(event.getEntity().getItemStack(), main.storage.Wheat));
            }

            if (event.getEntity().getItemStack().getType() == Material.MELON && main.storage.Melon != 0) {
                event.getEntity().setItemStack(main.timestamp.assignTimeStamp(event.getEntity().getItemStack(), main.storage.Melon));
            }

            if (event.getEntity().getItemStack().getType() == Material.PUMPKIN && main.storage.Pumpkin != 0) {
                event.getEntity().setItemStack(main.timestamp.assignTimeStamp(event.getEntity().getItemStack(), main.storage.Pumpkin));
            }

            if (event.getEntity().getItemStack().getType() == Material.BROWN_MUSHROOM && main.storage.Brown_Mushroom != 0) {
                event.getEntity().setItemStack(main.timestamp.assignTimeStamp(event.getEntity().getItemStack(), main.storage.Brown_Mushroom));
            }

            if (event.getEntity().getItemStack().getType() == Material.RED_MUSHROOM && main.storage.Red_Mushroom != 0) {
                event.getEntity().setItemStack(main.timestamp.assignTimeStamp(event.getEntity().getItemStack(), main.storage.Red_Mushroom));
            }

            if (event.getEntity().getItemStack().getType() == Material.NETHER_WART && main.storage.Nether_Wart != 0) {
                event.getEntity().setItemStack(main.timestamp.assignTimeStamp(event.getEntity().getItemStack(), main.storage.Nether_Wart));
            }

            if (event.getEntity().getItemStack().getType() == Material.MELON_SLICE && main.storage.Melon_Slice != 0) {
                event.getEntity().setItemStack(main.timestamp.assignTimeStamp(event.getEntity().getItemStack(), main.storage.Melon_Slice));
            }

            if (event.getEntity().getItemStack().getType() == Material.EGG && main.storage.Egg != 0) {
                event.getEntity().setItemStack(main.timestamp.assignTimeStamp(event.getEntity().getItemStack(), main.storage.Egg));
            }

            if (event.getEntity().getItemStack().getType() == Material.SUGAR_CANE && main.storage.Sugar_Cane != 0) {
                event.getEntity().setItemStack(main.timestamp.assignTimeStamp(event.getEntity().getItemStack(), main.storage.Sugar_Cane));
            }

            if (event.getEntity().getItemStack().getType() == Material.APPLE && main.storage.Apple != 0) {
                event.getEntity().setItemStack(main.timestamp.assignTimeStamp(event.getEntity().getItemStack(), main.storage.Apple));
            }

            if (event.getEntity().getItemStack().getType() == Material.POISONOUS_POTATO && main.storage.Poisonous_Potato != 0) {
                event.getEntity().setItemStack(main.timestamp.assignTimeStamp(event.getEntity().getItemStack(), main.storage.Poisonous_Potato));
            }

            if (event.getEntity().getItemStack().getType() == Material.CHORUS_FRUIT && main.storage.Chorus_Fruit != 0) {
                event.getEntity().setItemStack(main.timestamp.assignTimeStamp(event.getEntity().getItemStack(), main.storage.Chorus_Fruit));
            }
        }

    }

}

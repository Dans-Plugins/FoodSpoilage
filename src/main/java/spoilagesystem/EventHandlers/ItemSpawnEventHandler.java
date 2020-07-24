package spoilagesystem.EventHandlers;

import org.bukkit.Material;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import spoilagesystem.Main;

import static org.bukkit.Material.*;
import static org.bukkit.Material.DRIED_KELP;

public class ItemSpawnEventHandler {

    Main main = null;

    public ItemSpawnEventHandler(Main plugin) {
        main = plugin;
    }

    public void handle(ItemSpawnEvent event) {

        ItemStack item = event.getEntity().getItemStack();
        Material type = item.getType();

        // if timestamp not already assigned
        if (!main.timestamp.timeStampAssigned(event.getEntity().getItemStack())) {

            if (type == Material.POTATO && main.storage.Potato != 0) {
                event.getEntity().setItemStack(main.timestamp.assignTimeStamp(event.getEntity().getItemStack(), main.storage.Potato));
            }

            if (type == Material.CARROT && main.storage.Carrot != 0) {
                event.getEntity().setItemStack(main.timestamp.assignTimeStamp(item, main.storage.Carrot));
            }

            if (type == Material.BEETROOT && main.storage.Beetroot != 0) {
                event.getEntity().setItemStack(main.timestamp.assignTimeStamp(item, main.storage.Beetroot));
            }

            if (type == Material.BEEF && main.storage.Beef != 0) {
                event.getEntity().setItemStack(main.timestamp.assignTimeStamp(item, main.storage.Beef));
            }

            if (type == Material.PORKCHOP && main.storage.Porkchop != 0) {
                event.getEntity().setItemStack(main.timestamp.assignTimeStamp(item, main.storage.Porkchop));
            }

            if (type == Material.CHICKEN && main.storage.Chicken != 0) {
                event.getEntity().setItemStack(main.timestamp.assignTimeStamp(item, main.storage.Chicken));
            }

            if (type == Material.COD && main.storage.Cod != 0) {
                event.getEntity().setItemStack(main.timestamp.assignTimeStamp(item, main.storage.Cod));
            }

            if (type == Material.SALMON && main.storage.Salmon != 0) {
                event.getEntity().setItemStack(main.timestamp.assignTimeStamp(item, main.storage.Salmon));
            }

            if (type == Material.MUTTON && main.storage.Mutton != 0) {
                event.getEntity().setItemStack(main.timestamp.assignTimeStamp(item, main.storage.Mutton));
            }

            if (type == Material.RABBIT && main.storage.Rabbit != 0) {
                event.getEntity().setItemStack(main.timestamp.assignTimeStamp(item, main.storage.Rabbit));
            }

            if (type == Material.TROPICAL_FISH && main.storage.Tropical_Fish != 0) {
                event.getEntity().setItemStack(main.timestamp.assignTimeStamp(item, main.storage.Tropical_Fish));
            }

            if (type == Material.PUFFERFISH && main.storage.Pufferfish != 0) {
                event.getEntity().setItemStack(main.timestamp.assignTimeStamp(item, main.storage.Pufferfish));
            }

            if (type == Material.WHEAT && main.storage.Wheat != 0) {
                event.getEntity().setItemStack(main.timestamp.assignTimeStamp(item, main.storage.Wheat));
            }

            if (type == Material.MELON && main.storage.Melon != 0) {
                event.getEntity().setItemStack(main.timestamp.assignTimeStamp(item, main.storage.Melon));
            }

            if (type == Material.PUMPKIN && main.storage.Pumpkin != 0) {
                event.getEntity().setItemStack(main.timestamp.assignTimeStamp(item, main.storage.Pumpkin));
            }

            if (type == Material.BROWN_MUSHROOM && main.storage.Brown_Mushroom != 0) {
                event.getEntity().setItemStack(main.timestamp.assignTimeStamp(item, main.storage.Brown_Mushroom));
            }

            if (type == Material.RED_MUSHROOM && main.storage.Red_Mushroom != 0) {
                event.getEntity().setItemStack(main.timestamp.assignTimeStamp(item, main.storage.Red_Mushroom));
            }

            if (type == Material.NETHER_WART && main.storage.Nether_Wart != 0) {
                event.getEntity().setItemStack(main.timestamp.assignTimeStamp(item, main.storage.Nether_Wart));
            }

            if (type == Material.MELON_SLICE && main.storage.Melon_Slice != 0) {
                event.getEntity().setItemStack(main.timestamp.assignTimeStamp(item, main.storage.Melon_Slice));
            }

            if (type == Material.EGG && main.storage.Egg != 0) {
                event.getEntity().setItemStack(main.timestamp.assignTimeStamp(item, main.storage.Egg));
            }

            if (type == Material.SUGAR_CANE && main.storage.Sugar_Cane != 0) {
                event.getEntity().setItemStack(main.timestamp.assignTimeStamp(item, main.storage.Sugar_Cane));
            }

            if (type == Material.APPLE && main.storage.Apple != 0) {
                event.getEntity().setItemStack(main.timestamp.assignTimeStamp(item, main.storage.Apple));
            }

            if (type == Material.POISONOUS_POTATO && main.storage.Poisonous_Potato != 0) {
                event.getEntity().setItemStack(main.timestamp.assignTimeStamp(item, main.storage.Poisonous_Potato));
            }

            if (type == Material.CHORUS_FRUIT && main.storage.Chorus_Fruit != 0) {
                event.getEntity().setItemStack(main.timestamp.assignTimeStamp(item, main.storage.Chorus_Fruit));
            }

            if (type == COOKED_BEEF && main.storage.getTime(COOKED_BEEF) != 0) {
                event.getEntity().setItemStack(main.timestamp.assignTimeStamp(item, main.storage.getTime(COOKED_BEEF)));
            }

            if (type == COOKED_PORKCHOP && main.storage.getTime(COOKED_PORKCHOP) != 0) {
                event.getEntity().setItemStack(main.timestamp.assignTimeStamp(item, main.storage.getTime(COOKED_PORKCHOP)));
            }

            if (type == COOKED_CHICKEN && main.storage.getTime(COOKED_CHICKEN) != 0) {
                event.getEntity().setItemStack(main.timestamp.assignTimeStamp(item, main.storage.getTime(COOKED_CHICKEN)));
            }

            if (type == Material.COOKED_SALMON && main.storage.getTime(COOKED_SALMON) != 0) {
                event.getEntity().setItemStack(main.timestamp.assignTimeStamp(item, main.storage.getTime(COOKED_SALMON)));
            }

            if (type == Material.COOKED_MUTTON && main.storage.getTime(COOKED_MUTTON) != 0) {
                event.getEntity().setItemStack(main.timestamp.assignTimeStamp(item, main.storage.getTime(COOKED_MUTTON)));
            }

            if (type == Material.COOKED_RABBIT && main.storage.getTime(COOKED_RABBIT) != 0) {
                event.getEntity().setItemStack(main.timestamp.assignTimeStamp(item, main.storage.getTime(COOKED_RABBIT)));
            }

            if (type == Material.COOKED_COD && main.storage.getTime(COOKED_COD) != 0) {
                event.getEntity().setItemStack(main.timestamp.assignTimeStamp(item, main.storage.getTime(COOKED_COD)));
            }

            if (type == Material.BAKED_POTATO && main.storage.getTime(BAKED_POTATO) != 0) {
                event.getEntity().setItemStack(main.timestamp.assignTimeStamp(item, main.storage.getTime(BAKED_POTATO)));
            }

            if (type == Material.DRIED_KELP && main.storage.getTime(DRIED_KELP) != 0) {
                event.getEntity().setItemStack(main.timestamp.assignTimeStamp(item, main.storage.getTime(DRIED_KELP)));
            }
        }

    }

}

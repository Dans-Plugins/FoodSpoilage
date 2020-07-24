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

            if (type == Material.POTATO && main.storage.getTime(POTATO) != 0) {
                event.getEntity().setItemStack(main.timestamp.assignTimeStamp(event.getEntity().getItemStack(), main.storage.getTime(POTATO)));
            }

            if (type == Material.CARROT && main.storage.getTime(CARROT) != 0) {
                event.getEntity().setItemStack(main.timestamp.assignTimeStamp(item, main.storage.getTime(CARROT)));
            }

            if (type == Material.BEETROOT && main.storage.getTime(BEETROOT) != 0) {
                event.getEntity().setItemStack(main.timestamp.assignTimeStamp(item, main.storage.getTime(BEETROOT)));
            }

            if (type == Material.BEEF && main.storage.getTime(BEEF) != 0) {
                event.getEntity().setItemStack(main.timestamp.assignTimeStamp(item, main.storage.getTime(BEEF)));
            }

            if (type == Material.PORKCHOP && main.storage.getTime(PORKCHOP) != 0) {
                event.getEntity().setItemStack(main.timestamp.assignTimeStamp(item, main.storage.getTime(PORKCHOP)));
            }

            if (type == Material.CHICKEN && main.storage.getTime(CHICKEN) != 0) {
                event.getEntity().setItemStack(main.timestamp.assignTimeStamp(item, main.storage.getTime(CHICKEN)));
            }

            if (type == Material.COD && main.storage.getTime(COD) != 0) {
                event.getEntity().setItemStack(main.timestamp.assignTimeStamp(item, main.storage.getTime(COD)));
            }

            if (type == Material.SALMON && main.storage.getTime(SALMON) != 0) {
                event.getEntity().setItemStack(main.timestamp.assignTimeStamp(item, main.storage.getTime(SALMON)));
            }

            if (type == Material.MUTTON && main.storage.getTime(MUTTON) != 0) {
                event.getEntity().setItemStack(main.timestamp.assignTimeStamp(item, main.storage.getTime(MUTTON)));
            }

            if (type == Material.RABBIT && main.storage.getTime(RABBIT) != 0) {
                event.getEntity().setItemStack(main.timestamp.assignTimeStamp(item, main.storage.getTime(RABBIT)));
            }

            if (type == Material.TROPICAL_FISH && main.storage.getTime(TROPICAL_FISH) != 0) {
                event.getEntity().setItemStack(main.timestamp.assignTimeStamp(item, main.storage.getTime(TROPICAL_FISH)));
            }

            if (type == Material.PUFFERFISH && main.storage.getTime(PUFFERFISH) != 0) {
                event.getEntity().setItemStack(main.timestamp.assignTimeStamp(item, main.storage.getTime(PUFFERFISH)));
            }

            if (type == Material.WHEAT && main.storage.getTime(WHEAT) != 0) {
                event.getEntity().setItemStack(main.timestamp.assignTimeStamp(item, main.storage.getTime(WHEAT)));
            }

            if (type == Material.MELON && main.storage.getTime(MELON) != 0) {
                event.getEntity().setItemStack(main.timestamp.assignTimeStamp(item, main.storage.getTime(MELON)));
            }

            if (type == Material.PUMPKIN && main.storage.getTime(PUMPKIN) != 0) {
                event.getEntity().setItemStack(main.timestamp.assignTimeStamp(item, main.storage.getTime(PUMPKIN)));
            }

            if (type == Material.BROWN_MUSHROOM && main.storage.getTime(BROWN_MUSHROOM) != 0) {
                event.getEntity().setItemStack(main.timestamp.assignTimeStamp(item, main.storage.getTime(BROWN_MUSHROOM)));
            }

            if (type == Material.RED_MUSHROOM && main.storage.getTime(RED_MUSHROOM) != 0) {
                event.getEntity().setItemStack(main.timestamp.assignTimeStamp(item, main.storage.getTime(RED_MUSHROOM)));
            }

            if (type == Material.NETHER_WART && main.storage.getTime(NETHER_WART) != 0) {
                event.getEntity().setItemStack(main.timestamp.assignTimeStamp(item, main.storage.getTime(NETHER_WART)));
            }

            if (type == Material.MELON_SLICE && main.storage.getTime(MELON_SLICE) != 0) {
                event.getEntity().setItemStack(main.timestamp.assignTimeStamp(item, main.storage.getTime(MELON_SLICE)));
            }

            if (type == Material.EGG && main.storage.getTime(EGG) != 0) {
                event.getEntity().setItemStack(main.timestamp.assignTimeStamp(item, main.storage.getTime(EGG)));
            }

            if (type == Material.SUGAR_CANE && main.storage.getTime(SUGAR_CANE) != 0) {
                event.getEntity().setItemStack(main.timestamp.assignTimeStamp(item, main.storage.getTime(SUGAR_CANE)));
            }

            if (type == Material.APPLE && main.storage.getTime(APPLE) != 0) {
                event.getEntity().setItemStack(main.timestamp.assignTimeStamp(item, main.storage.getTime(APPLE)));
            }

            if (type == Material.POISONOUS_POTATO && main.storage.getTime(POISONOUS_POTATO) != 0) {
                event.getEntity().setItemStack(main.timestamp.assignTimeStamp(item, main.storage.getTime(POISONOUS_POTATO)));
            }

            if (type == Material.CHORUS_FRUIT && main.storage.getTime(CHORUS_FRUIT) != 0) {
                event.getEntity().setItemStack(main.timestamp.assignTimeStamp(item, main.storage.getTime(CHORUS_FRUIT)));
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

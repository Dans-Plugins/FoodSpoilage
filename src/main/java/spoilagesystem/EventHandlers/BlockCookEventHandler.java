package spoilagesystem.EventHandlers;

import org.bukkit.Material;
import org.bukkit.event.block.BlockCookEvent;
import org.bukkit.inventory.ItemStack;
import spoilagesystem.Main;

public class BlockCookEventHandler {

    Main main = null;

    public BlockCookEventHandler(Main plugin) {
        main = plugin;
    }

    public void handle(BlockCookEvent event) {

        ItemStack item = event.getResult();
        Material type = item.getType();

        if (type == Material.COOKED_BEEF && main.storage.Cooked_Beef != 0) {
            event.setResult(main.timestamp.assignTimeStamp(item, main.storage.Cooked_Beef));
        }

        if (type == Material.COOKED_PORKCHOP && main.storage.Cooked_Porkchop != 0) {
            event.setResult(main.timestamp.assignTimeStamp(item, main.storage.Cooked_Porkchop));
        }

        if (type == Material.COOKED_CHICKEN && main.storage.Cooked_Chicken != 0) {
            event.setResult(main.timestamp.assignTimeStamp(item, main.storage.Cooked_Chicken));
        }

        if (type == Material.COOKED_SALMON && main.storage.Cooked_Salmon != 0) {
            event.setResult(main.timestamp.assignTimeStamp(item, main.storage.Cooked_Salmon));
        }

        if (type == Material.COOKED_MUTTON && main.storage.Cooked_Mutton != 0) {
            event.setResult(main.timestamp.assignTimeStamp(item, main.storage.Cooked_Mutton));
        }

        if (type == Material.COOKED_RABBIT && main.storage.Cooked_Rabbit != 0) {
            event.setResult(main.timestamp.assignTimeStamp(item, main.storage.Cooked_Rabbit));
        }

        if (type == Material.COOKED_COD && main.storage.Cooked_Cod != 0) {
            event.setResult(main.timestamp.assignTimeStamp(item, main.storage.Cooked_Cod));
        }

        if (type == Material.BAKED_POTATO && main.storage.Baked_Potato != 0) {
            event.setResult(main.timestamp.assignTimeStamp(item, main.storage.Baked_Potato));
        }

        if (type == Material.DRIED_KELP && main.storage.Dried_Kelp != 0) {
            event.setResult(main.timestamp.assignTimeStamp(item, main.storage.Dried_Kelp));
        }

    }

}

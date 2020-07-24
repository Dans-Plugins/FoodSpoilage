package spoilagesystem.EventHandlers;

import org.bukkit.Material;
import org.bukkit.event.block.BlockCookEvent;
import org.bukkit.inventory.ItemStack;
import spoilagesystem.Main;

import static org.bukkit.Material.*;

public class BlockCookEventHandler {

    Main main = null;

    public BlockCookEventHandler(Main plugin) {
        main = plugin;
    }

    public void handle(BlockCookEvent event) {

        ItemStack item = event.getResult();
        Material type = item.getType();

        if (type == COOKED_BEEF && main.storage.getTime(COOKED_BEEF) != 0) {
            event.setResult(main.timestamp.assignTimeStamp(item, main.storage.getTime(COOKED_BEEF)));
        }

        if (type == COOKED_PORKCHOP && main.storage.getTime(COOKED_PORKCHOP) != 0) {
            event.setResult(main.timestamp.assignTimeStamp(item, main.storage.getTime(COOKED_PORKCHOP)));
        }

        if (type == COOKED_CHICKEN && main.storage.getTime(COOKED_CHICKEN) != 0) {
            event.setResult(main.timestamp.assignTimeStamp(item, main.storage.getTime(COOKED_CHICKEN)));
        }

        if (type == Material.COOKED_SALMON && main.storage.getTime(COOKED_SALMON) != 0) {
            event.setResult(main.timestamp.assignTimeStamp(item, main.storage.getTime(COOKED_SALMON)));
        }

        if (type == Material.COOKED_MUTTON && main.storage.getTime(COOKED_MUTTON) != 0) {
            event.setResult(main.timestamp.assignTimeStamp(item, main.storage.getTime(COOKED_MUTTON)));
        }

        if (type == Material.COOKED_RABBIT && main.storage.getTime(COOKED_RABBIT) != 0) {
            event.setResult(main.timestamp.assignTimeStamp(item, main.storage.getTime(COOKED_RABBIT)));
        }

        if (type == Material.COOKED_COD && main.storage.getTime(COOKED_COD) != 0) {
            event.setResult(main.timestamp.assignTimeStamp(item, main.storage.getTime(COOKED_COD)));
        }

        if (type == Material.BAKED_POTATO && main.storage.getTime(BAKED_POTATO) != 0) {
            event.setResult(main.timestamp.assignTimeStamp(item, main.storage.getTime(BAKED_POTATO)));
        }

        if (type == Material.DRIED_KELP && main.storage.getTime(DRIED_KELP) != 0) {
            event.setResult(main.timestamp.assignTimeStamp(item, main.storage.getTime(DRIED_KELP)));
        }

    }

}

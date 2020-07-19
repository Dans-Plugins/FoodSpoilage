package spoilagesystem.EventHandlers;

import org.bukkit.Material;
import org.bukkit.event.inventory.FurnaceSmeltEvent;
import spoilagesystem.Main;

public class FurnaceSmeltEventHandler {

    Main main = null;

    public FurnaceSmeltEventHandler(Main plugin) {
        main = plugin;
    }

    public void handle(FurnaceSmeltEvent event) {

        if (event.getResult().getType() == Material.COOKED_BEEF && main.storage.Cooked_Beef != 0) {
            event.setResult(main.timestamp.assignTimeStamp(event.getResult(), main.storage.Cooked_Beef));
        }

        if (event.getResult().getType() == Material.COOKED_PORKCHOP && main.storage.Cooked_Porkchop != 0) {
            event.setResult(main.timestamp.assignTimeStamp(event.getResult(), main.storage.Cooked_Porkchop));
        }

        if (event.getResult().getType() == Material.COOKED_CHICKEN && main.storage.Cooked_Chicken != 0) {
            event.setResult(main.timestamp.assignTimeStamp(event.getResult(), main.storage.Cooked_Chicken));
        }

        if (event.getResult().getType() == Material.COOKED_SALMON && main.storage.Cooked_Salmon != 0) {
            event.setResult(main.timestamp.assignTimeStamp(event.getResult(), main.storage.Cooked_Salmon));
        }

        if (event.getResult().getType() == Material.COOKED_MUTTON && main.storage.Cooked_Mutton != 0) {
            event.setResult(main.timestamp.assignTimeStamp(event.getResult(), main.storage.Cooked_Mutton));
        }

        if (event.getResult().getType() == Material.COOKED_RABBIT && main.storage.Cooked_Rabbit != 0) {
            event.setResult(main.timestamp.assignTimeStamp(event.getResult(), main.storage.Cooked_Rabbit));
        }

        if (event.getResult().getType() == Material.COOKED_COD && main.storage.Cooked_Cod != 0) {
            event.setResult(main.timestamp.assignTimeStamp(event.getResult(), main.storage.Cooked_Cod));
        }

        if (event.getResult().getType() == Material.BAKED_POTATO && main.storage.Baked_Potato != 0) {
            event.setResult(main.timestamp.assignTimeStamp(event.getResult(), main.storage.Baked_Potato));
        }

        if (event.getResult().getType() == Material.DRIED_KELP && main.storage.Dried_Kelp != 0) {
            event.setResult(main.timestamp.assignTimeStamp(event.getResult(), main.storage.Dried_Kelp));
        }

    }

}

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

        if (event.getResult().getType() == Material.COOKED_BEEF) {
            event.setResult(main.timestamp.assignTimeStamp(event.getResult(), main.storage.Cooked_Beef));
        }

        if (event.getResult().getType() == Material.COOKED_PORKCHOP) {
            event.setResult(main.timestamp.assignTimeStamp(event.getResult(), main.storage.Cooked_Porkchop));
        }

        if (event.getResult().getType() == Material.COOKED_CHICKEN) {
            event.setResult(main.timestamp.assignTimeStamp(event.getResult(), main.storage.Cooked_Chicken));
        }

        if (event.getResult().getType() == Material.COOKED_SALMON) {
            event.setResult(main.timestamp.assignTimeStamp(event.getResult(), main.storage.Cooked_Salmon));
        }

        if (event.getResult().getType() == Material.COOKED_MUTTON) {
            event.setResult(main.timestamp.assignTimeStamp(event.getResult(), main.storage.Cooked_Mutton));
        }

        if (event.getResult().getType() == Material.COOKED_RABBIT) {
            event.setResult(main.timestamp.assignTimeStamp(event.getResult(), main.storage.Cooked_Rabbit));
        }

        if (event.getResult().getType() == Material.COOKED_COD) {
            event.setResult(main.timestamp.assignTimeStamp(event.getResult(), main.storage.Cooked_Cod));
        }

    }

}

package spoilagesystem;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.plugin.java.JavaPlugin;
import spoilagesystem.EventHandlers.CraftItemEventHandler;
import spoilagesystem.EventHandlers.InventoryClickEventHandler;
import spoilagesystem.Subsystems.TimeStampSubsystem;

public final class Main extends JavaPlugin implements Listener {

    public String version = "v0.3";

    // subsystems
    public TimeStampSubsystem timestamp = new TimeStampSubsystem(this);

    @Override
    public void onEnable() {
        this.getServer().getPluginManager().registerEvents(this, this);
    }

    @Override
    public void onDisable() {

    }

    @EventHandler()
    public void onCraft(CraftItemEvent event) {
        CraftItemEventHandler handler = new CraftItemEventHandler(this);
        handler.handle(event);
    }

    @EventHandler()
    public void onInventoryClick(InventoryClickEvent event) {
        InventoryClickEventHandler handler = new InventoryClickEventHandler(this);
        handler.handle(event);
    }
}

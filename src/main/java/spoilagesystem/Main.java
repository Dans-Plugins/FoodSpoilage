package spoilagesystem;

import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.plugin.java.JavaPlugin;
import spoilagesystem.EventHandlers.CraftItemEventHandler;
import spoilagesystem.Subsystems.TimeStampSubsystem;

public final class Main extends JavaPlugin implements Listener {

    public String version = "v0.1";

    // subsystems
    TimeStampSubsystem timestamp = new TimeStampSubsystem(this);

    @Override
    public void onEnable() {
        this.getServer().getPluginManager().registerEvents(this, this);
    }

    @Override
    public void onDisable() {

    }

    public void onCraft(CraftItemEvent event) {
        CraftItemEventHandler handler = new CraftItemEventHandler(this);
        handler.handle(event);
    }
}

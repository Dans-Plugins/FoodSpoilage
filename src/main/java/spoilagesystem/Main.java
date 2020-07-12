package spoilagesystem;

import org.bukkit.plugin.java.JavaPlugin;
import spoilagesystem.Subsystems.TimeStampSubsystem;

public final class Main extends JavaPlugin {

    public String version = "v0.1";

    // subsystems
    TimeStampSubsystem timestamp = new TimeStampSubsystem(this);

    @Override
    public void onEnable() {

    }

    @Override
    public void onDisable() {

    }
}

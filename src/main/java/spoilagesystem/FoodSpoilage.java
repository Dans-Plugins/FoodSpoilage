package spoilagesystem;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import spoilagesystem.bStats.Metrics;

import java.io.File;

public final class FoodSpoilage extends JavaPlugin {

    private static FoodSpoilage instance;

    private String version = "v1.10";

    public static FoodSpoilage getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;

        ConfigManager.getInstance().ensureSmoothTransitionBetweenVersions();

        // config creation/loading
        if (!(new File("./plugins/FoodSpoilage/config.yml").exists())) {
            ConfigManager.getInstance().saveConfigDefaults();
        } else {
            reloadConfig();
        }

        if (!getVersion().equalsIgnoreCase(getConfig().getString("version"))) {
            ConfigManager.getInstance().handleVersionMismatch();
        }

        ConfigManager.getInstance().loadValuesFromConfig();

        EventRegistry.getInstance().registerEvents();

        int pluginId = 8992;

        Metrics metrics = new Metrics(this, pluginId);
    }

    @Override
    public void onDisable() {

    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        return CommandInterpreter.getInstance().interpretCommand(sender, label, args);
    }

    public String getVersion() {
        return version;
    }
}

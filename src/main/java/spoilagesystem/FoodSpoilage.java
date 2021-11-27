package spoilagesystem;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import spoilagesystem.bStats.Metrics;

import java.io.File;
import java.io.IOException;

public final class FoodSpoilage extends JavaPlugin {

    private static FoodSpoilage instance;

    public static FoodSpoilage getInstance() {
        return instance;
    }

    public String getVersion() {
        return "v2.1-alpha-1";
    }

    @Override
    public void onEnable() {
        instance = this;

        ConfigManager.getInstance().ensureSmoothTransitionBetweenVersions();

        // config creation/loading
        if (!(new File("./plugins/FoodSpoilage/config.yml").exists())) {
            ConfigManager.getInstance().create();
        } else {
            reloadConfig();
        }

        if (!getVersion().equalsIgnoreCase(getConfig().getString("version"))) {
            ConfigManager.getInstance().handleVersionMismatch();
        }

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

}

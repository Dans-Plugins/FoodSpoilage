package spoilagesystem;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import spoilagesystem.bStats.Metrics;
import spoilagesystem.services.LocalCommandService;
import spoilagesystem.services.LocalConfigService;
import spoilagesystem.utils.EventRegistry;

import java.io.File;

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

        LocalConfigService.getInstance().ensureSmoothTransitionBetweenVersions();

        // config creation/loading
        if (!(new File("./plugins/FoodSpoilage/config.yml").exists())) {
            LocalConfigService.getInstance().create();
        } else {
            reloadConfig();
        }

        if (!getVersion().equalsIgnoreCase(getConfig().getString("version"))) {
            LocalConfigService.getInstance().handleVersionMismatch();
        }

        EventRegistry.getInstance().registerEvents();

        int pluginId = 8992;

        new Metrics(this, pluginId);
    }

    @Override
    public void onDisable() {

    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        return LocalCommandService.getInstance().interpretCommand(sender, label, args);
    }

}

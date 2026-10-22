package spoilagesystem.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.PluginDescriptionFile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import spoilagesystem.FoodSpoilage;
import spoilagesystem.config.LocalConfigService;

import static org.bukkit.ChatColor.AQUA;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Tests for {@link DefaultCommand}, covering what a sender holding {@code fs.default} gets.
 */
public class DefaultCommandTest {

    private static final String[] NO_ARGS = new String[0];

    @Mock
    private FoodSpoilage plugin;

    @Mock
    private LocalConfigService configService;

    @Mock
    private CommandSender sender;

    @Mock
    private Command command;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        when(sender.hasPermission("fs.default")).thenReturn(true);
        when(plugin.getName()).thenReturn("FoodSpoilage");
        // The version shown is whatever plugin.yml carries, so it is taken from the description.
        when(plugin.getDescription()).thenReturn(new PluginDescriptionFile("FoodSpoilage", "9.8.7-TEST", "spoilagesystem.FoodSpoilage"));
    }

    @Test
    void sendsThePluginNameAndVersionFromItsDescription() {
        boolean handled = new DefaultCommand(plugin, configService).onCommand(sender, command, "fs", NO_ARGS);

        verify(sender).sendMessage(new String[] {
                AQUA + "FoodSpoilage v9.8.7-TEST",
                AQUA + "Developed by: Daniel McCoy Stephenson",
                AQUA + "Wiki: https://github.com/Dans-Plugins/FoodSpoilage/wiki"
        });
        assertTrue(handled);
    }
}

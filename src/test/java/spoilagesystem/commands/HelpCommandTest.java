package spoilagesystem.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import spoilagesystem.config.LocalConfigService;

import static org.bukkit.ChatColor.AQUA;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Tests for {@link HelpCommand}, covering what a sender holding {@code fs.help} gets.
 */
public class HelpCommandTest {

    private static final String[] NO_ARGS = new String[0];

    @Mock
    private LocalConfigService configService;

    @Mock
    private CommandSender sender;

    @Mock
    private Command command;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        when(sender.hasPermission("fs.help")).thenReturn(true);
    }

    @Test
    void listsEverySubcommand() {
        boolean handled = new HelpCommand(configService).onCommand(sender, command, "fs", NO_ARGS);

        verify(sender).sendMessage(new String[] {
                AQUA + "=== FoodSpoilage Commands ===",
                AQUA + "/fs help - View a list of helpful commands.",
                AQUA + "/fs timeleft - View how much time is left before an item expires.",
                AQUA + "/fs reload - Reload the plugin's configuration/save files."
        });
        assertTrue(handled);
    }
}

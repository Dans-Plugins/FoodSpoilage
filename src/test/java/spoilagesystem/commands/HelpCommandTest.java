package spoilagesystem.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import spoilagesystem.config.LocalConfigService;

import java.util.List;

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

    /** The listing is whatever {@code text.help} holds, so a translated config replaces every line. */
    @Test
    void sendsTheConfiguredHelpLinesInOrder() {
        when(configService.getHelpText()).thenReturn(List.of("§b=== FoodSpoilage-Befehle ===", "§b/fs help - Befehle anzeigen."));

        boolean handled = new HelpCommand(configService).onCommand(sender, command, "fs", NO_ARGS);

        verify(sender).sendMessage(new String[] {
                "§b=== FoodSpoilage-Befehle ===",
                "§b/fs help - Befehle anzeigen."
        });
        assertTrue(handled);
    }
}

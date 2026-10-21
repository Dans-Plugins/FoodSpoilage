package spoilagesystem.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import spoilagesystem.FoodSpoilage;
import spoilagesystem.config.LocalConfigService;

import static org.bukkit.ChatColor.GREEN;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.when;

/**
 * Tests for {@link ReloadCommand}, covering what a sender holding {@code fs.reload} gets.
 */
public class ReloadCommandTest {

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

        when(sender.hasPermission("fs.reload")).thenReturn(true);
        when(configService.getValuesLoadedText()).thenReturn("configured values-loaded reply");
    }

    @Test
    void reloadsThePluginAndThenSendsTheConfiguredReply() {
        boolean handled = new ReloadCommand(plugin, configService).onCommand(sender, command, "fs", NO_ARGS);

        // The reply is only true once the reload has run, so it must come after it.
        InOrder inOrder = inOrder(plugin, sender);
        inOrder.verify(plugin).reload();
        inOrder.verify(sender).sendMessage(GREEN + "configured values-loaded reply");
        assertTrue(handled);
    }
}

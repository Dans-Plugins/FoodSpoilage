package spoilagesystem.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import spoilagesystem.FoodSpoilage;
import spoilagesystem.config.LocalConfigService;
import spoilagesystem.timestamp.LocalTimeStampService;

import static org.bukkit.ChatColor.RED;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Tests for the reply each command sends to a sender who lacks its permission, which is read from
 * the configuration rather than written into the command.
 */
public class PermissionDeniedReplyTest {

    private static final String[] NO_ARGS = new String[0];

    @Mock
    private FoodSpoilage plugin;

    @Mock
    private LocalConfigService configService;

    @Mock
    private LocalTimeStampService timeStampService;

    @Mock
    private CommandSender sender;

    @Mock
    private Command command;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        when(sender.hasPermission(anyString())).thenReturn(false);
    }

    @Test
    void theBaseCommandSendsTheConfiguredReply() {
        when(configService.getNoPermsDefaultText()).thenReturn("configured default reply");

        new DefaultCommand(plugin, configService).onCommand(sender, command, "fs", NO_ARGS);

        verify(sender).sendMessage(RED + "configured default reply");
    }

    @Test
    void theHelpCommandSendsTheConfiguredReply() {
        when(configService.getNoPermsHelpText()).thenReturn("configured help reply");

        new HelpCommand(configService).onCommand(sender, command, "fs", NO_ARGS);

        verify(sender).sendMessage(RED + "configured help reply");
    }

    @Test
    void theTimeLeftCommandSendsTheConfiguredReply() {
        when(configService.getNoPermsTimeLeftText()).thenReturn("configured timeleft reply");

        new TimeLeftCommand(configService, timeStampService).onCommand(sender, command, "fs", NO_ARGS);

        verify(sender).sendMessage(RED + "configured timeleft reply");
    }

    @Test
    void theReloadCommandSendsTheConfiguredReplyWithoutTheUsageLine() {
        when(configService.getNoPermsReloadText()).thenReturn("configured reload reply");

        boolean handled = new ReloadCommand(plugin, configService).onCommand(sender, command, "fs", NO_ARGS);

        verify(sender).sendMessage(RED + "configured reload reply");
        verify(plugin, never()).reload();
        // Bukkit sends the usage line from plugin.yml whenever an executor returns false
        assertTrue(handled);
    }
}

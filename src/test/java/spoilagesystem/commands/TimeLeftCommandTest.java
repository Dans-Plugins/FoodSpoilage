package spoilagesystem.commands;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import spoilagesystem.config.LocalConfigService;
import spoilagesystem.timestamp.LocalTimeStampService;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Tests for {@link TimeLeftCommand}, covering the reply a permitted sender gets about the item in
 * their main hand.
 */
public class TimeLeftCommandTest {

    private static final String[] NO_ARGS = new String[0];

    @Mock
    private LocalConfigService configService;

    @Mock
    private LocalTimeStampService timeStampService;

    @Mock
    private Player player;

    @Mock
    private PlayerInventory playerInventory;

    @Mock
    private Command command;

    private TimeLeftCommand timeLeftCommand;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        timeLeftCommand = new TimeLeftCommand(configService, timeStampService);

        when(player.hasPermission("fs.timeleft")).thenReturn(true);
        when(player.getInventory()).thenReturn(playerInventory);
        when(configService.getNeverSpoilText()).thenReturn("configured never-spoil reply");
    }

    @Test
    void sendsTheTimeLeftOnTheItemInTheMainHand() {
        ItemStack bread = new ItemStack(Material.BREAD);
        when(playerInventory.getItemInMainHand()).thenReturn(bread);
        when(timeStampService.getTimeLeft(bread)).thenReturn("3 days left");

        boolean handled = timeLeftCommand.onCommand(player, command, "fs", NO_ARGS);

        verify(player).sendMessage("3 days left");
        verify(player, never()).sendMessage("configured never-spoil reply");
        assertTrue(handled);
    }

    @Test
    void sendsTheConfiguredNeverSpoilReplyWhenTheItemHasNoExpiry() {
        ItemStack stone = new ItemStack(Material.STONE);
        when(playerInventory.getItemInMainHand()).thenReturn(stone);
        when(timeStampService.getTimeLeft(stone)).thenReturn(null);

        boolean handled = timeLeftCommand.onCommand(player, command, "fs", NO_ARGS);

        verify(player).sendMessage("configured never-spoil reply");
        assertTrue(handled);
    }

    @Test
    void returnsFalseForASenderThatIsNotAPlayer() {
        // A console has no hand to inspect. Returning false makes Bukkit send the usage line from
        // plugin.yml instead.
        CommandSender console = mock(CommandSender.class);
        when(console.hasPermission("fs.timeleft")).thenReturn(true);

        boolean handled = timeLeftCommand.onCommand(console, command, "fs", NO_ARGS);

        assertFalse(handled);
        verify(console, never()).sendMessage(anyString());
        verify(timeStampService, never()).getTimeLeft(any());
    }
}

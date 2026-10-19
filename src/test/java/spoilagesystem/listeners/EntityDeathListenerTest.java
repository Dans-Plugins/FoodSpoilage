package spoilagesystem.listeners;

import org.bukkit.Material;
import org.bukkit.entity.Cow;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import spoilagesystem.timestamp.LocalTimeStampService;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Tests for {@link EntityDeathListener}, covering which drops of a slain mob are given an expiry
 * date, and that a player's death drops are left alone.
 */
public class EntityDeathListenerTest {

    @Mock
    private LocalTimeStampService timeStampService;

    @Mock
    private EntityDeathEvent event;

    private EntityDeathListener listener;

    private List<ItemStack> drops;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        listener = new EntityDeathListener(timeStampService);

        drops = new ArrayList<>();
        when(event.getDrops()).thenReturn(drops);
        when(event.getEntity()).thenReturn(mock(Cow.class));
    }

    @Test
    void replacesEdibleDropsWithStampedStacksAndKeepsTheRestInOrder() {
        ItemStack beef = item(Material.BEEF);
        ItemStack stampedBeef = item(Material.BEEF);
        ItemStack leather = item(Material.LEATHER);
        when(timeStampService.assignTimeStamp(beef)).thenReturn(stampedBeef);
        drops.add(beef);
        drops.add(leather);

        listener.onEntityDeath(event);

        assertEquals(List.of(stampedBeef, leather), drops);
        verify(timeStampService, never()).assignTimeStamp(leather);
    }

    @Test
    void leavesAPlayersDeathDropsAlone() {
        when(event.getEntity()).thenReturn(mock(Player.class));
        ItemStack bread = item(Material.BREAD);
        drops.add(bread);

        listener.onEntityDeath(event);

        assertEquals(List.of(bread), drops);
        verify(timeStampService, never()).assignTimeStamp(any(ItemStack.class));
    }

    @Test
    void doesNotStampTheSpoiledFoodMaterial() {
        ItemStack rottenFlesh = item(Material.ROTTEN_FLESH);
        when(timeStampService.isSpoiledFoodMaterial(rottenFlesh)).thenReturn(true);
        drops.add(rottenFlesh);

        listener.onEntityDeath(event);

        assertEquals(List.of(rottenFlesh), drops);
        verify(timeStampService, never()).assignTimeStamp(any(ItemStack.class));
    }

    @Test
    void doesNotRestampFoodThatAlreadyHasAnExpiryDate() {
        // A mob that picked up stamped food drops it on death; restamping it would push its
        // expiry date forward.
        ItemStack stamped = item(Material.BREAD);
        when(timeStampService.timeStampAssigned(stamped)).thenReturn(true);
        drops.add(stamped);

        listener.onEntityDeath(event);

        assertEquals(List.of(stamped), drops);
        verify(timeStampService, never()).assignTimeStamp(any(ItemStack.class));
    }

    @Test
    void doesNotStampWaxedFood() {
        ItemStack waxed = item(Material.BEEF);
        when(timeStampService.isWaxed(waxed)).thenReturn(true);
        drops.add(waxed);

        listener.onEntityDeath(event);

        assertEquals(List.of(waxed), drops);
        verify(timeStampService, never()).assignTimeStamp(any(ItemStack.class));
    }

    private ItemStack item(Material type) {
        ItemStack item = mock(ItemStack.class);
        when(item.getType()).thenReturn(type);
        return item;
    }
}

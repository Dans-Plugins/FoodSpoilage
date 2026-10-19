package spoilagesystem.listeners;

import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import spoilagesystem.config.LocalConfigService;
import spoilagesystem.timestamp.LocalTimeStampService;

import java.time.Duration;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Tests for {@link ItemSpawnListener}, covering which item entities are given an expiry date as
 * they appear in the world.
 */
public class ItemSpawnListenerTest {

    private static final Duration ONE_DAY = Duration.ofHours(24);

    @Mock
    private LocalConfigService configService;

    @Mock
    private LocalTimeStampService timeStampService;

    @Mock
    private ItemSpawnEvent event;

    @Mock
    private Item spawnedItem;

    private ItemSpawnListener listener;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        listener = new ItemSpawnListener(configService, timeStampService);

        when(event.getEntity()).thenReturn(spawnedItem);
        when(configService.getTime(any(Material.class))).thenReturn(ONE_DAY);
    }

    @Test
    void stampsUnstampedEdibleFoodWithItsConfiguredSpoilTime() {
        ItemStack bread = item(Material.BREAD);
        ItemStack stampedBread = item(Material.BREAD);
        when(timeStampService.assignTimeStamp(bread, ONE_DAY)).thenReturn(stampedBread);
        givenSpawned(bread);

        listener.onItemSpawn(event);

        verify(spawnedItem).setItemStack(stampedBread);
    }

    @Test
    void doesNotStampFoodWithNoConfiguredSpoilTime() {
        when(configService.getTime(Material.GOLDEN_APPLE)).thenReturn(Duration.ZERO);
        givenSpawned(item(Material.GOLDEN_APPLE));

        listener.onItemSpawn(event);

        verify(spawnedItem, never()).setItemStack(any());
    }

    @Test
    void doesNotStampNonEdibleItems() {
        givenSpawned(item(Material.WHEAT));

        listener.onItemSpawn(event);

        verify(spawnedItem, never()).setItemStack(any());
    }

    @Test
    void doesNotStampTheSpoiledFoodMaterial() {
        ItemStack rottenFlesh = item(Material.ROTTEN_FLESH);
        when(timeStampService.isSpoiledFoodMaterial(rottenFlesh)).thenReturn(true);
        givenSpawned(rottenFlesh);

        listener.onItemSpawn(event);

        verify(spawnedItem, never()).setItemStack(any());
    }

    @Test
    void doesNotRestampFoodThatAlreadyHasAnExpiryDate() {
        ItemStack stamped = item(Material.BREAD);
        when(timeStampService.timeStampAssigned(stamped)).thenReturn(true);
        givenSpawned(stamped);

        listener.onItemSpawn(event);

        verify(spawnedItem, never()).setItemStack(any());
    }

    @Test
    void doesNotStampWaxedFood() {
        ItemStack waxed = item(Material.BREAD);
        when(timeStampService.isWaxed(waxed)).thenReturn(true);
        givenSpawned(waxed);

        listener.onItemSpawn(event);

        verify(spawnedItem, never()).setItemStack(any());
    }

    private ItemStack item(Material type) {
        ItemStack item = mock(ItemStack.class);
        when(item.getType()).thenReturn(type);
        return item;
    }

    private void givenSpawned(ItemStack itemStack) {
        when(spawnedItem.getItemStack()).thenReturn(itemStack);
    }
}

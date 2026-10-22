package spoilagesystem.factories;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFactory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import spoilagesystem.config.LocalConfigService;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Tests for {@link SpoiledFoodFactory}, covering the item that expired food is replaced with.
 */
public class SpoiledFoodFactoryTest {

    @Mock
    private LocalConfigService configService;

    @Mock
    private ItemFactory itemFactory;

    @Mock
    private ItemMeta meta;

    // ItemStack reads and writes its meta through Bukkit's item factory, which needs a server.
    private MockedStatic<Bukkit> bukkit;

    private SpoiledFoodFactory factory;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        bukkit = mockStatic(Bukkit.class);
        bukkit.when(Bukkit::getItemFactory).thenReturn(itemFactory);
        when(itemFactory.isApplicable(any(ItemMeta.class), any(Material.class))).thenReturn(true);
        when(itemFactory.asMetaFor(any(ItemMeta.class), any(Material.class))).thenReturn(meta);
        when(itemFactory.updateMaterial(any(ItemMeta.class), any(Material.class))).thenAnswer(invocation -> invocation.getArgument(1));

        when(configService.getSpoiledFoodMaterial()).thenReturn(Material.POISONOUS_POTATO);
        when(configService.getSpoiledFoodName()).thenReturn("configured spoiled name");
        when(configService.getSpoiledFoodLore()).thenReturn("configured spoiled lore");
        factory = new SpoiledFoodFactory(configService);
    }

    @AfterEach
    void tearDown() {
        bukkit.close();
    }

    @Test
    void createsTheConfiguredMaterialInTheRequestedAmount() {
        when(itemFactory.getItemMeta(Material.POISONOUS_POTATO)).thenReturn(meta);

        ItemStack spoiledFood = factory.createSpoiledFood(7);

        assertEquals(Material.POISONOUS_POTATO, spoiledFood.getType());
        assertEquals(7, spoiledFood.getAmount());
    }

    @Test
    void namesTheItemAndGivesItTheConfiguredLoreInWhite() {
        when(itemFactory.getItemMeta(Material.POISONOUS_POTATO)).thenReturn(meta);

        factory.createSpoiledFood(1);

        verify(meta).setDisplayName("configured spoiled name");
        verify(meta).setLore(Collections.singletonList(ChatColor.WHITE + "configured spoiled lore"));
    }

    @Test
    void stillSetsTheAmountWhenTheMaterialHasNoMeta() {
        when(itemFactory.getItemMeta(Material.POISONOUS_POTATO)).thenReturn(null);

        ItemStack spoiledFood = factory.createSpoiledFood(3);

        assertEquals(Material.POISONOUS_POTATO, spoiledFood.getType());
        assertEquals(3, spoiledFood.getAmount());
    }
}

package spoilagesystem.packet;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import spoilagesystem.config.LocalConfigService;
import spoilagesystem.timestamp.LocalTimeStampService;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Service to dynamically inject expiry lore into item packets without modifying the actual items.
 * This ensures that lore is only visible when the plugin is active and doesn't persist after removal.
 * 
 * @author Daniel McCoy Stephenson
 */
public final class PacketLoreService {
    
    private final Plugin plugin;
    private final LocalConfigService configService;
    private final LocalTimeStampService timeStampService;
    private final ProtocolManager protocolManager;
    
    public PacketLoreService(Plugin plugin, LocalConfigService configService, LocalTimeStampService timeStampService) {
        this.plugin = plugin;
        this.configService = configService;
        this.timeStampService = timeStampService;
        this.protocolManager = ProtocolLibrary.getProtocolManager();
    }
    
    /**
     * Registers packet listeners to intercept outgoing item packets and inject expiry lore.
     */
    public void registerPacketListeners() {
        // Listen for window items (inventory contents)
        protocolManager.addPacketListener(new PacketAdapter(plugin, PacketType.Play.Server.WINDOW_ITEMS) {
            @Override
            public void onPacketSending(PacketEvent event) {
                PacketContainer packet = event.getPacket();
                List<ItemStack> items = packet.getItemListModifier().readSafely(0);
                
                if (items != null) {
                    List<ItemStack> modifiedItems = new ArrayList<>();
                    for (ItemStack item : items) {
                        modifiedItems.add(injectLoreIfNeeded(item));
                    }
                    packet.getItemListModifier().write(0, modifiedItems);
                }
            }
        });
        
        // Listen for set slot (single item updates)
        protocolManager.addPacketListener(new PacketAdapter(plugin, PacketType.Play.Server.SET_SLOT) {
            @Override
            public void onPacketSending(PacketEvent event) {
                PacketContainer packet = event.getPacket();
                ItemStack item = packet.getItemModifier().readSafely(0);
                
                if (item != null) {
                    packet.getItemModifier().write(0, injectLoreIfNeeded(item));
                }
            }
        });
        
        // Listen for entity equipment (items held/worn by entities)
        protocolManager.addPacketListener(new PacketAdapter(plugin, PacketType.Play.Server.ENTITY_EQUIPMENT) {
            @Override
            public void onPacketSending(PacketEvent event) {
                PacketContainer packet = event.getPacket();
                ItemStack item = packet.getItemModifier().readSafely(0);
                
                if (item != null) {
                    packet.getItemModifier().write(0, injectLoreIfNeeded(item));
                }
            }
        });
    }
    
    /**
     * Injects expiry lore into an item copy if it has an expiry timestamp.
     * Does not modify the original item.
     * 
     * @param originalItem The original item from the packet
     * @return A modified copy with lore, or the original if no expiry data exists
     */
    private ItemStack injectLoreIfNeeded(ItemStack originalItem) {
        if (originalItem == null || originalItem.getType().isAir()) {
            return originalItem;
        }
        
        // Check if this item has expiry data in NBT (but not lore yet)
        if (!originalItem.hasItemMeta()) {
            return originalItem;
        }
        
        ItemMeta meta = originalItem.getItemMeta();
        if (meta == null) {
            return originalItem;
        }
        
        // Only inject lore if there's NBT data but no lore already
        OffsetDateTime expiry = timeStampService.getTimeStampFromPersistentData(originalItem);
        if (expiry == null) {
            return originalItem;
        }
        
        // Check if lore already exists (old items or legacy behavior)
        if (meta.hasLore()) {
            return originalItem;
        }
        
        // Create a copy and inject the lore
        ItemStack modifiedItem = originalItem.clone();
        ItemMeta modifiedMeta = modifiedItem.getItemMeta();
        
        if (modifiedMeta != null) {
            String expiryDateString = timeStampService.formatExpiryDate(expiry);
            List<String> lore = configService.getExpiryDateText().stream()
                    .map(line -> line.replace("${expiry_date}", expiryDateString))
                    .toList();
            modifiedMeta.setLore(lore);
            modifiedItem.setItemMeta(modifiedMeta);
        }
        
        return modifiedItem;
    }
}

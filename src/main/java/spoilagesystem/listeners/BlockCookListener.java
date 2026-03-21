package spoilagesystem.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockCookEvent;

/**
 * @author Daniel McCoy Stephenson
 */
public final class BlockCookListener implements Listener {

    /**
     * Intentionally does not stamp cooked items with an expiry timestamp.
     *
     * <p>In Minecraft 1.20.5+, the furnace calls {@code canBurn()} on every server tick to
     * decide whether to continue cooking. That check compares the vanilla recipe result
     * (which carries no custom data) against the item already in the output slot using a
     * strict data-component comparison. If our plugin stamps the first cooked item with a
     * {@code minecraft:custom_data} component (via PDC), every subsequent {@code canBurn()}
     * call finds a mismatch and returns {@code false}, permanently stalling the furnace after
     * just one item — even though {@code BlockCookEvent} never fires again.</p>
     *
     * <p>The solution is to leave items in the furnace output slot completely vanilla
     * (no custom meta), so that {@code canBurn()} always sees identical items and the
     * whole stack cooks through. Items are stamped lazily by other listeners
     * ({@link InventoryOpenListener}, {@link PlayerJoinListener},
     * {@link EntityPickupItemListener}, {@link ItemSpawnListener}) once they leave
     * the furnace.</p>
     */
    @EventHandler
    public void onBlockCook(BlockCookEvent event) {
        // No-op: do not modify the result. See Javadoc above.
    }
}
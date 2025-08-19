package io.github.alathra.boltux.listener;

import io.github.alathra.boltux.BoltUX;
import io.github.alathra.boltux.utility.BlockUtil;
import org.bukkit.block.Barrel;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.popcraft.bolt.BoltPlugin;
import org.popcraft.bolt.protection.BlockProtection;
import org.popcraft.bolt.util.Permission;

public class InventoryOpenListener implements Listener {

    private final BoltPlugin boltPlugin;

    public InventoryOpenListener() {
        this.boltPlugin = BoltUX.getBoltPlugin();
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onInventoryOpen(InventoryOpenEvent event) {
        if (!(event.getPlayer() instanceof Player player)) return;

        // CHESTS
        if (event.getInventory().getHolder() instanceof Chest chest) {
            Block chestBlock = chest.getBlock();
            BlockProtection protection = loadChestProtection(chestBlock);
            if (protection == null) return;

            boolean allowed = boltPlugin.canAccess(protection, player, Permission.INTERACT, Permission.OPEN);
            if (!allowed) {
                event.setCancelled(true);
            }
            return;
        }

        // BARRELS
        if (event.getInventory().getHolder() instanceof Barrel barrel) {
            BlockProtection protection = boltPlugin.loadProtection(barrel.getBlock());
            if (protection == null) return;

            boolean allowed = boltPlugin.canAccess(protection, player, Permission.INTERACT, Permission.OPEN);
            if (!allowed) {
                event.setCancelled(true);
            }
        }
    }

    private BlockProtection loadChestProtection(Block chestBlock) {
        // Try canonical half first
        Block norm = BlockUtil.normalizeChestBlock(chestBlock);
        BlockProtection p = boltPlugin.loadProtection(norm);
        if (p != null) return p;

        // Fallback: other half (covers legacy protections saved on the opposite side)
        Block other = BlockUtil.getConnectedDoubleChest(chestBlock);
        if (other != null) {
            return boltPlugin.loadProtection(BlockUtil.normalizeChestBlock(other));
        }
        return null;
    }
}

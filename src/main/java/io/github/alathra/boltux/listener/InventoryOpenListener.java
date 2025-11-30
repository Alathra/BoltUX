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
import org.jetbrains.annotations.Nullable;
import org.popcraft.bolt.BoltPlugin;
import org.popcraft.bolt.protection.BlockProtection;
import org.popcraft.bolt.util.Permission;

public final class InventoryOpenListener implements Listener {
    private final BoltPlugin boltPlugin;

    public InventoryOpenListener() {
        this.boltPlugin = BoltUX.getBoltPlugin();
    }

    @SuppressWarnings("unused")
    @EventHandler(priority = EventPriority.NORMAL)
    public void onInventoryOpen(InventoryOpenEvent e) {
        if (!(e.getPlayer() instanceof Player player))
            return;

        if (e.getInventory().getHolder() == null) {
            return;
        }

        // CHESTS
        if (e.getInventory().getHolder() instanceof Chest chest) {
            final Block chestBlock = chest.getBlock();
            final BlockProtection protection = loadChestProtection(chestBlock);
            if (protection == null)
                return;

            final boolean allowed = boltPlugin.canAccess(protection, player, Permission.INTERACT, Permission.OPEN);
            if (!allowed) {
                e.setCancelled(true);
            }
            return;
        }

        // BARRELS
        if (e.getInventory().getHolder() instanceof Barrel barrel) {
            final BlockProtection protection = boltPlugin.loadProtection(barrel.getBlock());
            if (protection == null)
                return;

            final boolean allowed = boltPlugin.canAccess(protection, player, Permission.INTERACT, Permission.OPEN);
            if (!allowed) {
                e.setCancelled(true);
            }
        }
    }

    private @Nullable BlockProtection loadChestProtection(Block chestBlock) {
        // Try canonical half first
        final Block norm = BlockUtil.normalizeChestBlock(chestBlock);
        final BlockProtection protection = boltPlugin.loadProtection(norm);
        if (protection != null)
            return protection;

        // Fallback: other half (covers legacy protections saved on the opposite side)
        final Block other = BlockUtil.getConnectedDoubleChest(chestBlock);
        if (other != null) {
            return boltPlugin.loadProtection(BlockUtil.normalizeChestBlock(other));
        }
        return null;
    }
}

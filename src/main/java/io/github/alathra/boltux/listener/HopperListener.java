package io.github.alathra.boltux.listener;

import io.github.alathra.boltux.BoltUX;
import io.github.alathra.boltux.utility.BlockUtil;
import org.bukkit.block.Barrel;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.inventory.Inventory;
import org.popcraft.bolt.BoltPlugin;

public class HopperListener implements Listener {

    private final BoltPlugin boltPlugin;

    public HopperListener() {
        this.boltPlugin = BoltUX.getBoltPlugin();
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onHopperMove(InventoryMoveItemEvent event) {
        Inventory src = event.getSource();
        Inventory dst = event.getDestination();

        // Block pulls FROM protected chests/barrels
        if (src.getHolder() instanceof Chest chest) {
            if (isChestProtected(chest.getBlock())) {
                event.setCancelled(true);
                return;
            }
        } else if (src.getHolder() instanceof Barrel barrel) {
            if (boltPlugin.isProtected(barrel.getBlock())) {
                event.setCancelled(true);
                return;
            }
        }

        // Block pushes INTO protected chests/barrels
        if (dst.getHolder() instanceof Chest chest) {
            if (isChestProtected(chest.getBlock())) {
                event.setCancelled(true);
                return;
            }
        } else if (dst.getHolder() instanceof Barrel barrel) {
            if (boltPlugin.isProtected(barrel.getBlock())) {
                event.setCancelled(true);
            }
        }
    }

    private boolean isChestProtected(Block chestBlock) {
        // Normalize to a canonical half and check both halves defensively
        Block norm = BlockUtil.normalizeChestBlock(chestBlock);
        if (boltPlugin.isProtected(norm)) return true;

        Block other = BlockUtil.getConnectedDoubleChest(chestBlock);
        if (other != null && boltPlugin.isProtected(BlockUtil.normalizeChestBlock(other))) return true;

        return false;
    }
}

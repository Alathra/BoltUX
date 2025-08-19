package io.github.alathra.boltux.listener;

import io.github.alathra.boltux.BoltUX;
import io.github.alathra.boltux.utility.BlockUtil;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.popcraft.bolt.BoltPlugin;

public class PistonListener implements Listener {

    private final BoltPlugin boltPlugin;

    public PistonListener() {
        this.boltPlugin = BoltUX.getBoltPlugin();
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPistonExtend(BlockPistonExtendEvent event) {
        for (Block moved : event.getBlocks()) {
            if (isProtectedContainer(moved)) {
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPistonRetract(BlockPistonRetractEvent event) {
        for (Block moved : event.getBlocks()) {
            if (isProtectedContainer(moved)) {
                event.setCancelled(true);
                return;
            }
        }
    }

    private boolean isProtectedContainer(Block b) {
        Material t = b.getType();
        if (t == Material.CHEST || t == Material.TRAPPED_CHEST) {
            Block norm = BlockUtil.normalizeChestBlock(b);
            if (boltPlugin.isProtected(norm)) return true;

            Block other = BlockUtil.getConnectedDoubleChest(b);
            return other != null && boltPlugin.isProtected(BlockUtil.normalizeChestBlock(other));
        }
        if (t == Material.BARREL) {
            return boltPlugin.isProtected(b);
        }
        return false;
    }
}

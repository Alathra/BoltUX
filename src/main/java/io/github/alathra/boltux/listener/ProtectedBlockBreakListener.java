package io.github.alathra.boltux.listener;

import io.github.alathra.boltux.BoltUX;
import io.github.alathra.boltux.api.BoltUXAPI;
import io.github.alathra.boltux.config.Settings;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.popcraft.bolt.BoltPlugin;

public class ProtectedBlockBreakListener implements Listener {

    private final BoltPlugin boltPlugin;

    public ProtectedBlockBreakListener()  {
        boltPlugin = BoltUX.getBoltPlugin();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onProtectedBlockBreak(BlockBreakEvent event) {
        if (event.isCancelled()) {
            return;
        }
        if (!Settings.isLockingEnabled()) {
            return;
        }
        if (!Settings.isLockDroppingEnabled()) {
            return;
        }
        Block block = event.getBlock();
        if (!Settings.getLockItemEnabledWorlds().contains(block.getWorld())) {
            return;
        }
        if (!boltPlugin.isProtectable(block)) {
            return;
        }

        // Drop a lock item at the broken block location
        block.getWorld().dropItemNaturally(block.getLocation(), BoltUXAPI.getLockItem());
    }

}

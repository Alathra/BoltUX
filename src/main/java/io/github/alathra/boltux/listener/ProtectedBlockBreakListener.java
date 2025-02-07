package io.github.alathra.boltux.listener;

import io.github.alathra.boltux.BoltUX;
import io.github.alathra.boltux.core.MaterialGroups;
import io.github.alathra.boltux.packets.GlowingBlock;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.popcraft.bolt.BoltPlugin;
import org.popcraft.bolt.protection.BlockProtection;
import org.popcraft.bolt.util.Permission;

public class ProtectedBlockBreakListener implements Listener {

    private final BoltPlugin boltPlugin;

    public ProtectedBlockBreakListener() {
        boltPlugin = BoltUX.getBoltPlugin();
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onProtectedBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        if (!boltPlugin.isProtected(block)) {
            return;
        }
        Player player = event.getPlayer();
        Material material = block.getType();
        BlockProtection protection = boltPlugin.loadProtection(block);
        if (protection.getOwner().equals(player.getUniqueId())) {
            return;
        }

        boolean canBreak = false;
        if (MaterialGroups.inventoryBlocks.contains(material) || MaterialGroups.interactableBlocks.contains(material)) {
            canBreak = boltPlugin.canAccess(protection, player.getUniqueId(), Permission.DESTROY);
        }

        // Display red glowing block if player does not have access
        if (!canBreak) {
            GlowingBlock glowingBlock = new GlowingBlock(block, player);
            glowingBlock.glow(NamedTextColor.RED);
        }
    }

}

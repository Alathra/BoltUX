package io.github.alathra.boltux.listener;

import com.destroystokyo.paper.MaterialTags;
import io.github.alathra.boltux.BoltUX;
import io.github.alathra.boltux.core.MaterialGroups;
import io.github.alathra.boltux.packets.GlowingBlock;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.popcraft.bolt.BoltPlugin;
import org.popcraft.bolt.protection.BlockProtection;
import org.popcraft.bolt.util.Permission;

public class ProtectedBlockRightClickListener implements Listener {

    private final BoltPlugin boltPlugin;

    public ProtectedBlockRightClickListener() {
        boltPlugin = BoltUX.getBoltPlugin();
    }

    @EventHandler
    public void onProtectedBlockRightClick(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        if (event.getHand() == null) {
            return;
        }
        if (event.getHand().equals(EquipmentSlot.OFF_HAND)) {
            return;
        }
        Block block = event.getClickedBlock();
        if (block == null) {
            return;
        }
        if (!boltPlugin.isProtected(block)) {
            return;
        }
        Player player = event.getPlayer();
        Material material = block.getType();
        BlockProtection protection = boltPlugin.loadProtection(block);

        // Determine if player is protection owner
        boolean isOwner = protection.getOwner().equals(player.getUniqueId());
        if (isOwner) {
            // TODO: Open BoltUXGUI
            return;
        }

        boolean canAccess = false;
        if (MaterialGroups.inventoryBlocks.contains(material)) {
            canAccess = boltPlugin.canAccess(protection, player.getUniqueId(), Permission.INTERACT, Permission.OPEN);
        } else if(MaterialGroups.interactableBlocks.contains(material)) {
            canAccess = boltPlugin.canAccess(protection, player.getUniqueId(), Permission.INTERACT);
        }

        // Display red glowing block if player does not have access
        if (!canAccess) {
            GlowingBlock glowingBlock = new GlowingBlock(block, player);
            glowingBlock.glow(NamedTextColor.RED);
        }
    }
}

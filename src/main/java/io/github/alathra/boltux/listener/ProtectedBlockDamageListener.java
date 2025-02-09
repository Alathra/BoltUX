package io.github.alathra.boltux.listener;

import com.destroystokyo.paper.MaterialTags;
import io.github.alathra.boltux.BoltUX;
import io.github.alathra.boltux.core.MaterialGroups;
import io.github.alathra.boltux.packets.GlowingBlock;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.popcraft.bolt.BoltPlugin;
import org.popcraft.bolt.protection.BlockProtection;
import org.popcraft.bolt.util.Permission;

public class ProtectedBlockDamageListener implements Listener {

    private final BoltPlugin boltPlugin;

    public ProtectedBlockDamageListener() {
        boltPlugin = BoltUX.getBoltPlugin();
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onProtectedBlockDamage(PlayerInteractEvent event) {
        if (!event.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
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
        if (protection == null) {
            if (MaterialTags.DOORS.isTagged(material)) {
                protection = boltPlugin.loadProtection(block.getRelative(BlockFace.DOWN));
            } else {
                return;
            }
        }
        if (protection.getOwner().equals(player.getUniqueId())) {
            return;
        }

        boolean canBreak = true;
        if (MaterialGroups.containerBlocks.contains(material)
            || MaterialGroups.interactableBlocks.contains(material)
            || MaterialGroups.otherBlocks.contains(material)
        ) {
            canBreak = boltPlugin.canAccess(protection, player, Permission.DESTROY);
        }

        // Display red glowing block if player does not have access
        if (!canBreak) {
            GlowingBlock glowingBlock = new GlowingBlock(block, player);
            glowingBlock.glow(NamedTextColor.RED);
        }
    }

}

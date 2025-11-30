package io.github.alathra.boltux.listener;

import com.destroystokyo.paper.MaterialTags;
import io.github.alathra.boltux.BoltUX;
import io.github.alathra.boltux.data.EntityGroups;
import io.github.alathra.boltux.data.MaterialGroups;
import io.github.alathra.boltux.hook.Hook;
import io.github.alathra.boltux.packets.GlowingBlock;
import io.github.alathra.boltux.packets.GlowingEntity;
import io.github.alathra.boltux.utility.BlockUtil;
import io.papermc.paper.event.player.PrePlayerAttackEntityEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.popcraft.bolt.BoltPlugin;
import org.popcraft.bolt.protection.BlockProtection;
import org.popcraft.bolt.protection.EntityProtection;
import org.popcraft.bolt.util.Permission;

public final class ProtectionDamageListeners implements Listener {
    private final BoltPlugin boltPlugin;

    public ProtectionDamageListeners() {
        boltPlugin = BoltUX.getBoltPlugin();
    }

    @SuppressWarnings("unused")
    @EventHandler(priority = EventPriority.LOWEST)
    public void onProtectedBlockDamage(PlayerInteractEvent e) {
        if (!e.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
            return;
        }

        if (e.getHand() == null) {
            return;
        }

        if (e.getHand().equals(EquipmentSlot.OFF_HAND)) {
            return;
        }

        final Block block = e.getClickedBlock();
        if (block == null) {
            return;
        }

        if (!boltPlugin.isProtected(block)) {
            return;
        }

        final Player player = e.getPlayer();
        final Material material = block.getType();
        BlockProtection protection = boltPlugin.loadProtection(block);
        if (protection == null) {
            if (MaterialTags.DOORS.isTagged(material)) {
                protection = boltPlugin.loadProtection(block.getRelative(BlockFace.DOWN));
                if (protection == null) {
                    return;
                }
            } else if (material.equals(Material.CHEST) || material.equals(Material.TRAPPED_CHEST)) {
                protection = boltPlugin.loadProtection(BlockUtil.getConnectedDoubleChest(block));
                if (protection == null) {
                    return;
                }
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
            if (Hook.PacketEvents.isLoaded()) {
                GlowingBlock glowingBlock = new GlowingBlock(block, player);
                glowingBlock.glow(NamedTextColor.RED);
            }
        }
    }

    @SuppressWarnings("unused")
    @EventHandler(priority = EventPriority.LOWEST)
    public void onProtectedEntityDamage(EntityDamageByEntityEvent e) {
        if (!(e.getDamager() instanceof Player player)) {
            return;
        }

        final Entity entity = e.getEntity();
        if (!boltPlugin.isProtected(entity)) {
            return;
        }

        final EntityType entityType = entity.getType();
        final EntityProtection protection = boltPlugin.loadProtection(entity);
        if (protection == null) {
            return;
        }

        if (protection.getOwner().equals(player.getUniqueId())) {
            return;
        }

        boolean canBreak = true;
        if (EntityGroups.otherInteractableEntities.contains(entityType)
            || EntityGroups.otherEntities.contains(entityType)
        ) {
            canBreak = boltPlugin.canAccess(protection, player, Permission.INTERACT, Permission.DESTROY);
        }

        // Make entity glow red if player does not have access
        if (!canBreak) {
            new GlowingEntity(entity, player, NamedTextColor.RED);
        }
    }

    @SuppressWarnings("unused")
    @EventHandler(priority = EventPriority.LOWEST)
    public void onProtectedEntityAttack(PrePlayerAttackEntityEvent e) {
        final Entity entity = e.getAttacked();
        if (!boltPlugin.isProtected(entity)) {
            return;
        }

        final EntityType entityType = entity.getType();
        final EntityProtection protection = boltPlugin.loadProtection(entity);
        final Player player = e.getPlayer();
        if (protection == null) {
            return;
        }

        if (protection.getOwner().equals(player.getUniqueId())) {
            return;
        }

        boolean canBreak = true;
        if (EntityGroups.chestBoats.contains(entityType)
            || EntityGroups.containerMinecarts.contains(entityType)
        ) {
            canBreak = boltPlugin.canAccess(protection, player, Permission.INTERACT, Permission.DESTROY);
        }

        // Make entity glow red if player does not have access
        if (!canBreak) {
            if (Hook.PacketEvents.isLoaded()) {
                new GlowingEntity(entity, player, NamedTextColor.RED);
            }
        }
    }

    @SuppressWarnings("unused")
    @EventHandler(priority = EventPriority.LOWEST)
    public void onHangingEntityBreak(HangingBreakByEntityEvent e) {
        if (!(e.getRemover() instanceof Player player)) {
            return;
        }

        final Entity entity = e.getEntity();
        if (!boltPlugin.isProtected(entity)) {
            return;
        }

        final EntityType entityType = entity.getType();
        final EntityProtection protection = boltPlugin.loadProtection(entity);
        if (protection == null) {
            return;
        }

        if (protection.getOwner().equals(player.getUniqueId())) {
            return;
        }

        boolean canBreak = true;
        if (EntityGroups.otherInteractableEntities.contains(entityType)
            || EntityGroups.otherEntities.contains(entityType)
        ) {
            canBreak = boltPlugin.canAccess(protection, player, Permission.INTERACT, Permission.DESTROY);
        }

        // Make entity glow red if player does not have access
        if (!canBreak) {
            if (Hook.PacketEvents.isLoaded()) {
                new GlowingEntity(entity, player, NamedTextColor.RED);
            }
        }
    }
}

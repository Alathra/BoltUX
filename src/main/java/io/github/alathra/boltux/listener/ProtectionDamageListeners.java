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

public class ProtectionDamageListeners implements Listener {

    private final BoltPlugin boltPlugin;

    public ProtectionDamageListeners() {
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

    @EventHandler(priority = EventPriority.LOWEST)
    public void onProtectedEntityDamage(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player player)) {
            return;
        }
        Entity entity = event.getEntity();
        if (!boltPlugin.isProtected(entity)) {
            return;
        }
        EntityType entityType = entity.getType();
        EntityProtection protection = boltPlugin.loadProtection(entity);
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
            if (GlowingEntity.glowingEntitiesRawMap.containsKey(entity.getEntityId())) {
                return;
            }
            GlowingEntity glowingEntity = new GlowingEntity(entity, player);
            glowingEntity.glow(NamedTextColor.RED);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onProtectedEntityAttack(PrePlayerAttackEntityEvent event) {
        Entity entity = event.getAttacked();
        if (!boltPlugin.isProtected(entity)) {
            return;
        }
        EntityType entityType = entity.getType();
        EntityProtection protection = boltPlugin.loadProtection(entity);
        Player player = event.getPlayer();
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
                if (GlowingEntity.glowingEntitiesRawMap.containsKey(entity.getEntityId())) {
                    return;
                }
                GlowingEntity glowingEntity = new GlowingEntity(entity, player);
                glowingEntity.glow(NamedTextColor.RED);
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onHangingEntityBreak(HangingBreakByEntityEvent event) {
        if (!(event.getRemover() instanceof Player player)) {
            return;
        }
        Entity entity = event.getEntity();
        if (!boltPlugin.isProtected(entity)) {
            return;
        }
        EntityType entityType = entity.getType();
        EntityProtection protection = boltPlugin.loadProtection(entity);
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
            if (Hook.PacketEvents.isLoaded())  {
                if (GlowingEntity.glowingEntitiesRawMap.containsKey(entity.getEntityId())) {
                    return;
                }
                GlowingEntity glowingEntity = new GlowingEntity(entity, player);
                glowingEntity.glow(NamedTextColor.RED);
            }
        }
    }

}

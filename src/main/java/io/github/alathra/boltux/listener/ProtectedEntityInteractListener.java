package io.github.alathra.boltux.listener;

import io.github.alathra.boltux.BoltUX;
import io.github.alathra.boltux.core.EntityGroups;
import io.github.alathra.boltux.packets.GlowingEntity;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.popcraft.bolt.BoltPlugin;
import org.popcraft.bolt.protection.EntityProtection;
import org.popcraft.bolt.util.Permission;

public class ProtectedEntityInteractListener implements Listener {

    private final BoltPlugin boltPlugin;

    public ProtectedEntityInteractListener() {
        boltPlugin = BoltUX.getBoltPlugin();
    }

    // For all entities except armor stands
    @EventHandler(priority = EventPriority.NORMAL)
    public void onProtectedEntityRightClick(PlayerInteractEntityEvent event) {
        Entity entity = event.getRightClicked();
        if (!boltPlugin.isProtected(entity)) {
            return;
        }
        EntityType entityType = entity.getType();
        Player player = event.getPlayer();
        EntityProtection protection = boltPlugin.loadProtection(entity);
        if (protection == null) {
            return;
        }

        // Determine if player is protection owner
        boolean isOwner = protection.getOwner().equals(player.getUniqueId());
        if (isOwner) {
            // TODO: Open BoltUXGUI
            return;
        }

        boolean canAccess = true;
        if (EntityGroups.chestBoats.contains(entityType)) {
            canAccess = boltPlugin.canAccess(protection, player, Permission.INTERACT, Permission.OPEN, Permission.MOUNT);
        } else if (EntityGroups.containerMinecarts.contains(entityType)) {
            canAccess = boltPlugin.canAccess(protection, player, Permission.INTERACT, Permission.OPEN);
        } else if (EntityGroups.otherInteractableEntities.contains(entityType)) {
            canAccess = boltPlugin.canAccess(protection, player, Permission.INTERACT);
        } else if (EntityGroups.otherEntities.contains(entityType)) {
            canAccess = boltPlugin.canAccess(protection, player, Permission.INTERACT);
        }

        // Make entity glow red if player does not have access
        if (!canAccess) {
            if (GlowingEntity.glowingEntitiesRawMap.containsKey(entity.getEntityId())) {
                return;
            }
            GlowingEntity glowingEntity = new GlowingEntity(entity, player);
            glowingEntity.glow(NamedTextColor.RED);
        }

    }

    // For armor stands only
    @EventHandler(priority = EventPriority.NORMAL)
    public void onProtectedEntityRightClick(PlayerInteractAtEntityEvent event) {
        Entity entity = event.getRightClicked();
        if (entity.getType() != EntityType.ARMOR_STAND) {
            return;
        }
        Player player = event.getPlayer();
        EntityProtection protection = boltPlugin.loadProtection(entity);
        if (protection == null) {
            return;
        }

        // Determine if player is protection owner
        boolean isOwner = protection.getOwner().equals(player.getUniqueId());
        if (isOwner) {
            if (player.isSneaking()) {
                // TODO: Open BoltUXGUI
            }
        }
        boolean canAccess = boltPlugin.canAccess(protection, player, Permission.INTERACT, Permission.OPEN, Permission.MOUNT);

        // Make entity glow red if player does not have access
        if (!canAccess) {
            if (GlowingEntity.glowingEntitiesRawMap.containsKey(entity.getEntityId())) {
                return;
            }
            GlowingEntity glowingEntity = new GlowingEntity(entity, player);
            glowingEntity.glow(NamedTextColor.RED);
        }
    }
}

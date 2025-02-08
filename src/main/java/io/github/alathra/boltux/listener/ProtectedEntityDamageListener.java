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
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.popcraft.bolt.BoltPlugin;
import org.popcraft.bolt.protection.EntityProtection;
import org.popcraft.bolt.util.Permission;

public class ProtectedEntityDamageListener implements Listener {
    private final BoltPlugin boltPlugin;

    public ProtectedEntityDamageListener() {
        boltPlugin = BoltUX.getBoltPlugin();
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
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
        if (EntityGroups.chestBoats.contains(entityType)
            || EntityGroups.containerMinecarts.contains(entityType)
            || EntityGroups.otherInteractableEntities.contains(entityType)
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

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
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
            if (GlowingEntity.glowingEntitiesRawMap.containsKey(entity.getEntityId())) {
                return;
            }
            GlowingEntity glowingEntity = new GlowingEntity(entity, player);
            glowingEntity.glow(NamedTextColor.RED);
        }
    }
}

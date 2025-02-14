package io.github.alathra.boltux.listener;

import io.github.alathra.boltux.BoltUX;
import io.github.alathra.boltux.api.BoltUXAPI;
import io.github.alathra.boltux.config.Settings;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vehicle;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.vehicle.VehicleDamageEvent;
import org.bukkit.event.vehicle.VehicleDestroyEvent;
import org.popcraft.bolt.BoltPlugin;
import org.popcraft.bolt.protection.EntityProtection;
import org.popcraft.bolt.util.Permission;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class ProtectedEntityBreakListener implements Listener {

    private final BoltPlugin boltPlugin;
    private final Set<UUID> protectedVehicleUUIDs;

    public ProtectedEntityBreakListener() {
        boltPlugin = BoltUX.getBoltPlugin();
        protectedVehicleUUIDs = new HashSet<>();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onProtectedEntityDeath(EntityDeathEvent event) {
        if (event.isCancelled()) {
            return;
        }
        if (!Settings.isLockingEnabled()) {
            return;
        }
        if (!Settings.isLockDroppingEnabled()) {
            return;
        }
        Entity entity = event.getEntity();
        if (!boltPlugin.isProtected(entity)) {
            return;
        }

        // Drop a lock item at killed entity location
        entity.getWorld().dropItemNaturally(entity.getLocation(), BoltUXAPI.getLockItem());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onProtectedHangingEntityBreak(HangingBreakByEntityEvent event) {
        if (event.isCancelled()) {
            return;
        }
        if (!Settings.isLockingEnabled()) {
            return;
        }
        if (!Settings.isLockDroppingEnabled()) {
            return;
        }
        if (!(event.getRemover() instanceof Player)) {
            return;
        }
        Entity entity = event.getEntity();
        if (!boltPlugin.isProtectable(entity)) {
            return;
        }

        // Drop a lock item at the broken block location
        entity.getWorld().dropItemNaturally(entity.getLocation(), BoltUXAPI.getLockItem());
    }

    // BoltAPI can track the entity here, but not in VehicleDestroyEvent. Chaining events is needed.
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onProtectedVehicleDamage(VehicleDamageEvent event) {
        if (event.isCancelled()) {
            return;
        }
        // Prevents potential duplication of lock item
        Vehicle vehicle = event.getVehicle();
        protectedVehicleUUIDs.remove(vehicle.getUniqueId());
        if (!Settings.isLockingEnabled()) {
            return;
        }
        if (!Settings.isLockDroppingEnabled()) {
            return;
        }
        if (!(event.getAttacker() instanceof Player)) {
            return;
        }
        if (boltPlugin.isProtected(vehicle)) {
            protectedVehicleUUIDs.add(vehicle.getUniqueId());
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onProtectedVehicleDestroy(VehicleDestroyEvent event) {
        if (event.isCancelled()) {
            return;
        }
        if (!Settings.isLockingEnabled()) {
            return;
        }
        if (!Settings.isLockDroppingEnabled()) {
            return;
        }
        if (!(event.getAttacker() instanceof Player)) {
            return;
        }
        Vehicle vehicle = event.getVehicle();
        // Vehicle was protected
        if (protectedVehicleUUIDs.contains(vehicle.getUniqueId())) {
            // Drop a lock item at the broken block location
            vehicle.getWorld().dropItemNaturally(vehicle.getLocation(), BoltUXAPI.getLockItem());
            protectedVehicleUUIDs.remove(vehicle.getUniqueId());
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onProtectedLeadInteract(PlayerInteractEntityEvent event) {
        if (event.isCancelled()) {
            return;
        }
        if (!Settings.isLockingEnabled()) {
            return;
        }
        if (!Settings.isLockDroppingEnabled()) {
            return;
        }
        Entity entity = event.getRightClicked();
        if (!entity.getType().equals(EntityType.LEASH_KNOT)) {
            return;
        }
        if (!boltPlugin.isProtected(entity)) {
            return;
        }
        EntityProtection protection = boltPlugin.loadProtection(entity);
        if (protection == null) {
            return;
        }
        Player player = event.getPlayer();
        if (boltPlugin.canAccess(protection, event.getPlayer(), Permission.INTERACT)) {
            // Drop a lock item at the broken block location
            entity.getWorld().dropItemNaturally(entity.getLocation(), BoltUXAPI.getLockItem());
        }
    }

}

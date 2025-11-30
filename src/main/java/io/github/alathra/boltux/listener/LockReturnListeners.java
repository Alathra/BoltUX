package io.github.alathra.boltux.listener;

import io.github.alathra.boltux.BoltUX;
import io.github.alathra.boltux.api.BoltUXAPI;
import io.github.alathra.boltux.config.Settings;
import io.github.alathra.boltux.data.MaterialGroups;
import io.github.alathra.boltux.utility.BlockUtil;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.vehicle.VehicleDamageEvent;
import org.bukkit.event.vehicle.VehicleDestroyEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.popcraft.bolt.BoltPlugin;
import org.popcraft.bolt.protection.EntityProtection;
import org.popcraft.bolt.protection.Protection;
import org.popcraft.bolt.util.Permission;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class LockReturnListeners implements Listener {

    private final BoltPlugin boltPlugin;
    private final Set<UUID> protectedVehicleUUIDs;

    public LockReturnListeners() {
        boltPlugin = BoltUX.getBoltPlugin();
        protectedVehicleUUIDs = new HashSet<>();
    }

    @SuppressWarnings("unused")
    @EventHandler(priority = EventPriority.LOW)
    public void onProtectedBlockBreak(BlockBreakEvent e) {
        if (e.isCancelled()) {
            return;
        }
        if (!Settings.isLockItemEnabled()) {
            return;
        }
        if (!Settings.isLockDroppingEnabled()) {
            return;
        }
        Block block = e.getBlock();
        if (!Settings.getLockItemEnabledWorlds().contains(block.getWorld())) {
            return;
        }

        if (!boltPlugin.isProtected(block)) {
            return;
        }
        // check for double chest and return, so locks aren't duped
        if (block.getType().equals(Material.CHEST) || block.getType().equals(Material.TRAPPED_CHEST)) {
            Chest chest = (Chest) block.getState();
            if (BlockUtil.isDoubleChest(chest.getInventory())) {
                return;
            }
        }
        Protection protection = boltPlugin.findProtection(block);
        if (protection == null) {
            return;
        }
        Player player = e.getPlayer();
        if (!protection.getOwner().equals(player.getUniqueId())) {
            if (!boltPlugin.canAccess(block, player, Permission.DESTROY)) {
                return;
            }
        }
        Material material = block.getType();
        if (!MaterialGroups.containerBlocks.contains(material) && !MaterialGroups.interactableBlocks.contains(material) && !MaterialGroups.otherBlocks.contains(material)) {
            return;
        }


        // Drop a lock item at the broken block location
        block.getWorld().dropItemNaturally(block.getLocation(), BoltUXAPI.getLockItem());
    }

    @SuppressWarnings("unused")
    @EventHandler(priority = EventPriority.LOW)
    public void onProtectedEntityDeath(EntityDeathEvent e) {
        if (e.isCancelled()) {
            return;
        }
        if (!Settings.isLockItemEnabled()) {
            return;
        }
        if (!Settings.isLockDroppingEnabled()) {
            return;
        }
        Entity entity = e.getEntity();
        if (!Settings.getLockItemEnabledWorlds().contains(entity.getWorld())) {
            return;
        }
        if (!boltPlugin.isProtected(entity)) {
            return;
        }

        // Drop a lock item at killed entity location
        entity.getWorld().dropItemNaturally(entity.getLocation(), BoltUXAPI.getLockItem());
    }

    @SuppressWarnings("unused")
    @EventHandler(priority = EventPriority.LOW)
    public void onProtectedHangingEntityBreak(HangingBreakByEntityEvent e) {
        if (e.isCancelled()) {
            return;
        }
        if (!Settings.isLockItemEnabled()) {
            return;
        }
        if (!Settings.isLockDroppingEnabled()) {
            return;
        }
        if (!(e.getRemover() instanceof Player player)) {
            return;
        }
        Entity entity = e.getEntity();
        if (!Settings.getLockItemEnabledWorlds().contains(entity.getWorld())) {
            return;
        }
        Protection protection = boltPlugin.findProtection(entity);
        if (protection == null) {
            return;
        }
        if (!protection.getOwner().equals(player.getUniqueId())) {
            if (!boltPlugin.canAccess(entity, player, Permission.DESTROY)) {
                return;
            }
        }

        // Drop a lock item at the broken block location
        entity.getWorld().dropItemNaturally(entity.getLocation(), BoltUXAPI.getLockItem());
    }

    // BoltAPI can track the entity here, but not in VehicleDestroyEvent. Chaining events is needed.
    @SuppressWarnings("unused")
    @EventHandler(priority = EventPriority.LOW)
    public void onProtectedVehicleDamage(VehicleDamageEvent e) {
        if (e.isCancelled()) {
            return;
        }
        // Prevents potential duplication of lock item
        Vehicle vehicle = e.getVehicle();
        protectedVehicleUUIDs.remove(vehicle.getUniqueId());
        if (!Settings.isLockItemEnabled()) {
            return;
        }
        if (!Settings.isLockDroppingEnabled()) {
            return;
        }
        if (!(e.getAttacker() instanceof Player)) {
            return;
        }
        if (!Settings.getLockItemEnabledWorlds().contains(vehicle.getWorld())) {
            return;
        }
        if (boltPlugin.isProtected(vehicle)) {
            protectedVehicleUUIDs.add(vehicle.getUniqueId());
        }
    }

    @SuppressWarnings("unused")
    @EventHandler(priority = EventPriority.LOW)
    public void onProtectedVehicleDestroy(VehicleDestroyEvent e) {
        if (e.isCancelled()) {
            return;
        }
        if (!Settings.isLockItemEnabled()) {
            return;
        }
        if (!Settings.isLockDroppingEnabled()) {
            return;
        }
        if (!(e.getAttacker() instanceof Player)) {
            return;
        }
        Vehicle vehicle = e.getVehicle();
        if (!Settings.getLockItemEnabledWorlds().contains(vehicle.getWorld())) {
            return;
        }
        // Vehicle was protected
        if (protectedVehicleUUIDs.contains(vehicle.getUniqueId())) {
            // Drop a lock item at the broken block location
            vehicle.getWorld().dropItemNaturally(vehicle.getLocation(), BoltUXAPI.getLockItem());
            protectedVehicleUUIDs.remove(vehicle.getUniqueId());
        }
    }

    @SuppressWarnings("unused")
    @EventHandler(priority = EventPriority.LOW)
    public void onProtectedLeadInteract(PlayerInteractEntityEvent e) {
        if (e.isCancelled()) {
            return;
        }
        if (!Settings.isLockItemEnabled()) {
            return;
        }
        if (!Settings.isLockDroppingEnabled()) {
            return;
        }
        if (e.getHand().equals(EquipmentSlot.OFF_HAND)) {
            return;
        }
        Entity entity = e.getRightClicked();
        if (!entity.getType().equals(EntityType.LEASH_KNOT)) {
            return;
        }
        if (!Settings.getLockItemEnabledWorlds().contains(entity.getWorld())) {
            return;
        }
        if (!boltPlugin.isProtected(entity)) {
            return;
        }
        EntityProtection protection = boltPlugin.loadProtection(entity);
        if (protection == null) {
            return;
        }
        Player player = e.getPlayer();
        if (boltPlugin.canAccess(protection, e.getPlayer(), Permission.INTERACT)) {
            // Drop a lock item at the broken block location
            entity.getWorld().dropItemNaturally(entity.getLocation(), BoltUXAPI.getLockItem());
        }
    }

}

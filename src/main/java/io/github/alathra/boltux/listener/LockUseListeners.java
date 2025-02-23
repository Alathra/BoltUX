package io.github.alathra.boltux.listener;

import io.github.alathra.boltux.BoltUX;
import io.github.alathra.boltux.api.BoltUXAPI;
import io.github.alathra.boltux.config.Settings;
import io.github.alathra.boltux.packets.GlowingBlock;
import io.github.alathra.boltux.packets.GlowingEntity;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.popcraft.bolt.BoltPlugin;
import org.popcraft.bolt.protection.Protection;
import org.popcraft.bolt.util.BoltPlayer;

import java.util.*;

public class LockUseListeners implements Listener {

    private final BoltPlugin boltPlugin;
    // Player UUID, entity UUID

    public LockUseListeners() {
        boltPlugin = BoltUX.getBoltPlugin();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onLockUseOnBlock(PlayerInteractEvent event) {
        if (!Settings.isLockingEnabled()) {
            return;
        }
        Player player = event.getPlayer();
        if (!Settings.getLockItemEnabledWorlds().contains(player.getWorld())) {
            return;
        }
        if (!event.getAction().equals(org.bukkit.event.block.Action.RIGHT_CLICK_BLOCK)) {
            return;
        }
        if (event.getHand() == null) {
            return;
        }
        if (event.getHand().equals(EquipmentSlot.OFF_HAND)) {
            return;
        }
        if (!BoltUXAPI.isLockItem(player.getInventory().getItemInMainHand())) {
            return;
        }
        Block block = event.getClickedBlock();
        if (block == null) {
            return;
        }
        ItemStack lockItem = player.getInventory().getItemInMainHand();
        if (!player.isSneaking()) {
            return;
        }
        if (boltPlugin.isProtected(block)) {
            return;
        }
        if (!boltPlugin.isProtectable(block)) {
            return;
        }

        // Player is using lock item on a valid block
        GlowingBlock glowingBlock = new GlowingBlock(block, player);
        glowingBlock.glow(NamedTextColor.GREEN);
        if (Settings.isLockingSoundEnabled()) {
            player.playSound(Settings.getLockingSound());
        }
        lockItem.setAmount(lockItem.getAmount() - 1);

        // Create new protection
        org.popcraft.bolt.util.Action lockAction = new org.popcraft.bolt.util.Action(org.popcraft.bolt.util.Action.Type.LOCK, "bolt.command.lock", "private", false);
        BoltPlayer boltPlayer = boltPlugin.player(player.getUniqueId());
        final UUID protectionUUID = boltPlayer.isLockNil() ? org.popcraft.bolt.util.Profiles.NIL_UUID : player.getUniqueId();
        final String protectionType = Optional.ofNullable(lockAction.getData())
            .flatMap(type -> boltPlugin.getBolt().getAccessRegistry().getProtectionByType(type))
            .map(org.popcraft.bolt.access.Access::type)
            .orElse(boltPlugin.getDefaultProtectionType());
        final Protection protection = boltPlugin.createProtection(block, protectionUUID, protectionType);
        boltPlugin.saveProtection(protection);
        boltPlayer.setLockNil(false);

        /*
        BoltComponents.sendMessage(
            player,
            Translation.CLICK_ACTION,
            boltPlugin.isUseActionBar(),
            Placeholder.component(
                Translation.Placeholder.ACTION, resolveTranslation(Translation.LOCK, player)
            )
        );
         */

        event.setCancelled(true);
    }

    // For anything but armor stands
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onLockUseOnEntity(PlayerInteractEntityEvent event) {
        if (event.isCancelled()) {
            return;
        }
        if (!Settings.isLockingEnabled()) {
            return;
        }
        Player player = event.getPlayer();
        if (!Settings.getLockItemEnabledWorlds().contains(player.getWorld())) {
            return;
        }
        Entity entity = event.getRightClicked();
        if (event.getHand().equals(EquipmentSlot.OFF_HAND)) {
            return;
        }
        if (!BoltUXAPI.isLockItem(player.getInventory().getItemInMainHand())) {
            return;
        }
        ItemStack lockItem = player.getInventory().getItemInMainHand();
        if (!player.isSneaking()) {
            return;
        }
        if (boltPlugin.isProtected(entity)) {
            return;
        }
        if (!boltPlugin.isProtectable(entity)) {
            return;
        }

        GlowingEntity glowingEntity = new GlowingEntity(entity, player);
        glowingEntity.glow(NamedTextColor.GREEN);
        if (Settings.isLockingSoundEnabled()) {
            player.playSound(Settings.getLockingSound());
        }
        lockItem.setAmount(lockItem.getAmount() - 1);

        // Create new protection
        org.popcraft.bolt.util.Action lockAction = new org.popcraft.bolt.util.Action(org.popcraft.bolt.util.Action.Type.LOCK, "bolt.command.lock", "private", false);
        BoltPlayer boltPlayer = boltPlugin.player(player.getUniqueId());
        final UUID protectionUUID = boltPlayer.isLockNil() ? org.popcraft.bolt.util.Profiles.NIL_UUID : player.getUniqueId();
        final String protectionType = Optional.ofNullable(lockAction.getData())
            .flatMap(type -> boltPlugin.getBolt().getAccessRegistry().getProtectionByType(type))
            .map(org.popcraft.bolt.access.Access::type)
            .orElse(boltPlugin.getDefaultProtectionType());
        final Protection protection = boltPlugin.createProtection(entity, protectionUUID, protectionType);
        boltPlugin.saveProtection(protection);
        boltPlayer.setLockNil(false);

        event.setCancelled(true);

    }

    // For anything but armor stands
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onLockUseOnEntity(PlayerInteractAtEntityEvent event) {
        if (event.isCancelled()) {
            return;
        }
        if (!Settings.isLockingEnabled()) {
            return;
        }
        Player player = event.getPlayer();
        if (!Settings.getLockItemEnabledWorlds().contains(player.getWorld())) {
            return;
        }
        Entity entity = event.getRightClicked();
        if (entity.getType() != EntityType.ARMOR_STAND) {
            return;
        }
        if (event.getHand().equals(EquipmentSlot.OFF_HAND)) {
            return;
        }
        if (!BoltUXAPI.isLockItem(player.getInventory().getItemInMainHand())) {
            return;
        }
        ItemStack lockItem = player.getInventory().getItemInMainHand();
        if (!player.isSneaking()) {
            return;
        }
        if (boltPlugin.isProtected(entity)) {
            return;
        }
        if (!boltPlugin.isProtectable(entity)) {
            return;
        }

        GlowingEntity glowingEntity = new GlowingEntity(entity, player);
        glowingEntity.glow(NamedTextColor.GREEN);
        if (Settings.isLockingSoundEnabled()) {
            player.playSound(Settings.getLockingSound());
        }
        lockItem.setAmount(lockItem.getAmount() - 1);

        // Create new protection
        org.popcraft.bolt.util.Action lockAction = new org.popcraft.bolt.util.Action(org.popcraft.bolt.util.Action.Type.LOCK, "bolt.command.lock", "private", false);
        BoltPlayer boltPlayer = boltPlugin.player(player.getUniqueId());
        final UUID protectionUUID = boltPlayer.isLockNil() ? org.popcraft.bolt.util.Profiles.NIL_UUID : player.getUniqueId();
        final String protectionType = Optional.ofNullable(lockAction.getData())
            .flatMap(type -> boltPlugin.getBolt().getAccessRegistry().getProtectionByType(type))
            .map(org.popcraft.bolt.access.Access::type)
            .orElse(boltPlugin.getDefaultProtectionType());
        final Protection protection = boltPlugin.createProtection(entity, protectionUUID, protectionType);
        boltPlugin.saveProtection(protection);
        boltPlayer.setLockNil(false);

        event.setCancelled(true);

    }
}
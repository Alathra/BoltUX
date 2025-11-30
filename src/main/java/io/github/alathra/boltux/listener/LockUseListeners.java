package io.github.alathra.boltux.listener;

import com.destroystokyo.paper.MaterialTags;
import io.github.milkdrinkers.colorparser.paper.ColorParser;
import io.github.alathra.boltux.BoltUX;
import io.github.alathra.boltux.api.BoltUXAPI;
import io.github.alathra.boltux.config.Settings;
import io.github.alathra.boltux.data.Permissions;
import io.github.alathra.boltux.hook.Hook;
import io.github.alathra.boltux.hook.quickshop.QuickShopHook;
import io.github.alathra.boltux.packets.GlowingBlock;
import io.github.alathra.boltux.packets.GlowingEntity;
import io.github.alathra.boltux.packets.GlowingEntityTracker;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Bisected;
import org.bukkit.block.data.type.Door;
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

    @SuppressWarnings("unused")
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onLockUseOnBlock(PlayerInteractEvent e) {
        if (!Settings.isLockItemEnabled()) {
            return;
        }
        Player player = e.getPlayer();
        if (!Settings.getLockItemEnabledWorlds().contains(player.getWorld())) {
            return;
        }
        if (!e.getAction().equals(org.bukkit.event.block.Action.RIGHT_CLICK_BLOCK)) {
            return;
        }
        if (e.getHand() == null) {
            return;
        }
        if (e.getHand().equals(EquipmentSlot.OFF_HAND)) {
            return;
        }
        if (!BoltUXAPI.isLockItem(player.getInventory().getItemInMainHand())) {
            return;
        }
        Block block = e.getClickedBlock();
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
        if (!player.hasPermission(Permissions.LOCK_PERMISSION)) {
            player.sendMessage(ColorParser.of("<red>You do not have permission to use locks").build());
            return;
        }

        // Towny Compatibility
        if (Hook.Towny.isLoaded()) {
            if (!Hook.getTownyHook().canCreateProtection(true, player, block.getLocation())) {
                player.sendMessage(ColorParser.of("<red>You do not have permission to use locks here").build());
                return;
            }
        }

        // QuickShop Compatibility
        if (Hook.QuickShop.isLoaded()) {
            if(Hook.getQuickShopHook().isQuickShop(block.getLocation()) && Settings.isQuickShopLockingDisabled()) {
                player.sendMessage(ColorParser.of("<red>You cannot lock a QuickShop!").build());
                return;
            }
        }

        // Player is using lock item on a valid block
        if (Hook.PacketEvents.isLoaded()) {
            GlowingBlock glowingBlock = new GlowingBlock(block, player);
            glowingBlock.glow(NamedTextColor.GREEN);
        }

        if (Settings.isLockingSoundEnabled()) {
            player.playSound(Settings.getLockingSound());
        }
        lockItem.setAmount(lockItem.getAmount() - 1);

        Block protectionBlock = block;
        if (MaterialTags.DOORS.isTagged(block.getType())) {
            Door door = (Door) block.getBlockData();
            if (door.getHalf().equals(Bisected.Half.TOP)) {
                protectionBlock = block.getRelative(BlockFace.DOWN);
            }
        }

        // Create new protection
        BoltPlayer boltPlayer = boltPlugin.player(player.getUniqueId());
        final UUID protectionUUID = boltPlayer.isLockNil() ? org.popcraft.bolt.util.Profiles.NIL_UUID : player.getUniqueId();
        final Protection protection = boltPlugin.createProtection(protectionBlock, protectionUUID, "private");
        boltPlugin.saveProtection(protection);
        boltPlayer.setLockNil(false);

        player.sendMessage(ColorParser.of("<green>Protection has been created").build());

        e.setCancelled(true);
    }

    // For anything but armor stands
    @SuppressWarnings("unused")
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onLockUseOnEntity(PlayerInteractEntityEvent e) {
        if (e.isCancelled()) {
            return;
        }
        if (!Settings.isLockItemEnabled()) {
            return;
        }
        Player player = e.getPlayer();
        if (!Settings.getLockItemEnabledWorlds().contains(player.getWorld())) {
            return;
        }
        Entity entity = e.getRightClicked();
        if (e.getHand().equals(EquipmentSlot.OFF_HAND)) {
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
        if (!player.hasPermission(Permissions.LOCK_PERMISSION)) {
            player.sendMessage(ColorParser.of("<red>You do not have permission to use locks").build());
            return;
        }

        // Towny Compatibility
        if (Hook.Towny.isLoaded()) {
            if (!Hook.getTownyHook().canCreateProtection(true, player, entity.getLocation())) {
                player.sendMessage(ColorParser.of("<red>You do not have permission to use locks here").build());
                return;
            }
        }

        // QuickShop Compatibility
        if (Hook.QuickShop.isLoaded()) {
            if(Hook.getQuickShopHook().isQuickShop(entity.getLocation()) && Settings.isQuickShopLockingDisabled()) {
                player.sendMessage(ColorParser.of("<red>You cannot lock a QuickShop!").build());
                return;
            }
        }

        if (Hook.PacketEvents.isLoaded()) {
            new GlowingEntity(entity, player, NamedTextColor.GREEN);
        }

        if (Settings.isLockingSoundEnabled()) {
            player.playSound(Settings.getLockingSound());
        }
        lockItem.setAmount(lockItem.getAmount() - 1);

        // Create new protection
        BoltPlayer boltPlayer = boltPlugin.player(player.getUniqueId());
        final UUID protectionUUID = boltPlayer.isLockNil() ? org.popcraft.bolt.util.Profiles.NIL_UUID : player.getUniqueId();
        final Protection protection = boltPlugin.createProtection(entity, protectionUUID, "private");
        boltPlugin.saveProtection(protection);
        boltPlayer.setLockNil(false);

        player.sendMessage(ColorParser.of("<green>Protection has been created").build());

        e.setCancelled(true);
    }

    // For anything but armor stands
    @SuppressWarnings("unused")
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onLockUseOnEntity(PlayerInteractAtEntityEvent e) {
        if (e.isCancelled()) {
            return;
        }
        if (!Settings.isLockItemEnabled()) {
            return;
        }
        Player player = e.getPlayer();
        if (!Settings.getLockItemEnabledWorlds().contains(player.getWorld())) {
            return;
        }
        Entity entity = e.getRightClicked();
        if (entity.getType() != EntityType.ARMOR_STAND) {
            return;
        }
        if (e.getHand().equals(EquipmentSlot.OFF_HAND)) {
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
        if (!player.hasPermission(Permissions.LOCK_PERMISSION)) {
            player.sendMessage(ColorParser.of("<red>You do not have permission to use locks").build());
            return;
        }

        // Towny Compatibility
        if (Hook.Towny.isLoaded()) {
            if (!Hook.getTownyHook().canCreateProtection(true, player, entity.getLocation())) {
                player.sendMessage(ColorParser.of("<red>You do not have permission to use locks here").build());
                return;
            }
        }

        // QuickShop Compatibility
        if (Hook.QuickShop.isLoaded()) {
            if(Hook.getQuickShopHook().isQuickShop(entity.getLocation()) && Settings.isQuickShopLockingDisabled()) {
                player.sendMessage(ColorParser.of("<red>You cannot lock a QuickShop!").build());
                return;
            }
        }

        if (Hook.PacketEvents.isLoaded()) {
            new GlowingEntity(entity, player, NamedTextColor.GREEN);
        }

        if (Settings.isLockingSoundEnabled()) {
            player.playSound(Settings.getLockingSound());
        }
        lockItem.setAmount(lockItem.getAmount() - 1);

        // Create new protection
        BoltPlayer boltPlayer = boltPlugin.player(player.getUniqueId());
        final UUID protectionUUID = boltPlayer.isLockNil() ? org.popcraft.bolt.util.Profiles.NIL_UUID : player.getUniqueId();
        final Protection protection = boltPlugin.createProtection(entity, protectionUUID, "private");
        boltPlugin.saveProtection(protection);
        boltPlayer.setLockNil(false);

        player.sendMessage(ColorParser.of("<green>Protection has been created").build());

        e.setCancelled(true);
    }
}
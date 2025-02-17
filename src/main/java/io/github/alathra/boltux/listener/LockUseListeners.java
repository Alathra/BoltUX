package io.github.alathra.boltux.listener;

import io.github.alathra.boltux.BoltUX;
import io.github.alathra.boltux.api.BoltUXAPI;
import io.github.alathra.boltux.config.Settings;
import io.github.alathra.boltux.packets.GlowingBlock;
import io.github.alathra.boltux.packets.GlowingEntity;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.popcraft.bolt.BoltPlugin;
import org.popcraft.bolt.lang.Translation;
import org.popcraft.bolt.util.BoltComponents;
import org.popcraft.bolt.util.BoltPlayer;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class LockUseListeners implements Listener {

    private final BoltPlugin boltPlugin;
    // Player UUID, entity UUID
    private final Map<UUID, UUID> pendingLocking;

    public LockUseListeners() {
        boltPlugin = BoltUX.getBoltPlugin();
        pendingLocking = new HashMap<>();
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

        // https://github.com/pop4959/Bolt/blob/22bf46b45640e0ef75dbd38bc5c16cee2db267c0/bukkit/src/main/java/org/popcraft/bolt/command/impl/LockCommand.java#L19
        boltPlugin.player(player).setAction(new org.popcraft.bolt.util.Action(org.popcraft.bolt.util.Action.Type.LOCK, "bolt.command.lock", "private", false));
        // Bolt-Bukkit incapable of calling this, bolt-common will
        try {
            BoltComponents.sendMessage(
                player,
                Translation.CLICK_ACTION,
                boltPlugin.isUseActionBar(),
                Placeholder.component(
                    Translation.Placeholder.ACTION, BoltComponents.resolveTranslation(Translation.LOCK, player)
                )
            );
        } catch (NoSuchMethodError ignored) {}

        event.setCancelled(true);
    }

    // For armor stands only
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
        if (pendingLocking.containsKey(player.getUniqueId())) {
            pendingLocking.remove(player.getUniqueId());
            event.setCancelled(true);
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

        if (GlowingEntity.glowingEntitiesRawMap.containsKey(entity.getEntityId())) {
            return;
        }
        GlowingEntity glowingEntity = new GlowingEntity(entity, player);
        glowingEntity.glow(NamedTextColor.GREEN);
        if (Settings.isLockingSoundEnabled()) {
            player.playSound(Settings.getLockingSound());
        }
        lockItem.setAmount(lockItem.getAmount() - 1);

        // https://github.com/pop4959/Bolt/blob/22bf46b45640e0ef75dbd38bc5c16cee2db267c0/bukkit/src/main/java/org/popcraft/bolt/command/impl/LockCommand.java#L19
        BoltPlayer boltPlayer = boltPlugin.player(player.getUniqueId());
        boltPlayer.setAction(new org.popcraft.bolt.util.Action(org.popcraft.bolt.util.Action.Type.LOCK, "bolt.command.lock", "private", false));
        pendingLocking.remove(player.getUniqueId());
        pendingLocking.put(player.getUniqueId(), entity.getUniqueId());
        Bukkit.getScheduler().runTaskLater(BoltUX.getInstance(), () -> {
            Bukkit.getServer().getPluginManager().callEvent(new PlayerInteractAtEntityEvent(player, entity, event.getClickedPosition()));
        }, 1);

        // Bolt-Bukkit incapable of calling this, bolt-common will
        try {
            BoltComponents.sendMessage(
                player,
                Translation.CLICK_ACTION,
                boltPlugin.isUseActionBar(),
                Placeholder.component(
                    Translation.Placeholder.ACTION, BoltComponents.resolveTranslation(Translation.LOCK, player)
                )
            );
        } catch (NoSuchMethodError ignored) {}

        event.setCancelled(true);

    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInventoryOpen(InventoryOpenEvent event) {
        if (pendingLocking.containsKey(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
            return;
        }
    }
}
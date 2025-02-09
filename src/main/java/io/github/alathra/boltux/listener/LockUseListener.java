package io.github.alathra.boltux.listener;

import com.github.milkdrinkers.colorparser.ColorParser;
import io.github.alathra.boltux.BoltUX;
import io.github.alathra.boltux.api.BoltUXAPI;
import io.github.alathra.boltux.config.Settings;
import io.github.alathra.boltux.packets.GlowingBlock;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.popcraft.bolt.BoltPlugin;

public class LockUseListener implements Listener {

    private final BoltPlugin boltPlugin;

    public LockUseListener() {
        boltPlugin = BoltUX.getBoltPlugin();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onLockUse(PlayerInteractEvent event) {
        if (!Settings.isLockingEnabled()) {
            return;
        }
        if (!event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
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
        Player player = event.getPlayer();
        if (!Settings.getLockItemEnabledWorlds().contains(player.getWorld())) {
            return;
        }
        if (!BoltUXAPI.isLockItem(player.getInventory().getItemInMainHand())) {
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
        boltPlugin.createProtection(block, player.getUniqueId(), "private");
        GlowingBlock glowingBlock = new GlowingBlock(block, player);
        glowingBlock.glow(NamedTextColor.GREEN);
        //player.playSound(Settings.getLockingSound());
        lockItem.setAmount(lockItem.getAmount() - 1);
        event.setCancelled(true);
        player.sendMessage(ColorParser.of("<white>Locked <yellow>Private <block_type>")
            .parseMinimessagePlaceholder("block_type", block.getType().name())
            .build());
    }
}

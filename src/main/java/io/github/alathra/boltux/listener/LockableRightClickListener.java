package io.github.alathra.boltux.listener;

import io.github.alathra.boltux.BoltUX;
import io.github.alathra.boltux.packets.GlowingBlock;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

public class LockableRightClickListener implements Listener {

    @EventHandler
    public void onLockableClick(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        if (event.getHand() == null) {
            return;
        }
        if (event.getHand().equals(EquipmentSlot.OFF_HAND)) {
            return;
        }
        Player player = event.getPlayer();
        Block block = event.getClickedBlock();
        if (block == null) {
            return;
        }
        GlowingBlock glowingBlock = new GlowingBlock(block, player);
        glowingBlock.place();
        Bukkit.getScheduler().runTaskLater(BoltUX.getInstance(), glowingBlock::remove, 100L); // 5 seconds
    }
}

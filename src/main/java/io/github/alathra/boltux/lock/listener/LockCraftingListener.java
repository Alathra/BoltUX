package io.github.alathra.boltux.lock.listener;

import io.github.alathra.boltux.lock.CraftingHandler;
import io.github.milkdrinkers.colorparser.paper.ColorParser;
import io.github.alathra.boltux.data.Permissions;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;

public final class LockCraftingListener implements Listener {
    @SuppressWarnings("unused")
    @EventHandler(priority = EventPriority.NORMAL)
    public void CraftListener(CraftItemEvent event) {
        if (event.getCurrentItem() == null) {
            return;
        }

        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }

        if (player.hasPermission(Permissions.ADMIN_PERMISSION)) {
            return;
        }

        if (event.getRecipe().equals(CraftingHandler.getLockRecipe())) {
            player = (Player) event.getWhoClicked();
            if (!player.hasPermission(Permissions.CRAFT_PERMISSION)) {
                event.setCancelled(true);
                player.sendMessage(ColorParser.of("<red>You do not have permission to craft locks").build());
            }
        }
    }
}

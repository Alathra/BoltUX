package io.github.alathra.boltux.listener;

import com.destroystokyo.paper.MaterialTags;
import io.github.alathra.boltux.BoltUX;
import io.github.alathra.boltux.data.EntityGroups;
import io.github.alathra.boltux.data.MaterialGroups;
import io.github.alathra.boltux.data.Permissions;
import io.github.alathra.boltux.gui.GuiHandler;
import io.github.alathra.boltux.packets.GlowingBlock;
import io.github.alathra.boltux.packets.GlowingEntity;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.popcraft.bolt.BoltPlugin;
import org.popcraft.bolt.protection.BlockProtection;
import org.popcraft.bolt.protection.EntityProtection;
import org.popcraft.bolt.util.Permission;

public class ProtectionInteractListeners implements Listener {

    private final BoltPlugin boltPlugin;

    public ProtectionInteractListeners() {
        boltPlugin = BoltUX.getBoltPlugin();
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onProtectedBlockRightClick(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
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
        if (!boltPlugin.isProtected(block)) {
            return;
        }
        Player player = event.getPlayer();
        Material material = block.getType();
        BlockProtection protection = boltPlugin.loadProtection(block);
        if (protection == null) {
            if (MaterialTags.DOORS.isTagged(material)) {
                protection = boltPlugin.loadProtection(block.getRelative(BlockFace.DOWN));
            } else {
                return;
            }
        }

        // Determine if player is protection owner
        boolean isOwner = protection.getOwner().equals(player.getUniqueId());
        if (isOwner || player.hasPermission(Permissions.ADMIN_PERMISSION)) {
            if (player.isSneaking() && player.getInventory().getItemInMainHand().getType().equals(Material.AIR)) {
                GuiHandler.generateMainMenu(player, protection, block.getLocation());
                event.setCancelled(true);
                return;
            }
            return;
        }

        boolean canAccess = true;
        if (MaterialGroups.containerBlocks.contains(material)) {
            canAccess = boltPlugin.canAccess(protection, player, Permission.INTERACT, Permission.OPEN);
        } else if(MaterialGroups.interactableBlocks.contains(material)) {
            canAccess = boltPlugin.canAccess(protection, player, Permission.INTERACT);
        } else if (MaterialGroups.otherBlocks.contains(material)) {
            canAccess = boltPlugin.canAccess(protection, player, Permission.INTERACT);
        }

        // Display red glowing block if player does not have access
        if (!canAccess) {
            GlowingBlock glowingBlock = new GlowingBlock(block, player);
            glowingBlock.glow(NamedTextColor.RED);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onProtectedEntityRightClick(PlayerInteractEntityEvent event) {
        if (event.getHand().equals(EquipmentSlot.OFF_HAND)) {
            return;
        }
        Entity entity = event.getRightClicked();
        Player player = event.getPlayer();
        EntityProtection protection = boltPlugin.loadProtection(entity);
        if (protection == null) {
            return;
        }

        // Determine if player is protection owner
        boolean isOwner = protection.getOwner().equals(player.getUniqueId());
        if (isOwner || player.hasPermission(Permissions.ADMIN_PERMISSION)) {
            if (player.isSneaking() && player.getInventory().getItemInMainHand().getType().equals(Material.AIR)) {
                GuiHandler.generateMainMenu(player, protection, entity.getLocation());
                event.setCancelled(true);
                return;
            }
            return;
        }
        EntityType entityType = entity.getType();

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

    @EventHandler(priority = EventPriority.NORMAL)
    public void onProtectedEntityRightClick(PlayerInteractAtEntityEvent event) {
        if (event.getHand().equals(EquipmentSlot.OFF_HAND)) {
            return;
        }
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
        if (isOwner || player.hasPermission(Permissions.ADMIN_PERMISSION)) {
            if (player.isSneaking() && player.getInventory().getItemInMainHand().getType().equals(Material.AIR)) {
                GuiHandler.generateMainMenu(player, protection, entity.getLocation());
                event.setCancelled(true);
                return;
            }
            return;
        }

        boolean canAccess = boltPlugin.canAccess(protection, player, Permission.INTERACT);

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

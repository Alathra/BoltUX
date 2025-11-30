package io.github.alathra.boltux.packets;

import com.destroystokyo.paper.MaterialTags;
import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes;
import io.github.alathra.boltux.BoltUX;
import io.github.alathra.boltux.config.Settings;
import io.github.alathra.boltux.utility.BlockUtil;
import io.github.retrooper.packetevents.util.SpigotConversionUtil;
import me.tofaa.entitylib.meta.mobs.cuboid.SlimeMeta;
import me.tofaa.entitylib.wrapper.WrapperEntity;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.data.Bisected;
import org.bukkit.block.data.type.Door;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class GlowingBlock {
    private final Block block;
    private final Player player;
    private final Set<WrapperEntity> entities = ConcurrentHashMap.newKeySet();

    public GlowingBlock(Block block, Player player) {
        this.block = block;
        this.player = player;
    }

    public void glow(NamedTextColor color) {
        placeEntities();
        setTeam(color);
        Bukkit.getScheduler().runTaskLaterAsynchronously(BoltUX.getInstance(), () -> removeEntities(color), Settings.getGlowBlockTime() * 20L);
    }

    private void placeEntities() {
        List<Location> entityLocations = new ArrayList<>();
        entityLocations.add(block.getLocation().add(0.5, 0.0, 0.5));

        if (MaterialTags.DOORS.isTagged(block.getType())) {
            Door door = (Door) block.getBlockData();
            if (door.getHalf().equals(Bisected.Half.TOP)) {
                entityLocations.add(block.getLocation().add(0.0, -1.0, 0.0).add(0.5, 0.0, 0.5));
            } else {
                entityLocations.add(block.getLocation().add(0.0, 1.0, 0.0).add(0.5, 0.0, 0.5));
            }
        } else if (block.getType().equals(Material.CHEST) || block.getType().equals(Material.TRAPPED_CHEST)) {
            Chest chest = (Chest) block.getState();
            if (BlockUtil.isDoubleChest(chest.getInventory())) {
                Block connectedChest = BlockUtil.getConnectedDoubleChest(block);
                if (connectedChest == null) {
                    return;
                }
                entityLocations.add(connectedChest.getLocation().add(0.5, 0.0, 0.5));
            }
        }
        for (Location location : entityLocations) {
            // Slimes are block-shaped
            // Slimes are used instead of shulkers because shulkers collision cannot be disabled
            WrapperEntity entity = new WrapperEntity(EntityTypes.SLIME);
            SlimeMeta slimeMeta = (SlimeMeta) entity.getEntityMeta();
            slimeMeta.setGlowing(true);
            slimeMeta.setInvisible(true);
            // Medium slime is the size of a block
            slimeMeta.setSize(2);
            entity.addViewer(player.getUniqueId());
            // Place a second entity 1 block up for doors
            entity.spawn(SpigotConversionUtil.fromBukkitLocation(location));
            entities.add(entity);
        }
    }

    public void removeEntities(NamedTextColor color) {
        entities.stream()
            .filter(Objects::nonNull)
            .forEach(WrapperEntity::despawn);
        removeTeam(color);
    }

    private void setTeam(NamedTextColor color) {
        TeamsPacketUtil.addToTeam(
            player,
            color,
            entities.stream()
                .filter(Objects::nonNull)
                .filter(entity -> !TeamTracker.getInstance().isTracked(entity.getUuid())) // Only include untracked
                .map(entity -> entity.getUuid().toString())
                .toList()
        );
    }

    private void removeTeam(NamedTextColor color) {
        TeamsPacketUtil.removeFromTeam(
            player,
            color,
            entities.stream()
                .filter(Objects::nonNull)
                .filter(entity -> TeamTracker.getInstance().isTracked(entity.getUuid())) // Only include tracked
                .map(entity -> entity.getUuid().toString())
                .toList()
        );
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof GlowingBlock that)) return false;
        return Objects.equals(block, that.block);
    }

    @Override
    public int hashCode() {
        return block.hashCode();
    }
}

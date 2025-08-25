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
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;
import java.util.List;

public class GlowingBlock {

    private final Block block;
    private final Player player;
    private final List<WrapperEntity> entities;
    private Team team;

    public GlowingBlock (Block block, Player player) {
        this.block = block;
        this.player = player;
        entities = new ArrayList<>();
    }

    public void glow(NamedTextColor color) {
        placeEntities();
        setGlowColor(color);
        Bukkit.getScheduler().runTaskLater(BoltUX.getInstance(), this::removeEntities, Settings.getGlowBlockTime() * 20L);
    }

    private void placeEntities() {
        List<Location> entityLocations = new ArrayList<>();
        entityLocations.add(block.getLocation().add(0.5, 0.0,0.5));

        if (MaterialTags.DOORS.isTagged(block.getType())) {
            Door door = (Door) block.getBlockData();
            if(door.getHalf().equals(Bisected.Half.TOP)) {
                entityLocations.add(block.getLocation().add(0.0, -1.0, 0.0).add(0.5, 0.0,0.5));
            } else {
                entityLocations.add(block.getLocation().add(0.0, 1.0, 0.0).add(0.5, 0.0,0.5));
            }
        } else if (block.getType().equals(Material.CHEST) || block.getType().equals(Material.TRAPPED_CHEST)) {
            Chest chest = (Chest) block.getState();
            if (BlockUtil.isDoubleChest(chest.getInventory())) {
                Block connectedChest = BlockUtil.getConnectedDoubleChest(block);
                if (connectedChest == null) {
                    return;
                }
                entityLocations.add(connectedChest.getLocation().add(0.5, 0.0,0.5));
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

    public void removeEntities() {
        for (WrapperEntity entity : entities) {
            if (entity == null)
                return;
            team.removeEntry(entity.getUuid().toString());
            entity.despawn();
            entity = null;
        }
    }

    private void setGlowColor(NamedTextColor color) {
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        Scoreboard scoreboard = manager.getMainScoreboard();
        String teamName = "boltux_color_" + color.toString();
        team = scoreboard.getTeam(teamName);
        if (team == null) {
            team = scoreboard.registerNewTeam(teamName);
            team.color(color);
            team.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.NEVER);
            team.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.NEVER);
        }

        for (WrapperEntity entity : entities) {
            if (entity == null) {
                continue;
            }
            team.addEntry(entity.getUuid().toString());
        }
    }

}

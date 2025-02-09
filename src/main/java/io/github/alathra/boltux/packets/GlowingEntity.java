package io.github.alathra.boltux.packets;

import com.github.retrooper.packetevents.PacketEvents;
import io.github.alathra.boltux.BoltUX;
import io.github.alathra.boltux.config.Settings;
import io.github.retrooper.packetevents.util.SpigotConversionUtil;
import me.tofaa.entitylib.meta.EntityMeta;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;
import org.jetbrains.annotations.Nullable;

import java.util.*;


public class GlowingEntity {

    // Map of EntityID, EntityUUID
    public static final Map<Integer, UUID> glowingEntitiesRawMap = new HashMap<>();
    public static final Set<GlowingEntity> glowingEntities = new HashSet<>();

    private final Entity entity;
    private final Player player;
    private BukkitTask stopGlowTimer;
    private Team team;

    public GlowingEntity(Entity entity, Player player) {
        this.entity = entity;
        this.player = player;
    }

    public void glow(NamedTextColor color) {
        glowingEntities.add(this);
        EntityMeta entityMeta = EntityMeta.createMeta(entity.getEntityId(), SpigotConversionUtil.fromBukkitEntityType(entity.getType()));
        setGlowColor(color);
        // Without this 1-tick delay, this will have no effect, for some reason
        Bukkit.getScheduler().runTaskLater(BoltUX.getInstance(), () -> {
            entityMeta.setGlowing(true);
            PacketEvents.getAPI().getPlayerManager().sendPacket(player, entityMeta.createPacket());
            glowingEntitiesRawMap.put(entity.getEntityId(), entity.getUniqueId());
        }, 1L);

        // Clear glow effect after glow time is reached
        stopGlowTimer = Bukkit.getScheduler().runTaskLater(BoltUX.getInstance(), () -> {
            entityMeta.setGlowing(false);
            PacketEvents.getAPI().getPlayerManager().sendPacket(player, entityMeta.createPacket());
            glowingEntitiesRawMap.remove(entity.getEntityId());
            // Remove from team so glow color is not persistent
            team.removeEntity(entity);
            glowingEntities.remove(this);
        }, Settings.getGlowBlockTime() * 20L);
    }

    public void cancelStopGlowTimer() {
        stopGlowTimer.cancel();
        glowingEntities.remove(this);
    }

    private void setGlowColor(NamedTextColor color) {
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        Scoreboard scoreboard = manager.getMainScoreboard();
        String teamName = "boltux_color_" + color.toString();
        team = scoreboard.getTeam(teamName);
        if (team == null) {
            team = scoreboard.registerNewTeam(teamName);
            team.color(color);
        }
        if (entity == null) {
            return;
        }
        team.addEntity(entity);
    }

    public int getEntityID() {
        return entity.getEntityId();
    }

    public static @Nullable GlowingEntity getGlowingEntityByEntityID(int entityID) {
        for (GlowingEntity glowingEntity : glowingEntities) {
            if (glowingEntity.getEntityID() == entityID) {
                return glowingEntity;
            }
        }
        return null;
    }
}

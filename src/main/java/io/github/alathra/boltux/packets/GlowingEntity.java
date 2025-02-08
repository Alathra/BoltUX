package io.github.alathra.boltux.packets;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityMetadata;
import io.github.alathra.boltux.BoltUX;
import io.github.alathra.boltux.config.Settings;
import io.github.retrooper.packetevents.util.SpigotConversionUtil;
import me.tofaa.entitylib.meta.EntityMeta;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class GlowingEntity {

    public static final Set<UUID> glowingEntities = new HashSet<>();

    private final Entity entity;
    private final Player player;
    private Team team;

    public GlowingEntity(Entity entity, Player player) {
        this.entity = entity;
        this.player = player;
    }

    public void glow(NamedTextColor color) {
        EntityMeta entityMeta = EntityMeta.createMeta(entity.getEntityId(), SpigotConversionUtil.fromBukkitEntityType(entity.getType()));
        setGlowColor(color);
        // Without this 1-tick delay, this will have no effect, for some reason
        Bukkit.getScheduler().runTaskLater(BoltUX.getInstance(), () -> {
            entityMeta.setGlowing(true);
            PacketEvents.getAPI().getPlayerManager().sendPacket(player, entityMeta.createPacket());
            glowingEntities.add(entity.getUniqueId());
        }, 1L);

        // Clear glow effect after glow time is reached
        Bukkit.getScheduler().runTaskLater(BoltUX.getInstance(), () -> {
            entityMeta.setGlowing(false);
            PacketEvents.getAPI().getPlayerManager().sendPacket(player, entityMeta.createPacket());
            glowingEntities.remove(entity.getUniqueId());
            // Remove from team so glow color is not persistent
            team.removeEntity(entity);
        }, Settings.getGlowBlockTime() * 20L);
    }

    private void setGlowColor(NamedTextColor color) {
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        Scoreboard scoreboard = manager.getMainScoreboard();
        String teamName = "boltux_color_" + color.examinableName();
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
}

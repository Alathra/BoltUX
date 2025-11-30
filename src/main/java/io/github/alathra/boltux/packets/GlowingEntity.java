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

import java.util.List;
import java.util.Objects;

public class GlowingEntity {
    private final Entity entity;
    private final Player player;
    private final EntityMeta entityMeta;

    public GlowingEntity(Entity entity, Player player, NamedTextColor color) {
        this.entity = entity;
        this.player = player;
        this.entityMeta = EntityMeta.createMeta(entity.getEntityId(), SpigotConversionUtil.fromBukkitEntityType(entity.getType()));

        // Only executes if this entity is not already glowing
        if (!GlowingEntityTracker.getInstance().isGlowing(player, entity)) {
            // Set this entity as glowing
            GlowingEntityTracker.getInstance().track(player, entity);

            setTeam(color);
            entityMeta.setGlowing(true);
            PacketEvents.getAPI().getPlayerManager().sendPacket(player, entityMeta.createPacket());

            // Clear glow effect after glow time is reached
            Bukkit.getScheduler().runTaskLaterAsynchronously(BoltUX.getInstance(), () -> {
                GlowingEntityTracker.getInstance().untrack(player, entity);
                entityMeta.setGlowing(false);
                PacketEvents.getAPI().getPlayerManager().sendPacket(player, entityMeta.createPacket());
                removeTeam();
            }, Settings.getGlowBlockTime() * 20L);
        }
    }


    private void setTeam(NamedTextColor color) {
        if (!entity.isValid())
            return;

        // Only allow untracked
        if (!TeamTracker.getInstance().isTracked(entity.getUniqueId())) { // Only allow tracked
            TeamsPacketUtil.addToTeam(
                player,
                color,
                List.of(entity.getUniqueId().toString())
            );
            TeamTracker.getInstance().setTracked(entity.getUniqueId(), color);
        }
    }

    private void removeTeam() {
        if (TeamTracker.getInstance().isTracked(entity.getUniqueId())) { // Only allow tracked
            TeamsPacketUtil.removeFromTeam(
                player,
                TeamTracker.getInstance().getTrackedColor(entity.getUniqueId()),
                List.of(entity.getUniqueId().toString())
            );
            TeamTracker.getInstance().unTrack(entity.getUniqueId());
        }
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof GlowingEntity that)) return false;
        return Objects.equals(entity, that.entity);
    }

    @Override
    public int hashCode() {
        return entity.hashCode();
    }
}

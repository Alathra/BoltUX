package io.github.alathra.boltux.packets;

import com.github.retrooper.packetevents.event.PacketListener;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityMetadata;
import io.github.retrooper.packetevents.util.SpigotConversionUtil;
import me.tofaa.entitylib.meta.EntityMeta;
import me.tofaa.entitylib.meta.Metadata;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Fixes interaction with glowing entities.
 * @author darksaid98
 */
public class GlowPacketListener implements PacketListener {
    /**
     * When interacting with a entity that has custom metadata, the client requests an update packet for the entity.
     * This would break our glowing mechanic as said update packet would not include our ephemeral "glowing" metadata.
     * This listener modifies the outgoing metadata packets and includes the glowing.
     * @param event packet event
     */
    @Override
    public void onPacketSend(@NotNull PacketSendEvent event) {
        // Only catch client bound entity metadata packets
        if (!event.getPacketType().equals(PacketType.Play.Server.ENTITY_METADATA)) {
            return;
        }

        final WrapperPlayServerEntityMetadata packet = new WrapperPlayServerEntityMetadata(event);
        final Player player = event.getPlayer();

        // Get the bukkit entity from the entity id
        final Entity entity = SpigotConversionUtil.getEntityById(player.getWorld(), packet.getEntityId());
        if (entity == null)
            return;

        // If this entity isn't tracked by us, it is not a glowing entity
        if (!GlowingEntityTracker.getInstance().isGlowing(player, entity)) {
            return;
        }

        // Modify existing metadata to enable glowing
        final Metadata metadata = new Metadata(packet.getEntityId());
        metadata.setMetaFromPacket(packet);

        final EntityMeta entityMeta = new EntityMeta(packet.getEntityId(), metadata);
        entityMeta.setGlowing(true);

        // Replace metadata of entity with modified metadata
        packet.setEntityMetadata(entityMeta);
    }
}

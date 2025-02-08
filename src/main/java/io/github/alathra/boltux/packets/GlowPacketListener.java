package io.github.alathra.boltux.packets;

import com.github.retrooper.packetevents.event.PacketListener;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.player.User;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientInteractEntity;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;

public class GlowPacketListener implements PacketListener {

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        User user = event.getUser();
        if (!event.getPacketType().equals(PacketType.Play.Client.INTERACT_ENTITY)) {
            return;
        }
        // They interacted with an entity.
        WrapperPlayClientInteractEntity packet = new WrapperPlayClientInteractEntity(event);
        // Retrieve that entity's ID
        int entityId = packet.getEntityId();
        if (!GlowingEntity.glowingEntitiesRawMap.containsKey(entityId)) {
            return;
        }
        // Cancel the current glow, stop it from removing glow
        GlowingEntity.glowingEntitiesRawMap.remove(entityId);
        GlowingEntity oldGlowingEntity = GlowingEntity.getGlowingEntityByEntityID(entityId);
        if (oldGlowingEntity != null) {
            oldGlowingEntity.cancelStopGlowTimer();
            GlowingEntity.glowingEntities.remove(oldGlowingEntity);
        }
        // Create a new glow effect
        try {
            Entity entity = Bukkit.getEntity(GlowingEntity.glowingEntitiesRawMap.get(entityId));
            GlowingEntity glowingEntity = new GlowingEntity(entity, Bukkit.getPlayer(user.getUUID()));
            glowingEntity.glow(NamedTextColor.RED);
        } catch (IllegalArgumentException ignored) {}
    }
}

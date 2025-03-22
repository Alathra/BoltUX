package io.github.alathra.boltux.packets;

import com.github.retrooper.packetevents.event.PacketListener;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.player.User;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientInteractEntity;
import io.github.alathra.boltux.BoltUX;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.popcraft.bolt.protection.EntityProtection;

public class GlowPacketListener implements PacketListener {

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (!event.getPacketType().equals(PacketType.Play.Client.INTERACT_ENTITY)) {
            return;
        }
        User user = event.getUser();
        // They interacted with an entity.
        WrapperPlayClientInteractEntity packet = new WrapperPlayClientInteractEntity(event);
        // Retrieve that entity's ID
        final int entityId = packet.getEntityId();
        if (!GlowingEntity.glowingEntitiesRawMap.containsKey(entityId)) {
            return;
        }
        // Cancel the current glow, stop it from removing glow
        GlowingEntity oldGlowingEntity = GlowingEntity.getGlowingEntityByEntityID(entityId);
        if (oldGlowingEntity == null) {
            return;
        }
        Entity entity = oldGlowingEntity.getEntity();
        oldGlowingEntity.stopGlowNow();
        // Create a new glow effect
        EntityProtection protection = BoltUX.getBoltPlugin().loadProtection(entity);
        if (protection == null) {
            return;
        }
        GlowingEntity glowingEntity = new GlowingEntity(entity, Bukkit.getPlayer(user.getUUID()));
        if (protection.getOwner().equals(user.getUUID())) {
            glowingEntity.glow(NamedTextColor.GREEN);
        } else {
            glowingEntity.glow(NamedTextColor.RED);
        }
    }
}

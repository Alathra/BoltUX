package io.github.alathra.boltux.packets;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Tracks all glowing entities for a player. Used to prevent duplicate glowing entities.
 *
 * @author darksaid98
 */
public final class GlowingEntityTracker {
    private static GlowingEntityTracker INSTANCE = null;

    public static GlowingEntityTracker getInstance() {
        if (INSTANCE == null)
            INSTANCE = new GlowingEntityTracker();
        return INSTANCE;
    }

    private GlowingEntityTracker() {
    }

    private final Map<Player, Set<UUID>> glowingEntities = new ConcurrentHashMap<>();

    public void track(Player player, Entity entity) {
        glowingEntities.compute(player, (player1, uuids) -> {
            if (uuids != null) {
                uuids.add(entity.getUniqueId());
                return uuids;
            }

            final Set<UUID> set = ConcurrentHashMap.newKeySet();
            set.add(entity.getUniqueId());
            return set;
        });
    }

    public void untrack(Player player) {
        glowingEntities.remove(player);
    }

    public void untrack(Player player, Entity entity) {
        glowingEntities.computeIfPresent(player, (player1, uuids) -> {
            uuids.remove(entity.getUniqueId());
            return uuids;
        });
        if (glowingEntities.containsKey(player) && glowingEntities.get(player).isEmpty())
            glowingEntities.remove(player);
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean isGlowing(Player player, Entity entity) {
        return Optional.ofNullable(glowingEntities.get(player))
            .map(uuids -> uuids.contains(entity.getUniqueId()))
            .orElse(false);
    }

    public void clear() {
        glowingEntities.clear();
    }
}

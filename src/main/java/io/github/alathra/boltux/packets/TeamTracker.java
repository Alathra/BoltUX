package io.github.alathra.boltux.packets;

import net.kyori.adventure.text.format.NamedTextColor;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Used to track whether an entity is in a scoreboard team or not.
 * @author darksaid98
 */
public class TeamTracker {
    private static TeamTracker INSTANCE = null;

    public static TeamTracker getInstance() {
        if (INSTANCE == null)
            INSTANCE = new TeamTracker();
        return INSTANCE;
    }

    private TeamTracker() {}

    private final Map<UUID, NamedTextColor> entityMap = new ConcurrentHashMap<>();

    public void setTracked(UUID uuid, NamedTextColor color) {
        entityMap.put(uuid, color);
    }

    public void unTrack(UUID uuid) {
        entityMap.remove(uuid);
    }

    public NamedTextColor getTrackedColor(UUID uuid) {
        return entityMap.get(uuid);
    }

    public boolean isTracked(UUID uuid) {
        return entityMap.containsKey(uuid);
    }

    public void clear() {
        entityMap.clear();
    }
}

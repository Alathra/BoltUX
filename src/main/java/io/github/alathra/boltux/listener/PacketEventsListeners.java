package io.github.alathra.boltux.listener;

import io.github.alathra.boltux.packets.GlowingEntityTracker;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PacketEventsListeners implements Listener {
    @SuppressWarnings("unused")
    public void onPlayerQuit(PlayerQuitEvent e) {
        GlowingEntityTracker.getInstance().untrack(e.getPlayer());
    }
}

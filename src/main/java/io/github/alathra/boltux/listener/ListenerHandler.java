package io.github.alathra.boltux.listener;

import io.github.alathra.boltux.BoltUX;
import io.github.alathra.boltux.hook.Hook;
import io.github.alathra.boltux.utility.Reloadable;

public class ListenerHandler implements Reloadable {
    private final BoltUX plugin;

    public ListenerHandler(BoltUX plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onLoad(BoltUX plugin) {
    }

    @Override
    public void onEnable(BoltUX plugin) {
        plugin.getServer().getPluginManager().registerEvents(new LockUseListeners(), plugin);
        plugin.getServer().getPluginManager().registerEvents(new LockReturnListeners(), plugin);
        plugin.getServer().getPluginManager().registerEvents(new ProtectionDamageListeners(), plugin);
        plugin.getServer().getPluginManager().registerEvents(new ProtectionInteractListeners(), plugin);
        if (Hook.PacketEvents.isLoaded())
            plugin.getServer().getPluginManager().registerEvents(new PacketEventsListeners(), plugin);
    }

    @Override
    public void onDisable(BoltUX plugin) {
    }
}

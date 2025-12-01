package io.github.alathra.boltux.lock.listener;

import io.github.alathra.boltux.BoltUX;
import io.github.alathra.boltux.Reloadable;

public final class ListenerHandler implements Reloadable {
    private final BoltUX plugin;

    public ListenerHandler(BoltUX plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onEnable(BoltUX plugin) {
        plugin.getServer().getPluginManager().registerEvents(new LockUseListener(), plugin);
        plugin.getServer().getPluginManager().registerEvents(new LockDropListener(), plugin);
        plugin.getServer().getPluginManager().registerEvents(new LockCraftingListener(), plugin);
    }

}

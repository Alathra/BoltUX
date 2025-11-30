package io.github.alathra.boltux.lock;

import io.github.alathra.boltux.BoltUX;
import io.github.alathra.boltux.Reloadable;
import io.github.alathra.boltux.lock.listener.ListenerHandler;

public final class LockHandler implements Reloadable {
    private ListenerHandler listenerHandler;
    private CraftingHandler craftingHandler;

    public LockHandler(BoltUX plugin) {
    }

    @Override
    public void onLoad(BoltUX plugin) {
        listenerHandler = new ListenerHandler(plugin);
        craftingHandler = new CraftingHandler(plugin);
        listenerHandler.onLoad(plugin);
        craftingHandler.onLoad(plugin);
    }

    @Override
    public void onEnable(BoltUX plugin) {
        listenerHandler.onEnable(plugin);
        craftingHandler.onEnable(plugin);
    }

    @Override
    public void onDisable(BoltUX plugin) {
        listenerHandler.onDisable(plugin);
        craftingHandler.onDisable(plugin);
        listenerHandler = null;
        craftingHandler = null;
    }
}

package io.github.alathra.boltux.listener;

import io.github.alathra.boltux.BoltUX;
import io.github.alathra.boltux.utility.Reloadable;

public class ListenerHandler implements Reloadable {
    private final BoltUX plugin;

    public ListenerHandler(BoltUX plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onLoad() {
    }

    @Override
    public void onEnable() {
        // Template listeners
        plugin.getServer().getPluginManager().registerEvents(new UpdateCheckListener(), plugin);
        if (BoltUX.getVaultHook().isHookLoaded())
            plugin.getServer().getPluginManager().registerEvents(new VaultListener(), plugin);
        // BoltUX specific
        plugin.getServer().getPluginManager().registerEvents(new LockUseListeners(), plugin);
        plugin.getServer().getPluginManager().registerEvents(new LockReturnListeners(), plugin);
        plugin.getServer().getPluginManager().registerEvents(new ProtectionDamageListeners(), plugin);
        plugin.getServer().getPluginManager().registerEvents(new ProtectionInteractListeners(), plugin);
    }

    @Override
    public void onDisable() {
    }
}

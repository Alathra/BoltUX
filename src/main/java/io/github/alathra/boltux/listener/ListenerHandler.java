package io.github.alathra.boltux.listener;

import io.github.alathra.boltux.BoltUX;
import io.github.alathra.boltux.utility.Reloadable;

/**
 * A class to handle registration of event listeners.
 */
public class ListenerHandler implements Reloadable {
    private final BoltUX plugin;

    /**
     * Instantiates the Listener handler.
     *
     * @param plugin the plugin instance
     */
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
        plugin.getServer().getPluginManager().registerEvents(new LockUseListener(), plugin);
        plugin.getServer().getPluginManager().registerEvents(new ProtectedBlockBreakListener(), plugin);
        plugin.getServer().getPluginManager().registerEvents(new ProtectedEntityBreakListener(), plugin);
        plugin.getServer().getPluginManager().registerEvents(new ProtectedBlockDamageListener(), plugin);
        plugin.getServer().getPluginManager().registerEvents(new ProtectedBlockInteractListener(), plugin);
        plugin.getServer().getPluginManager().registerEvents(new ProtectedEntityDamageListener(), plugin);
        plugin.getServer().getPluginManager().registerEvents(new ProtectedEntityInteractListener(), plugin);
    }

    @Override
    public void onDisable() {
    }
}

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
        plugin.getServer().getPluginManager().registerEvents(new UpdateCheckListener(), plugin);
        if (BoltUX.getVaultHook().isHookLoaded())
            plugin.getServer().getPluginManager().registerEvents(new VaultListener(), plugin);
    }

    @Override
    public void onDisable() {
    }
}

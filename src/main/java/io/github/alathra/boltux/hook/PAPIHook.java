package io.github.alathra.boltux.hook;

import io.github.alathra.boltux.BoltUX;
import org.bukkit.Bukkit;

/**
 * A hook to interface with <a href="https://wiki.placeholderapi.com/">PlaceholderAPI</a>.
 */
public class PAPIHook implements Hook {
    private final BoltUX plugin;
    private final static String pluginName = "PlaceholderAPI";
    private PAPIExpansion PAPIExpansion;

    /**
     * Instantiates a new PlaceholderAPI hook.
     *
     * @param plugin the plugin instance
     */
    public PAPIHook(BoltUX plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onLoad() {
    }

    @Override
    public void onEnable() {
        if (!isHookLoaded())
            return;

        PAPIExpansion = new PAPIExpansion(plugin);
        PAPIExpansion.register();
    }

    @Override
    public void onDisable() {
        if (!isHookLoaded())
            return;

        PAPIExpansion.unregister();
        PAPIExpansion = null;
    }

    @Override
    public boolean isHookLoaded() {
        return Bukkit.getPluginManager().isPluginEnabled(pluginName);
    }
}

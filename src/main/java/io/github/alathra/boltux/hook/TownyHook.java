package io.github.alathra.boltux.hook;

import io.github.alathra.boltux.BoltUX;

public class TownyHook implements Hook {
    private final BoltUX plugin;

    public TownyHook(BoltUX plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean isHookLoaded() {
        return plugin.getServer().getPluginManager().isPluginEnabled("Towny");
    }

    @Override
    public void onLoad() {

    }

    @Override
    public void onEnable() {

    }

    @Override
    public void onDisable() {

    }
}

package io.github.alathra.boltux.config;

import io.github.alathra.boltux.BoltUX;
import io.github.alathra.boltux.Reloadable;
import io.github.milkdrinkers.crate.Config;
import io.github.milkdrinkers.crate.internal.settings.ReloadSetting;

/**
 * A class that generates/loads {@literal &} provides access to a configuration file.
 */
public class ConfigHandler implements Reloadable {
    private final BoltUX plugin;
    private Config cfg;

    /**
     * Instantiates a new Config handler.
     *
     * @param plugin the plugin instance
     */
    public ConfigHandler(BoltUX plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onLoad(BoltUX plugin) {
        cfg = Config.builderConfig()
            .path(plugin.getDataFolder().toPath().resolve("config.yml"))
            .defaults(plugin.getResource("config.yml"))
            .reload(ReloadSetting.MANUALLY)
            .build();
    }

    /**
     * Gets main config object.
     *
     * @return the config object
     */
    public Config getConfig() {
        return cfg;
    }
}

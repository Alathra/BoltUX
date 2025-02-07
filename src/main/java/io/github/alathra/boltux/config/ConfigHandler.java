package io.github.alathra.boltux.config;

import com.github.milkdrinkers.crate.Config;
import io.github.alathra.boltux.BoltUX;
import io.github.alathra.boltux.utility.Reloadable;

import javax.inject.Singleton;

/**
 * A class that generates/loads {@literal &} provides access to a configuration file.
 */
@Singleton
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
    public void onLoad() {
        cfg = new Config("config", plugin.getDataFolder().getPath(), plugin.getResource("config.yml")); // Create a config file from the template in our resources folder
        Settings.init(plugin);
    }

    @Override
    public void onEnable() {
    }

    @Override
    public void onDisable() {
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

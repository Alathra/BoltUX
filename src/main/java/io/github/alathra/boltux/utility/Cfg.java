package io.github.alathra.boltux.utility;

import io.github.milkdrinkers.crate.Config;
import io.github.alathra.boltux.BoltUX;
import io.github.alathra.boltux.config.ConfigHandler;
import org.jetbrains.annotations.NotNull;

/**
 * Convenience class for accessing {@link ConfigHandler#getConfig}
 */
public abstract class Cfg {
    /**
     * Convenience method for {@link ConfigHandler#getConfig} to getConnection {@link Config}
     *
     * @return the config
     */
    @NotNull
    public static Config get() {
        return BoltUX.getInstance().getConfigHandler().getConfig();
    }
}

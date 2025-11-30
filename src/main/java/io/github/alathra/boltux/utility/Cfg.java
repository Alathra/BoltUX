package io.github.alathra.boltux.utility;

import io.github.alathra.boltux.BoltUX;
import io.github.alathra.boltux.config.ConfigHandler;
import io.github.milkdrinkers.crate.Config;
import org.jetbrains.annotations.NotNull;

/**
 * Convenience class for accessing {@link ConfigHandler#getConfig}
 */
public final class Cfg {
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

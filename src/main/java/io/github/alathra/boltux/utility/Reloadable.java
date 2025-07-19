package io.github.alathra.boltux.utility;

import io.github.alathra.boltux.BoltUX;

/**
 * Implemented in classes that should support being reloaded IE executing the methods during runtime after startup.
 */
public interface Reloadable {
    /**
     * On plugin load.
     */
    void onLoad(BoltUX plugin);

    /**
     * On plugin enable.
     */
    void onEnable(BoltUX plugin);

    /**
     * On plugin disable.
     */
    void onDisable(BoltUX plugin);
}
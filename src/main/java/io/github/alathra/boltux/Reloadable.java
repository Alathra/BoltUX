package io.github.alathra.boltux;

/**
 * Implemented in classes that should support being reloaded IE executing the methods during runtime after startup.
 */
public interface Reloadable {
    /**
     * On plugin load.
     */
    default void onLoad(BoltUX plugin) {
    }

    /**
     * On plugin enable.
     */
    default void onEnable(BoltUX plugin) {
    }

    /**
     * On plugin disable.
     */
    default void onDisable(BoltUX plugin) {
    }

}

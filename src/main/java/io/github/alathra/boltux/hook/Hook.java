package io.github.alathra.boltux.hook;

import io.github.alathra.boltux.utility.Reloadable;

/**
 * Implemented in hooks to other APIs
 */
public interface Hook extends Reloadable {
    /**
     * Check if this hook is loaded and ready for use.
     * @return boolean whether this hook is loaded or not
     * @implNote This check is a guarantee that the hook and its dependencies have loaded properly and are ready for usage.
     */
    boolean isHookLoaded();
}

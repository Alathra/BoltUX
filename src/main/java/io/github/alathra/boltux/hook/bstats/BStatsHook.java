package io.github.alathra.boltux.hook.bstats;

import io.github.alathra.boltux.BoltUX;
import io.github.alathra.boltux.hook.AbstractHook;
import org.bstats.bukkit.Metrics;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

/**
 * A hook to interface with <a href="https://github.com/Bastian/bstats-metrics">BStats</a>.
 */
public final class BStatsHook extends AbstractHook {
    private final static int BSTATS_ID = 28171;
    private @Nullable Metrics hook;

    /**
     * Instantiates a new BStats hook.
     *
     * @param plugin the plugin instance
     */
    public BStatsHook(BoltUX plugin) {
        super(plugin);
    }

    @Override
    public void onEnable(BoltUX plugin) {
        // Catch startup errors for bstats
        try {
            setHook(new Metrics(getPlugin(), BSTATS_ID));
        } catch (Exception ignored) {
            setHook(null);
        }
    }

    @Override
    public void onDisable(BoltUX plugin) {
        getHook().shutdown();
        setHook(null);
    }

    @Override
    public boolean isHookLoaded() {
        return hook != null;
    }

    /**
     * Gets BStats metrics instance. Should only be used following {@link #isHookLoaded()}.
     *
     * @return instance
     */
    public Metrics getHook() {
        if (!isHookLoaded())
            throw new IllegalStateException("Attempted to access BStats metrics instance hook when it is unavailable!");

        return hook;
    }

    /**
     * Sets the BStats metrics instance.
     *
     * @param hook The BStats metrics instance {@link Metrics}
     */
    @ApiStatus.Internal
    private void setHook(@Nullable Metrics hook) {
        this.hook = hook;
    }
}

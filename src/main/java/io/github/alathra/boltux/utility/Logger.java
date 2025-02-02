package io.github.alathra.boltux.utility;


import io.github.alathra.boltux.BoltUX;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.jetbrains.annotations.NotNull;

/**
 * A class that provides shorthand access to {@link BoltUX#getComponentLogger}.
 */
public class Logger {
    /**
     * Get component logger. Shorthand for:
     *
     * @return the component logger {@link BoltUX#getComponentLogger}.
     */
    @NotNull
    public static ComponentLogger get() {
        return BoltUX.getInstance().getComponentLogger();
    }
}

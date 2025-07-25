package io.github.alathra.boltux.command;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPIBukkitConfig;
import io.github.alathra.boltux.BoltUX;
import io.github.alathra.boltux.utility.Reloadable;

/**
 * A class to handle registration of commands.
 */
public class CommandHandler implements Reloadable {
    private final BoltUX plugin;

    /**
     * Instantiates the Command handler.
     *
     * @param plugin the plugin
     */
    public CommandHandler(BoltUX plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onLoad(BoltUX plugin) {
        CommandAPI.onLoad(
            new CommandAPIBukkitConfig(plugin)
                .shouldHookPaperReload(true)
                .silentLogs(true)
                .beLenientForMinorVersions(true)
        );
    }

    @Override
    public void onEnable(BoltUX plugin) {
        CommandAPI.onEnable();

        // Register commands here
        new BoltUXCommand();
    }

    @Override
    public void onDisable(BoltUX plugin) {
        CommandAPI.getRegisteredCommands().forEach(registeredCommand -> CommandAPI.unregister(registeredCommand.namespace() + ':' + registeredCommand.commandName(), true));
        CommandAPI.onDisable();
    }
}
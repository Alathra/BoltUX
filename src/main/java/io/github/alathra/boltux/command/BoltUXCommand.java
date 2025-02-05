package io.github.alathra.boltux.command;

import com.github.milkdrinkers.colorparser.ColorParser;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.executors.CommandArguments;
import io.github.alathra.boltux.api.BoltUXAPI;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

class BoltUXCommand {
    private static final String ADMIN_PERM = "boltux.admin";

    protected BoltUXCommand() {
        new CommandAPICommand("boltux")
            .withFullDescription("BoltUX commands.")
            .withShortDescription("BoltUX commands.")
            .withPermission(ADMIN_PERM)
            .withSubcommands(
                GetLockCommand(),
                new TranslationCommand().command()
            )
            .executes(this::helpMenu)
            .register();
    }

    private void helpMenu(CommandSender sender, CommandArguments args) {
        sender.sendMessage(ColorParser.of("<yellow>BoltUX Commands:").build());
        sender.sendMessage(ColorParser.of("<yellow>/boltux getlock <green>Spawn a lock item").build());
    }

    private CommandAPICommand GetLockCommand() {
        return new CommandAPICommand("getlock")
            .withFullDescription("Gives yourself the lock item.")
            .withShortDescription("Gives lock item.")
            .withPermission(ADMIN_PERM)
            .executesPlayer((Player sender, CommandArguments args) -> sender.getInventory().addItem(BoltUXAPI.getLockItem()));
    }
}

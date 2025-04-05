package io.github.alathra.boltux.command;

import com.github.milkdrinkers.colorparser.ColorParser;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.IntegerArgument;
import dev.jorel.commandapi.executors.CommandArguments;
import io.github.alathra.boltux.api.BoltUXAPI;
import io.github.alathra.boltux.data.Permissions;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

class BoltUXCommand {

    protected BoltUXCommand() {
        new CommandAPICommand("boltux")
            .withFullDescription("BoltUX commands.")
            .withShortDescription("BoltUX commands.")
            .withPermission(Permissions.ADMIN_PERMISSION)
            .withSubcommands(
                getLockCommand()
            )
            .executes(this::helpMenu)
            .register();
    }

    private void helpMenu(CommandSender sender, CommandArguments args) {
        sender.sendMessage(ColorParser.of("<yellow>BoltUX Commands:").build());
        sender.sendMessage(ColorParser.of("<yellow>/boltux getlock <green>Spawn a lock item").build());
    }

    private CommandAPICommand getLockCommand() {
        return new CommandAPICommand("getlock")
            .withFullDescription("Gives yourself the lock item.")
            .withShortDescription("Gives lock item.")
            .withPermission(Permissions.ADMIN_PERMISSION)
            .withOptionalArguments(
                new IntegerArgument("amount")
                    .replaceSuggestions(ArgumentSuggestions.strings("64", "32", "16"))
            )
            .executesPlayer((Player sender, CommandArguments args) -> {
                Integer amount = (Integer) args.get("amount");
                if (amount == null) {
                    sender.getInventory().addItem(BoltUXAPI.getLockItem(1));
                    return;
                }
                sender.getInventory().addItem(BoltUXAPI.getLockItem(amount));
            });
    }
}

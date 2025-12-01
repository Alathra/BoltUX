package io.github.alathra.boltux.gui.transfer;

import dev.triumphteam.gui.builder.item.PaperItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import dev.triumphteam.gui.guis.PaginatedGui;
import io.github.alathra.boltux.BoltUX;
import io.github.alathra.boltux.gui.AbstractGui;
import io.github.alathra.boltux.gui.GuiHandler;
import io.github.alathra.boltux.gui.GuiHelper;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.popcraft.bolt.protection.Protection;

import java.util.Objects;

public final class TransferMenu extends AbstractGui {
    public static PaginatedGui generateBase(Player player, Protection protection, Location protectionLocation) {
        PaginatedGui base = Gui.paginated()
            .title(translate("gui.transfer.title"))
            .rows(6)
            .disableItemPlace()
            .disableItemSwap()
            .disableItemDrop()
            .disableItemTake()
            .create();

        // Apply gray glass pane border
        base.getFiller().fillBorder(PaperItemBuilder.from(borderItem()).asGuiItem());

        // Create page nav buttons
        base.setItem(6, 6, PaperItemBuilder.from(nextButton()).asGuiItem(event -> {
            base.next();
        }));

        base.setItem(6, 4, PaperItemBuilder.from(previousButton()).asGuiItem(event -> {
            base.previous();
        }));

        // Back button
        base.setItem(6, 1, PaperItemBuilder.from(backButton()).asGuiItem(event -> {
            GuiHandler.generateMainMenu(player, protection, protectionLocation);
        }));

        return base;
    }

    public static void populateContent(PaginatedGui gui, Player player, Protection protection) {
        GuiHelper.getSuggestedPlayers(player, protection.getOwner()).forEach(suggestedPlayer -> gui.addItem(playerToTransferableAccessIcon(gui, player, Bukkit.getOfflinePlayer(suggestedPlayer), protection)));
    }

    private static GuiItem playerToTransferableAccessIcon(PaginatedGui gui, Player viewer, OfflinePlayer player, Protection protection) {
        ItemStack skullItem = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta skullMeta = (SkullMeta) skullItem.getItemMeta();
        skullMeta.setOwningPlayer(player);
        skullMeta.displayName(translate("gui.transfer.buttons.player.name", viewer, (c) -> {
            c.with("name", Objects.requireNonNull(player.getName()));
        }));
        skullMeta.lore(
            translateList("gui.transfer.buttons.player.lore", viewer, (c) -> {
                c.with("name", Objects.requireNonNull(player.getName()));
            })
        );

        skullItem.setItemMeta(skullMeta);
        return PaperItemBuilder.from(skullItem).asGuiItem(event -> {
            protection.setOwner(player.getUniqueId());
            BoltUX.getBoltPlugin().saveProtection(protection);
            gui.close(viewer);
        });
    }
}

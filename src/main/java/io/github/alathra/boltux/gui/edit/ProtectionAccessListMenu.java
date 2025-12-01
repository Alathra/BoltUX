package io.github.alathra.boltux.gui.edit;

import com.palmergames.bukkit.towny.object.Town;
import dev.triumphteam.gui.builder.item.PaperItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import dev.triumphteam.gui.guis.PaginatedGui;
import io.github.alathra.boltux.BoltUX;
import io.github.alathra.boltux.gui.AbstractGui;
import io.github.alathra.boltux.gui.GuiHandler;
import io.github.alathra.boltux.hook.Hook;
import io.github.alathra.boltux.utility.BoltUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.popcraft.bolt.protection.Protection;
import org.popcraft.bolt.util.Group;

import java.util.Objects;

import static io.github.alathra.boltux.gui.GuiHelper.getGroupMemberNames;

public final class ProtectionAccessListMenu extends AbstractGui {
    public static PaginatedGui generateBase(Player player, Protection protection, Location protectionLocation) {
        PaginatedGui base = Gui.paginated()
            .title(translate("gui.access-list.title"))
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
            GuiHandler.generateProtectionAccessMenu(player, protection, protectionLocation);
        }));

        return base;
    }

    public static void populateContent(PaginatedGui gui, Protection protection) {
        // Used so this method can reload the gui content
        gui.clearPageItems();

        if (Hook.Towny.isLoaded() && Bukkit.getPluginManager().isPluginEnabled("BoltTowny")) {
            BoltUtil.getTownAccessSet(protection).forEach(accessTown -> gui.addItem(townToRemovableAccessIcon(gui, protection, accessTown)));
        }
        BoltUtil.getGroupAccessSet(protection).forEach(accessGroup -> gui.addItem(groupToRemovableAccessIcon(gui, protection, accessGroup)));
        BoltUtil.getPlayerAccessSet(protection).forEach(accessPlayer -> gui.addItem(playerToRemovableAccessIcon(gui, protection, Bukkit.getOfflinePlayer(accessPlayer))));
    }

    private static GuiItem townToRemovableAccessIcon(PaginatedGui gui, Protection protection, Town town) {
        final ItemStack townItem = new ItemStack(Material.ENDER_CHEST);
        final ItemMeta townMeta = townItem.getItemMeta();
        townMeta.displayName(translate("gui.access-list.buttons.town.name", (c) -> {
            c.with("name", town.getName());
        }));
        townMeta.lore(translateList("gui.access-list.buttons.town.lore", (c) -> {
            c.with("name", town.getName());
        }));
        townItem.setItemMeta(townMeta);
        return PaperItemBuilder.from(townItem).asGuiItem(event -> {
            protection.getAccess().remove("town:" + town.getName());
            BoltUX.getBoltPlugin().saveProtection(protection);
            populateContent(gui, protection);
        });
    }

    private static GuiItem groupToRemovableAccessIcon(PaginatedGui gui, Protection protection, Group group) {
        final ItemStack groupItem = new ItemStack(Material.CHEST);
        final ItemMeta groupMeta = groupItem.getItemMeta();
        groupMeta.displayName(translate("gui.access-list.buttons.group.name", (c) -> {
            c.with("name", group.getName());
            c.with("owner", Objects.requireNonNull(Bukkit.getOfflinePlayer(group.getOwner()).getName()));
            c.with("members", getGroupMemberNames(group));
        }));
        groupMeta.lore(translateList("gui.access-list.buttons.group.lore", (c) -> {
            c.with("name", group.getName());
            c.with("owner", Objects.requireNonNull(Bukkit.getOfflinePlayer(group.getOwner()).getName()));
            c.with("members", getGroupMemberNames(group));
        }));
        groupItem.setItemMeta(groupMeta);
        return PaperItemBuilder.from(groupItem).asGuiItem(event -> {
            protection.getAccess().remove("group:" + group.getName());
            BoltUX.getBoltPlugin().saveProtection(protection);
            populateContent(gui, protection);
        });
    }

    private static GuiItem playerToRemovableAccessIcon(PaginatedGui gui, Protection protection, OfflinePlayer player) {
        final ItemStack skullItem = new ItemStack(Material.PLAYER_HEAD);
        final SkullMeta skullMeta = (SkullMeta) skullItem.getItemMeta();
        skullMeta.setOwningPlayer(player);
        skullMeta.displayName(translate("gui.access-list.buttons.player.name", (c) -> {
            c.with("name", Objects.requireNonNull(player.getName()));
        }));
        skullMeta.lore(translateList("gui.access-list.buttons.player.lore", (c) -> {
            c.with("name", Objects.requireNonNull(player.getName()));
        }));
        skullItem.setItemMeta(skullMeta);
        return PaperItemBuilder.from(skullItem).asGuiItem(event -> {
            protection.getAccess().remove("player:" + player.getUniqueId());
            BoltUX.getBoltPlugin().saveProtection(protection);
            populateContent(gui, protection);
        });
    }
}

package io.github.alathra.boltux.gui.edit;

import com.palmergames.bukkit.towny.object.Town;
import dev.triumphteam.gui.builder.item.PaperItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import dev.triumphteam.gui.guis.PaginatedGui;
import io.github.alathra.boltux.BoltUX;
import io.github.alathra.boltux.gui.AbstractGui;
import io.github.alathra.boltux.gui.GuiHandler;
import io.github.alathra.boltux.gui.GuiHelper;
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

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import static io.github.alathra.boltux.gui.GuiHelper.getGroupMemberNames;

public final class ProtectionAccessAddMenu extends AbstractGui {
    public static PaginatedGui generateBase(Player player, Protection protection, Location protectionLocation) {
        PaginatedGui base = Gui.paginated()
            .title(translate("gui.access-add.title"))
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

    public static void populateContent(PaginatedGui gui, Player player, Protection protection) {
        // Used so this method can reload the gui content
        gui.clearPageItems();

        // Get all suggested towns that haven't been granted access
        if (Hook.Towny.isLoaded() && Bukkit.getPluginManager().isPluginEnabled("BoltTowny")) {
            Set<Town> townSuggestions = new HashSet<>(GuiHelper.getSuggestedTowns(player));
            townSuggestions.removeAll(BoltUtil.getTownAccessSet(protection));
            townSuggestions.forEach(suggestedTown -> gui.addItem(townToAddableAccessIcon(gui, protection, suggestedTown)));
        }

        // Get all groups the player owns that haven't been granted access
        BoltUtil.getGroupsWithoutAccess(protection).forEach(group -> gui.addItem(groupToAddableAccessIcon(gui, protection, group)));

        // Get suggested players that haven't been granted access
        Set<UUID> playerSuggestions = new HashSet<>(GuiHelper.getSuggestedPlayers(player, protection.getOwner()));
        playerSuggestions.removeAll(BoltUtil.getPlayerAccessSet(protection));
        playerSuggestions.forEach(suggestedPlayer -> gui.addItem(playerToAddableAccessIcon(gui, protection, Bukkit.getOfflinePlayer(suggestedPlayer))));
    }

    private static GuiItem townToAddableAccessIcon(PaginatedGui gui, Protection protection, Town town) {
        final ItemStack townItem = new ItemStack(Material.ENDER_CHEST);
        final ItemMeta townMeta = townItem.getItemMeta();
        townMeta.displayName(translate("gui.access-add.buttons.town.name", (c) -> {
            c.with("name", town.getName());
        }));
        townMeta.lore(translateList("gui.access-add.buttons.town.lore", (c) -> {
            c.with("name", town.getName());
        }));
        townItem.setItemMeta(townMeta);
        return PaperItemBuilder.from(townItem).asGuiItem(event -> {
            protection.getAccess().put("town:" + town.getName(), "normal");
            BoltUX.getBoltPlugin().saveProtection(protection);
            populateContent(gui, (Player) event.getWhoClicked(), protection);
        });
    }

    private static GuiItem groupToAddableAccessIcon(PaginatedGui gui, Protection protection, Group group) {
        final ItemStack groupItem = new ItemStack(Material.CHEST);
        final ItemMeta groupMeta = groupItem.getItemMeta();
        groupMeta.displayName(translate("gui.access-add.buttons.group.name", (c) -> {
            c.with("name", group.getName());
            c.with("owner", Objects.requireNonNull(Bukkit.getOfflinePlayer(group.getOwner()).getName()));
            c.with("members", getGroupMemberNames(group));
        }));
        groupMeta.lore(translateList("gui.access-add.buttons.group.lore", (c) -> {
            c.with("name", group.getName());
            c.with("owner", Objects.requireNonNull(Bukkit.getOfflinePlayer(group.getOwner()).getName()));
            c.with("members", getGroupMemberNames(group));
        }));
        groupItem.setItemMeta(groupMeta);
        return PaperItemBuilder.from(groupItem).asGuiItem(event -> {
            protection.getAccess().put("group:" + group.getName(), "normal");
            BoltUX.getBoltPlugin().saveProtection(protection);
            populateContent(gui, (Player) event.getWhoClicked(), protection);
        });
    }

    private static GuiItem playerToAddableAccessIcon(PaginatedGui gui, Protection protection, OfflinePlayer player) {
        final ItemStack skullItem = new ItemStack(Material.PLAYER_HEAD);
        final SkullMeta skullMeta = (SkullMeta) skullItem.getItemMeta();
        skullMeta.setOwningPlayer(player);
        skullMeta.displayName(translate("gui.access-add.buttons.player.name", (c) -> {
            c.with("name", Objects.requireNonNull(player.getName()));
        }));
        skullMeta.lore(translateList("gui.access-add.buttons.player.lore", (c) -> {
            c.with("name", Objects.requireNonNull(player.getName()));
        }));
        skullItem.setItemMeta(skullMeta);
        return PaperItemBuilder.from(skullItem).asGuiItem(event -> {
            protection.getAccess().put("player:" + player.getUniqueId(), "normal");
            BoltUX.getBoltPlugin().saveProtection(protection);
            populateContent(gui, (Player) event.getWhoClicked(), protection);
        });
    }
}

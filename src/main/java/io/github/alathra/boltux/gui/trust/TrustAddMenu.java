package io.github.alathra.boltux.gui.trust;

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
import org.popcraft.bolt.BoltPlugin;
import org.popcraft.bolt.access.AccessList;
import org.popcraft.bolt.data.Store;
import org.popcraft.bolt.protection.Protection;
import org.popcraft.bolt.util.Group;

import java.util.*;

import static io.github.alathra.boltux.gui.GuiHelper.getGroupMemberNames;

public final class TrustAddMenu extends AbstractGui {
    private static BoltPlugin boltPlugin;

    public static PaginatedGui generateBase(Player player, Protection protection, Location protectionLocation) {
        PaginatedGui base = Gui.paginated()
            .title(translate("gui.trust-add.title"))
            .rows(6)
            .disableItemPlace()
            .disableItemSwap()
            .disableItemDrop()
            .disableItemTake()
            .create();
        boltPlugin = BoltUX.getBoltPlugin();

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
            GuiHandler.generateTrustMenu(player, protection, protectionLocation);
        }));

        return base;
    }

    public static void populateContent(PaginatedGui gui, Protection protection, Player player) {
        // Used so this method can reload the gui content
        gui.clearPageItems();

        final Store store = boltPlugin.getBolt().getStore();

        // Get untrusted suggested towns
        if (Hook.Towny.isLoaded() && Bukkit.getPluginManager().isPluginEnabled("BoltTowny")) {
            Set<Town> suggestedTowns = new HashSet<>(GuiHelper.getSuggestedTowns(player));
            suggestedTowns.removeAll(BoltUtil.getTrustedTowns(player));
            suggestedTowns.forEach(suggestedTown -> gui.addItem(townToAddableTrustIcon(gui, protection, suggestedTown)));
        }

        // Get untrusted groups
        final Set<Group> untrustedGroups = new HashSet<>();
        final List<String> groupNames = boltPlugin.getPlayersOwnedGroups(player);
        for (String groupName : groupNames) {
            store.loadGroup(groupName).thenAccept(untrustedGroups::add);
        }
        untrustedGroups.removeAll(BoltUtil.getTrustedGroups(player));
        untrustedGroups.forEach(group -> gui.addItem(groupToAddableTrustIcon(gui, protection, group)));

        // Get untrusted suggested players
        Set<UUID> suggestedPlayers = new HashSet<>(GuiHelper.getSuggestedPlayers(player, protection.getOwner()));
        suggestedPlayers.removeAll(BoltUtil.getTrustedPlayers(player));
        suggestedPlayers.forEach(suggestedPlayer -> gui.addItem(playerToAddableTrustIcon(gui, protection, Bukkit.getOfflinePlayer(suggestedPlayer))));
        gui.update();
    }

    private static GuiItem townToAddableTrustIcon(PaginatedGui gui, Protection protection, Town town) {
        final ItemStack townItem = new ItemStack(Material.ENDER_CHEST);
        final ItemMeta townMeta = townItem.getItemMeta();
        townMeta.displayName(translate("gui.trust-add.buttons.town.name", (c) -> {
            c.with("name", town.getName());
        }));
        townMeta.lore(translateList("gui.trust-add.buttons.town.lore", (c) -> {
            c.with("name", town.getName());
        }));
        townItem.setItemMeta(townMeta);
        return PaperItemBuilder.from(townItem).asGuiItem(event -> {
            final int slot = event.getSlot();
            final Store store = BoltUX.getBoltPlugin().getBolt().getStore();
            final AccessList accessList = Objects.requireNonNullElse(store.loadAccessList(protection.getOwner()).join(), new AccessList(protection.getOwner(), new HashMap<>()));
            store.saveAccessList(accessList);
            populateContent(gui, protection, (Player) event.getWhoClicked());
        });
    }

    private static GuiItem groupToAddableTrustIcon(PaginatedGui gui, Protection protection, Group group) {
        final ItemStack groupItem = new ItemStack(Material.CHEST);
        final ItemMeta groupMeta = groupItem.getItemMeta();
        groupMeta.displayName(translate("gui.trust-add.buttons.group.name", (c) -> {
            c.with("name", group.getName());
            c.with("owner", Objects.requireNonNull(Bukkit.getOfflinePlayer(group.getOwner()).getName()));
            c.with("members", getGroupMemberNames(group));
        }));
        groupMeta.lore(translateList("gui.trust-add.buttons.group.lore", (c) -> {
            c.with("name", group.getName());
            c.with("owner", Objects.requireNonNull(Bukkit.getOfflinePlayer(group.getOwner()).getName()));
            c.with("members", getGroupMemberNames(group));
        }));
        groupItem.setItemMeta(groupMeta);
        return PaperItemBuilder.from(groupItem).asGuiItem(event -> {
            final Store store = BoltUX.getBoltPlugin().getBolt().getStore();
            final AccessList accessList = Objects.requireNonNullElse(store.loadAccessList(protection.getOwner()).join(), new AccessList(protection.getOwner(), new HashMap<>()));
            store.saveAccessList(accessList);
            populateContent(gui, protection, (Player) event.getWhoClicked());
        });
    }

    private static GuiItem playerToAddableTrustIcon(PaginatedGui gui, Protection protection, OfflinePlayer player) {
        final ItemStack skullItem = new ItemStack(Material.PLAYER_HEAD);
        final SkullMeta skullMeta = (SkullMeta) skullItem.getItemMeta();
        skullMeta.setOwningPlayer(player);
        skullMeta.displayName(translate("gui.trust-add.buttons.player.name", (c) -> {
            c.with("name", Objects.requireNonNull(player.getName()));
        }));
        skullMeta.lore(translateList("gui.trust-add.buttons.player.lore", (c) -> {
            c.with("name", Objects.requireNonNull(player.getName()));
        }));
        skullItem.setItemMeta(skullMeta);
        return PaperItemBuilder.from(skullItem).asGuiItem(event -> {
            final Store store = BoltUX.getBoltPlugin().getBolt().getStore();
            final AccessList accessList = Objects.requireNonNullElse(store.loadAccessList(protection.getOwner()).join(), new AccessList(protection.getOwner(), new HashMap<>()));
            accessList.getAccess().put("player:" + player.getUniqueId(), "normal");
            store.saveAccessList(accessList);
            populateContent(gui, protection, (Player) event.getWhoClicked());
        });
    }
}

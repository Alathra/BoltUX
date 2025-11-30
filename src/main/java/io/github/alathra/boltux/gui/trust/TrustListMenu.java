package io.github.alathra.boltux.gui.trust;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import io.github.milkdrinkers.colorparser.paper.ColorParser;
import com.palmergames.bukkit.towny.object.Town;
import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import dev.triumphteam.gui.guis.PaginatedGui;
import io.github.alathra.boltux.BoltUX;
import io.github.alathra.boltux.gui.GuiHandler;
import io.github.alathra.boltux.hook.Hook;
import io.github.alathra.boltux.utility.BoltUtil;
import net.kyori.adventure.text.format.TextDecoration;
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

import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

public class TrustListMenu {

    private static BoltPlugin boltPlugin;

    public static PaginatedGui generateBase(Player player, Protection protection, Location protectionLocation) {
        PaginatedGui base = Gui.paginated()
            .title(ColorParser.of("List/Remove Trusted").build())
            .rows(6)
            .disableItemPlace()
            .disableItemSwap()
            .disableItemDrop()
            .disableItemTake()
            .create();
        boltPlugin = BoltUX.getBoltPlugin();

        // Apply gray glass pane border
        ItemStack grayBorder = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta grayBorderItemMeta = grayBorder.getItemMeta();
        grayBorderItemMeta.displayName(ColorParser.of("").build());
        grayBorder.setItemMeta(grayBorderItemMeta);
        base.getFiller().fillBorder(ItemBuilder.from(grayBorder).asGuiItem());

        // Create page nav buttons
        ItemStack nextPage = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta nextPageMeta = (SkullMeta) nextPage.getItemMeta();
        final UUID uuid1 = UUID.randomUUID();
        final PlayerProfile playerProfile1 = Bukkit.createProfile(uuid1, uuid1.toString().substring(0, 16));
        final String rightArrowTexture = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTliZjMyOTJlMTI2YTEwNWI1NGViYTcxM2FhMWIxNTJkNTQxYTFkODkzODgyOWM1NjM2NGQxNzhlZDIyYmYifX19";
        playerProfile1.setProperty(new ProfileProperty("textures", rightArrowTexture));
        nextPageMeta.setPlayerProfile(playerProfile1);
        nextPageMeta.displayName(ColorParser.of("<yellow>Next Page").build().decoration(TextDecoration.ITALIC, false));
        nextPage.setItemMeta(nextPageMeta);
        base.setItem(6, 6, ItemBuilder.from(nextPage).asGuiItem(event -> {
            base.next();
        }));

        ItemStack prevPage = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta prevPageMeta = (SkullMeta) prevPage.getItemMeta();
        final UUID uuid2 = UUID.randomUUID();
        final PlayerProfile playerProfile2 = Bukkit.createProfile(uuid2, uuid2.toString().substring(0, 16));
        final String leftArrowTexture = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmQ2OWUwNmU1ZGFkZmQ4NGU1ZjNkMWMyMTA2M2YyNTUzYjJmYTk0NWVlMWQ0ZDcxNTJmZGM1NDI1YmMxMmE5In19fQ==";
        playerProfile2.setProperty(new ProfileProperty("textures", leftArrowTexture));
        prevPageMeta.setPlayerProfile(playerProfile2);
        prevPageMeta.displayName(ColorParser.of("<yellow>Previous Page").build().decoration(TextDecoration.ITALIC, false));
        prevPage.setItemMeta(prevPageMeta);
        base.setItem(6, 4, ItemBuilder.from(prevPage).asGuiItem(event -> {
            base.previous();
        }));

        // Back button
        ItemStack backButton = new ItemStack(Material.PAPER);
        ItemMeta backButtonMeta = backButton.getItemMeta();
        backButtonMeta.displayName(ColorParser.of("<red>Back").build().decoration(TextDecoration.ITALIC, false));
        backButtonMeta.lore(List.of(
            ColorParser.of("<gray>Return to trust options menu").build().decoration(TextDecoration.ITALIC, false)
        ));
        backButton.setItemMeta(backButtonMeta);
        base.setItem(6, 1, ItemBuilder.from(backButton).asGuiItem(event -> {
            GuiHandler.generateTrustMenu(player, protection, protectionLocation);
        }));

        return base;
    }

    public static void populateContent(PaginatedGui gui, Protection protection, Player player) {
        if (Hook.Towny.isLoaded() && Bukkit.getPluginManager().isPluginEnabled("BoltTowny")) {
            BoltUtil.getTrustedTowns(player).forEach(trustedTown -> gui.addItem(townToRemovableTrustIcon(gui, protection, trustedTown)));
        }
        BoltUtil.getTrustedGroups(player).forEach(trustedGroup -> gui.addItem(groupToRemovableTrustIcon(gui, protection, trustedGroup)));
        BoltUtil.getTrustedPlayers(player).forEach(trustedPlayer -> gui.addItem(playerToRemovableTrustIcon(gui, protection, Bukkit.getOfflinePlayer(trustedPlayer))));
    }

    private static GuiItem townToRemovableTrustIcon(PaginatedGui gui, Protection protection, Town town) {
        ItemStack townItem = new ItemStack(Material.ENDER_CHEST);
        ItemMeta townMeta = townItem.getItemMeta();
        townMeta.displayName(ColorParser.of("<green>" + town.getName()).build().decoration(TextDecoration.ITALIC, false));
        townMeta.lore(List.of(
            ColorParser.of("<gray>Town").build().decoration(TextDecoration.ITALIC, false)
        ));
        townItem.setItemMeta(townMeta);
        return ItemBuilder.from(townItem).asGuiItem(event -> {
            final int slot = event.getSlot();
            final Store store = BoltUX.getBoltPlugin().getBolt().getStore();
            final AccessList accessList = Objects.requireNonNullElse(store.loadAccessList(protection.getOwner()).join(), new AccessList(protection.getOwner(), new HashMap<>()));
            accessList.getAccess().remove("town:" + town.getName());
            store.saveAccessList(accessList);
            townItem.setAmount(0);
            gui.updateItem(slot, townItem);
        });
    }

    private static GuiItem groupToRemovableTrustIcon(PaginatedGui gui, Protection protection, Group group) {
        ItemStack groupItem = new ItemStack(Material.CHEST);
        ItemMeta groupMeta = groupItem.getItemMeta();
        String groupMemberNames = String.join(", ",
            group.getMembers().stream()
                .map(uuid -> Bukkit.getOfflinePlayer(uuid).getName())
                .collect(Collectors.toSet())
        );
        groupMeta.displayName(ColorParser.of("<blue>" + group.getName()).build().decoration(TextDecoration.ITALIC, false));
        groupMeta.lore(List.of(
            ColorParser.of("<gray>Group").build().decoration(TextDecoration.ITALIC, false),
            ColorParser.of("<gray>Owner: " + Bukkit.getOfflinePlayer(group.getOwner()).getName()).build().decoration(TextDecoration.ITALIC, false),
            ColorParser.of("<gray>Members: " + groupMemberNames).build().decoration(TextDecoration.ITALIC, false),
            ColorParser.of("<gray>Click to revoke trust").build().decoration(TextDecoration.ITALIC, false)
        ));
        groupItem.setItemMeta(groupMeta);
        return ItemBuilder.from(groupItem).asGuiItem(event -> {
            final int slot = event.getSlot();
            final Store store = BoltUX.getBoltPlugin().getBolt().getStore();
            final AccessList accessList = Objects.requireNonNullElse(store.loadAccessList(protection.getOwner()).join(), new AccessList(protection.getOwner(), new HashMap<>()));
            accessList.getAccess().remove("group:" + group.getName());
            store.saveAccessList(accessList);
            groupItem.setAmount(0);
            gui.updateItem(slot, groupItem);
        });
    }

    private static GuiItem playerToRemovableTrustIcon(PaginatedGui gui, Protection protection, OfflinePlayer player) {
        ItemStack skullItem = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta skullMeta = (SkullMeta) skullItem.getItemMeta();
        skullMeta.setOwningPlayer(player);
        skullMeta.displayName(ColorParser.of("<green>" + player.getName()).build().decoration(TextDecoration.ITALIC, false));
        skullMeta.lore(List.of(
            ColorParser.of("<gray>Player").build().decoration(TextDecoration.ITALIC, false)
        ));
        skullItem.setItemMeta(skullMeta);
        return ItemBuilder.from(skullItem).asGuiItem(event -> {
            final int slot = event.getSlot();
            final Store store = BoltUX.getBoltPlugin().getBolt().getStore();
            final AccessList accessList = Objects.requireNonNullElse(store.loadAccessList(protection.getOwner()).join(), new AccessList(protection.getOwner(), new HashMap<>()));
            accessList.getAccess().remove("player:" + player.getUniqueId());
            store.saveAccessList(accessList);
            skullItem.setAmount(0);
            gui.updateItem(slot, skullItem);
        });
    }

}

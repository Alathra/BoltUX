package io.github.alathra.boltux.gui.edit;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import com.github.milkdrinkers.colorparser.ColorParser;
import com.palmergames.bukkit.towny.object.Town;
import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import dev.triumphteam.gui.guis.PaginatedGui;
import io.github.alathra.boltux.BoltUX;
import io.github.alathra.boltux.gui.GuiHandler;
import io.github.alathra.boltux.gui.GuiHelper;
import io.github.alathra.boltux.utility.BoltUtil;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.popcraft.bolt.BoltPlugin;
import org.popcraft.bolt.protection.Protection;
import org.popcraft.bolt.util.Group;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class ProtectionAccessAddMenu {

    private static BoltPlugin boltPlugin;

    public static PaginatedGui generateBase(Player player, Protection protection, Location protectionLocation) {
        PaginatedGui base = Gui.paginated()
            .title(ColorParser.of("Add Access").build())
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
            ColorParser.of("<gray>Return to access options menu").build().decoration(TextDecoration.ITALIC, false)
        ));
        backButton.setItemMeta(backButtonMeta);
        base.setItem(6, 1, ItemBuilder.from(backButton).asGuiItem(event -> {
            GuiHandler.generateProtectionAccessMenu(player, protection, protectionLocation);
        }));

        return base;
    }

    public static void populateContent(PaginatedGui gui, Player player, Protection protection) {

        // Get all suggested towns that haven't been granted access
        if (BoltUX.getTownyHook().isHookLoaded() && Bukkit.getPluginManager().isPluginEnabled("BoltTowny")) {
            Set<Town> townSuggestions = new HashSet<>(GuiHelper.getSuggestedTowns(player));
            townSuggestions.removeAll(BoltUtil.getTownAccessSet(protection));
            townSuggestions.forEach(suggestedTown -> gui.addItem(townToAddableAccessIcon(gui, protection, suggestedTown)));
        }

        // Get all groups the player owns that haven't been granted access
        BoltUtil.getGroupsWithoutAccess(protection).forEach(group -> gui.addItem(groupToAddableAccessIcon(gui, protection, group)));

        // Get suggested players that haven't been granted access
        Set<UUID> playerSuggestions = new HashSet<>(GuiHelper.getSuggestedPlayers(player));
        playerSuggestions.removeAll(BoltUtil.getPlayerAccessSet(protection));
        playerSuggestions.forEach(suggestedPlayer -> gui.addItem(playerToAddableAccessIcon(gui, protection, Bukkit.getOfflinePlayer(suggestedPlayer))));
    }

    private static GuiItem townToAddableAccessIcon(PaginatedGui gui, Protection protection, Town town) {
        ItemStack townItem = new ItemStack(Material.ENDER_CHEST);
        ItemMeta townMeta = townItem.getItemMeta();
        townMeta.displayName(ColorParser.of("<green>" + town.getName()).build().decoration(TextDecoration.ITALIC, false));
        townMeta.lore(List.of(
            ColorParser.of("<gray>Town").build().decoration(TextDecoration.ITALIC, false),
            ColorParser.of("<gray>Click to grant access").build().decoration(TextDecoration.ITALIC, false)
        ));
        townItem.setItemMeta(townMeta);
        return ItemBuilder.from(townItem).asGuiItem(event -> {
            final int slot = event.getSlot();
            protection.getAccess().put("town:" + town.getName(), "normal");
            BoltUX.getBoltPlugin().saveProtection(protection);
            townItem.addUnsafeEnchantment(Enchantment.BINDING_CURSE, 1);
            townItem.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            townMeta.lore(List.of(
                ColorParser.of("<gray>Town").build().decoration(TextDecoration.ITALIC, false),
                ColorParser.of("<green>Access has been granted").build().decoration(TextDecoration.ITALIC, false)

            ));
            townItem.setItemMeta(townMeta);
            gui.updateItem(slot, townItem);
        });
    }

    private static GuiItem groupToAddableAccessIcon(PaginatedGui gui, Protection protection, Group group) {
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
            ColorParser.of("<gray>Click to grant access").build().decoration(TextDecoration.ITALIC, false)
        ));
        groupItem.setItemMeta(groupMeta);
        return ItemBuilder.from(groupItem).asGuiItem(event -> {
            final int slot = event.getSlot();
            protection.getAccess().put("group:" + group.getName(), "normal");
            BoltUX.getBoltPlugin().saveProtection(protection);
            groupItem.addUnsafeEnchantment(Enchantment.BINDING_CURSE, 1);
            groupItem.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            groupMeta.lore(List.of(
                ColorParser.of("<gray>Group").build().decoration(TextDecoration.ITALIC, false),
                ColorParser.of("<gray>Owner: " + Bukkit.getOfflinePlayer(group.getOwner()).getName()).build().decoration(TextDecoration.ITALIC, false),
                ColorParser.of("<gray>Members: " + groupMemberNames).build().decoration(TextDecoration.ITALIC, false),
                ColorParser.of("<green>Access has been granted").build().decoration(TextDecoration.ITALIC, false)
            ));
            groupItem.setItemMeta(groupMeta);
            gui.updateItem(slot, groupItem);
        });
    }

    private static GuiItem playerToAddableAccessIcon(PaginatedGui gui, Protection protection, OfflinePlayer player) {
        ItemStack skullItem = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta skullMeta = (SkullMeta) skullItem.getItemMeta();
        skullMeta.setOwningPlayer(player);
        skullMeta.displayName(ColorParser.of("<green>" + player.getName()).build().decoration(TextDecoration.ITALIC, false));
        skullMeta.lore(List.of(
            ColorParser.of("<gray>Player").build().decoration(TextDecoration.ITALIC, false),
            ColorParser.of("<gray>Click to grant access").build().decoration(TextDecoration.ITALIC, false)
        ));
        skullItem.setItemMeta(skullMeta);
        return ItemBuilder.from(skullItem).asGuiItem(event -> {
            final int slot = event.getSlot();
            protection.getAccess().put("player:" + player.getUniqueId(), "normal");
            BoltUX.getBoltPlugin().saveProtection(protection);
            skullItem.addUnsafeEnchantment(Enchantment.BINDING_CURSE, 1);
            skullItem.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            skullMeta.lore(List.of(
                ColorParser.of("<gray>Player").build().decoration(TextDecoration.ITALIC, false),
                ColorParser.of("<green>Access has been granted").build().decoration(TextDecoration.ITALIC, false)

            ));
            skullItem.setItemMeta(skullMeta);
            gui.updateItem(slot, skullItem);
        });
    }

}

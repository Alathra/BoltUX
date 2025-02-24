package io.github.alathra.boltux.gui.trust;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import com.github.milkdrinkers.colorparser.ColorParser;
import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.PaginatedGui;
import io.github.alathra.boltux.BoltUX;
import io.github.alathra.boltux.gui.GuiHandler;
import io.github.alathra.boltux.gui.GuiHelper;
import io.github.alathra.boltux.utility.BoltUtil;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.popcraft.bolt.BoltPlugin;
import org.popcraft.bolt.protection.Protection;

import java.util.List;
import java.util.UUID;

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

    public static void populateContent(PaginatedGui gui, Player player) {
        if (BoltUX.getTownyHook().isHookLoaded() && Bukkit.getPluginManager().isPluginEnabled("BoltTowny")) {
            BoltUtil.getTrustedTowns(player).forEach(trustedTown -> gui.addItem(GuiHelper.townToRemovableTrustIcon(gui, player, trustedTown)));
        }
        BoltUtil.getTrustedGroups(player).forEach(trustedGroup -> gui.addItem(GuiHelper.groupToRemovableTrustIcon(gui, player, trustedGroup)));
        BoltUtil.getTrustedPlayers(player).forEach(trustedPlayer -> gui.addItem(GuiHelper.playerToRemovableTrustIcon(gui, player, trustedPlayer)));
    }

}

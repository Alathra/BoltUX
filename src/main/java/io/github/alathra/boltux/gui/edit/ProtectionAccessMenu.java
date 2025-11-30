package io.github.alathra.boltux.gui.edit;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import io.github.alathra.boltux.gui.GuiHandler;
import io.github.milkdrinkers.colorparser.paper.ColorParser;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.popcraft.bolt.protection.Protection;

import java.util.List;

public final class ProtectionAccessMenu {
    public static Gui generateBase() {
        Gui base = Gui.gui()
            .title(ColorParser.of("Access Options").build())
            .rows(3)
            .disableItemPlace()
            .disableItemSwap()
            .disableItemDrop()
            .disableItemTake()
            .create();

        // Apply gray glass pane border
        ItemStack grayBorder = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta grayBorderItemMeta = grayBorder.getItemMeta();
        grayBorderItemMeta.displayName(ColorParser.of("").build());
        grayBorder.setItemMeta(grayBorderItemMeta);
        base.getFiller().fillBorder(ItemBuilder.from(grayBorder).asGuiItem());

        return base;
    }

    public static void generateButtons(Gui gui, Player player, Protection protection, Location protectionLocation) {

        // Add Access Button
        ItemStack addAccessButton = new ItemStack(Material.WRITABLE_BOOK);
        ItemMeta addAccessButtonMeta = addAccessButton.getItemMeta();
        addAccessButtonMeta.displayName(ColorParser.of("<green>Add Access").build().decoration(TextDecoration.ITALIC, false));
        addAccessButtonMeta.lore(List.of(
            ColorParser.of("<gray>Grant new access to players and groups").build().decoration(TextDecoration.ITALIC, false),
            ColorParser.of("<gray>Applies to this specific protection").build().decoration(TextDecoration.ITALIC, false)
        ));
        addAccessButton.setItemMeta(addAccessButtonMeta);
        gui.setItem(2, 4, ItemBuilder.from(addAccessButton).asGuiItem(event -> {
            GuiHandler.generateProtectionAccessAddMenu(player, protection, protectionLocation);
        }));

        // List/Remove Access Button
        ItemStack listRemoveAccessButton = new ItemStack(Material.BOOK);
        ItemMeta listRemoveAccessMeta = listRemoveAccessButton.getItemMeta();
        listRemoveAccessMeta.displayName(ColorParser.of("<yellow>List/Remove Access").build().decoration(TextDecoration.ITALIC, false));
        listRemoveAccessMeta.lore(List.of(
            ColorParser.of("<gray>List or remove access for players and groups").build().decoration(TextDecoration.ITALIC, false),
            ColorParser.of("<gray>Applies to this specific protection").build().decoration(TextDecoration.ITALIC, false)
        ));
        listRemoveAccessButton.setItemMeta(listRemoveAccessMeta);
        gui.setItem(2, 6, ItemBuilder.from(listRemoveAccessButton).asGuiItem(event -> {
            GuiHandler.generateProtectionAccessListMenu(player, protection, protectionLocation);
        }));

        // Back button
        ItemStack backButton = new ItemStack(Material.PAPER);
        ItemMeta backButtonMeta = backButton.getItemMeta();
        backButtonMeta.displayName(ColorParser.of("<red>Back").build().decoration(TextDecoration.ITALIC, false));
        backButtonMeta.lore(List.of(
            ColorParser.of("<gray>Return to main menu").build().decoration(TextDecoration.ITALIC, false)
        ));
        backButton.setItemMeta(backButtonMeta);
        gui.setItem(3, 1, ItemBuilder.from(backButton).asGuiItem(event -> {
            GuiHandler.generateMainMenu(player, protection, protectionLocation);
        }));
    }
}

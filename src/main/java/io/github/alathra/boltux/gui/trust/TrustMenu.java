package io.github.alathra.boltux.gui.trust;

import dev.triumphteam.gui.builder.item.PaperItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import io.github.alathra.boltux.gui.AbstractGui;
import io.github.alathra.boltux.gui.GuiHandler;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.popcraft.bolt.protection.Protection;

public final class TrustMenu extends AbstractGui {
    public static Gui generateBase() {
        Gui base = Gui.gui()
            .title(translate("gui.trust.title"))
            .rows(3)
            .disableItemPlace()
            .disableItemSwap()
            .disableItemDrop()
            .disableItemTake()
            .create();

        // Apply gray glass pane border
        base.getFiller().fillBorder(PaperItemBuilder.from(borderItem()).asGuiItem());

        return base;
    }

    public static void generateButtons(Gui gui, Player player, Protection protection, Location protectionLocation) {
        // Add Access Button
        ItemStack addAccessButton = new ItemStack(Material.WRITABLE_BOOK);
        ItemMeta addAccessButtonMeta = addAccessButton.getItemMeta();
        addAccessButtonMeta.displayName(translate("gui.trust.buttons.add.name", player));
        addAccessButtonMeta.lore(translateList("gui.trust.buttons.add.lore", player));
        addAccessButton.setItemMeta(addAccessButtonMeta);
        gui.setItem(2, 4, PaperItemBuilder.from(addAccessButton).asGuiItem(event -> {
            GuiHandler.generateTrustAddMenu(player, protection, protectionLocation);
        }));

        // List/Remove Access Button
        ItemStack listRemoveAccessButton = new ItemStack(Material.BOOK);
        ItemMeta listRemoveAccessMeta = listRemoveAccessButton.getItemMeta();
        listRemoveAccessMeta.displayName(translate("gui.trust.buttons.list.name", player));
        listRemoveAccessMeta.lore(translateList("gui.trust.buttons.list.lore", player));
        listRemoveAccessButton.setItemMeta(listRemoveAccessMeta);
        gui.setItem(2, 6, PaperItemBuilder.from(listRemoveAccessButton).asGuiItem(event -> {
            GuiHandler.generateTrustListMenu(player, protection, protectionLocation);
        }));

        // Back button
        gui.setItem(3, 1, PaperItemBuilder.from(backButton()).asGuiItem(event -> {
            GuiHandler.generateMainMenu(player, protection, protectionLocation);
        }));

    }
}

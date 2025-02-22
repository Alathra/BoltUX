package io.github.alathra.boltux.gui;

import com.github.milkdrinkers.colorparser.ColorParser;
import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import io.github.alathra.boltux.BoltUX;
import io.github.alathra.boltux.api.BoltUXAPI;
import io.github.alathra.boltux.config.Settings;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.popcraft.bolt.BoltPlugin;
import org.popcraft.bolt.protection.Protection;

import java.util.List;

public class MainMenu {

    private static BoltPlugin boltPlugin;

    public static Gui generateBase() {
        Gui base = Gui.gui()
            .title(ColorParser.of("Protection Options").build())
            .rows(1)
            .disableItemPlace()
            .disableItemSwap()
            .disableItemDrop()
            .disableItemTake()
            .create();
        boltPlugin = BoltUX.getBoltPlugin();
        return base;
    }

    public static void generateButtons(Gui gui, Player player, Protection protection, Location protectionLocation) {

        // Access (Edit) Button
        ItemStack editPlayerAccessButton = new ItemStack(Material.OAK_FENCE_GATE);
        ItemMeta editPlayerAccessButtonMeta = editPlayerAccessButton.getItemMeta();
        editPlayerAccessButtonMeta.displayName(ColorParser.of("<green>Edit Access").build().decoration(TextDecoration.ITALIC, false));
        editPlayerAccessButtonMeta.lore(List.of(
            ColorParser.of("<gray>Edit access for players and groups").build().decoration(TextDecoration.ITALIC, false),
            ColorParser.of("<gray>Applies to this specific protection").build().decoration(TextDecoration.ITALIC, false)
        ));
        editPlayerAccessButton.setItemMeta(editPlayerAccessButtonMeta);
        gui.setItem(1, 1, ItemBuilder.from(editPlayerAccessButton).asGuiItem(event -> {
            GuiHandler.generateProtectionEditMenu(player, protection, protectionLocation);
        }));

        // Transfer Ownership Button
        ItemStack transferButton = new ItemStack(Material.GOLDEN_HELMET);
        ItemMeta transferButtonMeta = transferButton.getItemMeta();
        transferButtonMeta.displayName(ColorParser.of("<dark_purple>Transfer Ownership").build().decoration(TextDecoration.ITALIC, false));
        transferButtonMeta.lore(List.of(
            ColorParser.of("<gray>Transfer ownership to another player").build().decoration(TextDecoration.ITALIC, false),
            ColorParser.of("<gray>Applies to this specific protection").build().decoration(TextDecoration.ITALIC, false)
        ));
        transferButton.setItemMeta(transferButtonMeta);
        gui.setItem(1, 2, ItemBuilder.from(transferButton).asGuiItem(event -> {
            GuiHandler.generateTransferMenu(player, protection, protectionLocation);
        }));

        // Unlock/Delete Button
        ItemStack unlockButton = new ItemStack(Material.IRON_AXE);
        ItemMeta unlockButtonMeta = unlockButton.getItemMeta();
        unlockButtonMeta.displayName(ColorParser.of("<blue>Unlock").build().decoration(TextDecoration.ITALIC, false));
        unlockButtonMeta.lore(List.of(
            ColorParser.of("<gray>Delete this protection").build().decoration(TextDecoration.ITALIC, false)
        ));
        unlockButton.setItemMeta(unlockButtonMeta);
        gui.setItem(1, 3, ItemBuilder.from(unlockButton).asGuiItem(event -> {
            if (Settings.isLockingEnabled() && Settings.getLockItemEnabledWorlds().contains(player.getWorld())) {
                player.getWorld().dropItemNaturally(protectionLocation, BoltUXAPI.getLockItem());
            }
            gui.close(player);
        }));

        // Global Settings Button
        ItemStack globalSettingsButton = new ItemStack(Material.HEART_OF_THE_SEA);
        ItemMeta globalSettingsButtonMeta = globalSettingsButton.getItemMeta();
        globalSettingsButtonMeta.displayName(ColorParser.of("<gold>Trust Settings").build().decoration(TextDecoration.ITALIC, false));
        globalSettingsButtonMeta.lore(List.of(
            ColorParser.of("<gray>Manage your trusted groups and players").build().decoration(TextDecoration.ITALIC, false)
        ));
        globalSettingsButton.setItemMeta(globalSettingsButtonMeta);
        gui.setItem(1, 8, ItemBuilder.from(globalSettingsButton).asGuiItem(event -> {
            GuiHandler.generateTrustMenu(player, protection, protectionLocation);
        }));

        // Exit Icon
        ItemStack exitButton = new ItemStack(Material.BARRIER);
        ItemMeta exitButtonMeta = exitButton.getItemMeta();
        exitButtonMeta.displayName(ColorParser.of("<red>Exit").build().decoration(TextDecoration.ITALIC, false));
        exitButton.setItemMeta(exitButtonMeta);
        gui.setItem(1, 9, ItemBuilder.from(exitButton).asGuiItem(event -> {
            gui.close(player);
        }));
    }

}

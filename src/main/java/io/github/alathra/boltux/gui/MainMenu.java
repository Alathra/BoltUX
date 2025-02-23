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

import java.util.LinkedList;
import java.util.List;

public class MainMenu {

    private static BoltPlugin boltPlugin;
    private static final List<String> modes = new LinkedList<>(List.of("deposit", "display", "private", "public", "withdrawal"));

    public static Gui generateBase() {
        Gui base = Gui.gui()
            .title(ColorParser.of("Protection Options").build())
            .rows(5)
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
        gui.setItem(2, 3, ItemBuilder.from(editPlayerAccessButton).asGuiItem(event -> {
            GuiHandler.generateProtectionAccessMenu(player, protection, protectionLocation);
        }));

        // Change Mode Button
        ItemStack modeChangeButton = new ItemStack(Material.HOPPER);
        ItemMeta modeChangeMeta = modeChangeButton.getItemMeta();
        modeChangeMeta.displayName(ColorParser.of("<light_purple>Change Mode").build().decoration(TextDecoration.ITALIC, false));
        modeChangeMeta.lore(List.of(
            ColorParser.of("<gray>Current: <yellow>" + generateFormattedModeName(protection)).build().decoration(TextDecoration.ITALIC, false),
            ColorParser.of("<gray>" + generateModeDescription(protection)).build().decoration(TextDecoration.ITALIC, false),
            ColorParser.of("").build(),
            ColorParser.of("<gray>Applies to this specific protection").build().decoration(TextDecoration.ITALIC, false),
            ColorParser.of("<gray>Only applicable for containers").build().decoration(TextDecoration.ITALIC, false)
        ));
        modeChangeButton.setItemMeta(modeChangeMeta);
        gui.setItem(2, 5, ItemBuilder.from(modeChangeButton).asGuiItem(event -> {
            if (event.isLeftClick()) {
                protection.setType(getNextMode(protection.getType()));
            } else if (event.isRightClick()) {
                protection.setType(getPreviousMode(protection.getType()));
            }
            boltPlugin.saveProtection(protection);
            modeChangeMeta.lore(List.of(
                ColorParser.of("<gray>Current: <yellow>" + generateFormattedModeName(protection)).build().decoration(TextDecoration.ITALIC, false),
                ColorParser.of("<gray>" + generateModeDescription(protection)).build().decoration(TextDecoration.ITALIC, false),
                ColorParser.of("").build(),
                ColorParser.of("<gray>Applies to this specific protection").build().decoration(TextDecoration.ITALIC, false),
                ColorParser.of("<gray>Only applicable for containers").build().decoration(TextDecoration.ITALIC, false)
            ));
            modeChangeButton.setItemMeta(modeChangeMeta);
            gui.updateItem(2, 5, modeChangeButton);
        }));

        // Trust Settings Button
        ItemStack globalSettingsButton = new ItemStack(Material.HEART_OF_THE_SEA);
        ItemMeta globalSettingsButtonMeta = globalSettingsButton.getItemMeta();
        globalSettingsButtonMeta.displayName(ColorParser.of("<gold>Trust Settings").build().decoration(TextDecoration.ITALIC, false));
        globalSettingsButtonMeta.lore(List.of(
            ColorParser.of("<gray>Manage your trusted groups and players").build().decoration(TextDecoration.ITALIC, false)
        ));
        globalSettingsButton.setItemMeta(globalSettingsButtonMeta);
        gui.setItem(2, 7, ItemBuilder.from(globalSettingsButton).asGuiItem(event -> {
            GuiHandler.generateTrustMenu(player, protection, protectionLocation);
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
        gui.setItem(4, 4, ItemBuilder.from(transferButton).asGuiItem(event -> {
            GuiHandler.generateTransferMenu(player, protection, protectionLocation);
        }));

        // Unlock/Delete Button
        ItemStack unlockButton = new ItemStack(Material.IRON_AXE);
        ItemMeta unlockButtonMeta = unlockButton.getItemMeta();
        unlockButtonMeta.displayName(ColorParser.of("<red>Unlock").build().decoration(TextDecoration.ITALIC, false));
        unlockButtonMeta.lore(List.of(
            ColorParser.of("<gray>Delete this protection").build().decoration(TextDecoration.ITALIC, false)
        ));
        unlockButton.setItemMeta(unlockButtonMeta);
        gui.setItem(4, 6, ItemBuilder.from(unlockButton).asGuiItem(event -> {
            if (Settings.isLockingEnabled() && Settings.getLockItemEnabledWorlds().contains(player.getWorld())) {
                player.getWorld().dropItemNaturally(protectionLocation, BoltUXAPI.getLockItem());
            }
            boltPlugin.removeProtection(protection);
            gui.close(player);
        }));

        // Exit Icon
        ItemStack exitButton = new ItemStack(Material.BARRIER);
        ItemMeta exitButtonMeta = exitButton.getItemMeta();
        exitButtonMeta.displayName(ColorParser.of("<red>Exit").build().decoration(TextDecoration.ITALIC, false));
        exitButton.setItemMeta(exitButtonMeta);
        gui.setItem(5, 9, ItemBuilder.from(exitButton).asGuiItem(event -> {
            gui.close(player);
        }));
    }

    private static String generateModeDescription(Protection protection) {
        String description = "";
        switch (protection.getType()) {
            case "deposit" -> description = "Items can be added but not removed";
            case "display" -> description = "Items can viewed but not added or removed";
            case "private" -> description = "Items cannot be viewed";
            case "public" -> description = "Items can be added or removed";
            case "withdrawal" -> description = "Items can be removed but not added";
        }
        return description;
    }

    private static String generateFormattedModeName(Protection protection) {
        // capitalize first letter
        return protection.getType().substring(0, 1).toUpperCase() + protection.getType().substring(1);
    }

    private static String getNextMode(String currentMode) {
        int index = modes.indexOf(currentMode);
        return modes.get((index + 1) % modes.size());
    }

    private static String getPreviousMode(String currentMode) {
        int index = modes.indexOf(currentMode);
        return modes.get((index - 1 + modes.size()) % modes.size());
    }


}

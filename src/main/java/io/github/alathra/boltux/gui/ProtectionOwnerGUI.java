package io.github.alathra.boltux.gui;

import com.github.milkdrinkers.colorparser.ColorParser;
import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import io.github.alathra.boltux.BoltUX;
import io.github.alathra.boltux.api.BoltUXAPI;
import io.github.alathra.boltux.config.Settings;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.popcraft.bolt.BoltPlugin;
import org.popcraft.bolt.protection.BlockProtection;
import org.popcraft.bolt.protection.EntityProtection;
import org.popcraft.bolt.protection.Protection;

import java.util.List;

public class ProtectionOwnerGUI {

    private static BoltPlugin boltPlugin;

    public static Gui generateBase(Player player, Protection protection) {
        Gui guiBase = null;
        if (protection instanceof BlockProtection blockProtection) {
            guiBase = Gui.gui()
                .title(ColorParser.of(player.getName() + "'s Protected " + blockProtection.getBlock()).build())
                .rows(1)
                .disableItemPlace()
                .disableItemSwap()
                .disableItemDrop()
                .disableItemTake()
                .create();
        }
        else if (protection instanceof EntityProtection entityProtection) {
            guiBase = Gui.gui()
                .title(ColorParser.of(player.getName() + "'s Protected " + entityProtection.getEntity()).build())
                .rows(1)
                .disableItemPlace()
                .disableItemSwap()
                .disableItemDrop()
                .disableItemTake()
                .create();
        }
        boltPlugin = BoltUX.getBoltPlugin();
        return guiBase;
    }

    public static void generateButtons(Gui gui, Player player, Protection protection, Location protectionLocation) {

        // Info Icon
        ItemStack infoIcon = new ItemStack(Material.OAK_SIGN);
        ItemMeta infoIconMeta = infoIcon.getItemMeta();
        String ownerName = Bukkit.getOfflinePlayer(protection.getOwner()).getName();
        infoIconMeta.displayName(ColorParser.of("<yellow>Protection Info").build().decoration(TextDecoration.ITALIC, false));
        infoIconMeta.lore(List.of(
            ColorParser.of("<gray>Owner: " + ownerName).build().decoration(TextDecoration.ITALIC, false)
        ));
        infoIcon.setItemMeta(infoIconMeta);
        gui.setItem(1, 1, ItemBuilder.from(infoIcon).asGuiItem());

        // Individual Access (Edit) Button
        ItemStack editPlayerAccessButton = new ItemStack(Material.OAK_FENCE_GATE);
        ItemMeta editPlayerAccessButtonMeta = editPlayerAccessButton.getItemMeta();
        editPlayerAccessButtonMeta.displayName(ColorParser.of("<green>Edit Player Access").build().decoration(TextDecoration.ITALIC, false));
        editPlayerAccessButtonMeta.lore(List.of(
            ColorParser.of("<gray>Grant access to individual players").build().decoration(TextDecoration.ITALIC, false),
            ColorParser.of("<gray>Applies to this specific block/entity").build().decoration(TextDecoration.ITALIC, false)
        ));
        editPlayerAccessButton.setItemMeta(editPlayerAccessButtonMeta);
        gui.setItem(1, 2, ItemBuilder.from(editPlayerAccessButton).asGuiItem(event -> {

        }));

        // Group Access (Edit) Button
        ItemStack editGroupAccessButton = new ItemStack(Material.CRAFTING_TABLE);
        ItemMeta editGroupButtonMeta = editGroupAccessButton.getItemMeta();
        editGroupButtonMeta.displayName(ColorParser.of("<green>Edit Group Access").build().decoration(TextDecoration.ITALIC, false));
        editGroupButtonMeta.lore(List.of(
            ColorParser.of("<gray>Grant access to groups of players").build().decoration(TextDecoration.ITALIC, false),
            ColorParser.of("<gray>Applies to this specific block/entity").build().decoration(TextDecoration.ITALIC, false)
        ));
        editGroupAccessButton.setItemMeta(editGroupButtonMeta);
        gui.setItem(1, 3, ItemBuilder.from(editGroupAccessButton).asGuiItem(event -> {

        }));

        // Transfer Ownership Button
        ItemStack transferButton = new ItemStack(Material.GOLDEN_HELMET);
        ItemMeta transferButtonMeta = transferButton.getItemMeta();
        transferButtonMeta.displayName(ColorParser.of("<dark_purple>Transfer Ownership").build().decoration(TextDecoration.ITALIC, false));
        transferButtonMeta.lore(List.of(
            ColorParser.of("<gray>Transfer ownership to another player").build().decoration(TextDecoration.ITALIC, false),
            ColorParser.of("<gray>Applies to this specific block/entity").build().decoration(TextDecoration.ITALIC, false)
        ));
        transferButton.setItemMeta(transferButtonMeta);
        gui.setItem(1, 4, ItemBuilder.from(transferButton).asGuiItem(event -> {

        }));

        // Unlock Button
        ItemStack unlockButton = new ItemStack(Material.IRON_AXE);
        ItemMeta unlockButtonMeta = unlockButton.getItemMeta();
        unlockButtonMeta.displayName(ColorParser.of("<blue>Unlock").build().decoration(TextDecoration.ITALIC, false));
        unlockButtonMeta.lore(List.of(
            ColorParser.of("<gray>Unlock this protected block/entity").build().decoration(TextDecoration.ITALIC, false)
        ));
        unlockButton.setItemMeta(unlockButtonMeta);
        gui.setItem(1, 5, ItemBuilder.from(unlockButton).asGuiItem(event -> {
            if (Settings.isLockingEnabled() && Settings.getLockItemEnabledWorlds().contains(player.getWorld())) {
                player.getWorld().dropItemNaturally(protectionLocation, BoltUXAPI.getLockItem());
            }
            gui.close(player);
        }));

        // Global Settings Button
        ItemStack globalSettingsButton = new ItemStack(Material.HEART_OF_THE_SEA);
        ItemMeta globalSettingsButtonMeta = globalSettingsButton.getItemMeta();
        globalSettingsButtonMeta.displayName(ColorParser.of("<gold>Global Settings").build().decoration(TextDecoration.ITALIC, false));
        globalSettingsButtonMeta.lore(List.of(
            ColorParser.of("<gray>Manage your groups and trusted players").build().decoration(TextDecoration.ITALIC, false)
        ));
        globalSettingsButton.setItemMeta(globalSettingsButtonMeta);
        gui.setItem(1, 8, ItemBuilder.from(globalSettingsButton).asGuiItem(event -> {

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

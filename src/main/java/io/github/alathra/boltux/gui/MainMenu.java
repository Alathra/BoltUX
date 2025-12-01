package io.github.alathra.boltux.gui;

import dev.triumphteam.gui.builder.item.PaperItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import io.github.alathra.boltux.BoltUX;
import io.github.alathra.boltux.api.BoltUXAPI;
import io.github.alathra.boltux.config.Settings;
import io.github.milkdrinkers.colorparser.paper.ColorParser;
import io.github.milkdrinkers.wordweaver.Translation;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.popcraft.bolt.BoltPlugin;
import org.popcraft.bolt.protection.Protection;

import java.util.LinkedList;
import java.util.List;

public final class MainMenu extends AbstractGui {
    private static BoltPlugin boltPlugin;
    private static final List<String> modes = new LinkedList<>(List.of("deposit", "display", "private", "public", "withdrawal"));

    public static Gui generateBase() {
        Gui base = Gui.gui()
            .title(translate("gui.main.title"))
            .rows(5)
            .disableItemPlace()
            .disableItemSwap()
            .disableItemDrop()
            .disableItemTake()
            .create();
        boltPlugin = BoltUX.getBoltPlugin();

        // Apply gray glass pane border
        base.getFiller().fillBorder(PaperItemBuilder.from(borderItem()).asGuiItem());

        return base;
    }

    public static void generateButtons(Gui gui, Player player, Protection protection, Location protectionLocation) {
        // Access (Edit) Button
        final ItemStack editPlayerAccessButton = new ItemStack(Material.OAK_FENCE_GATE);
        final ItemMeta editPlayerAccessButtonMeta = editPlayerAccessButton.getItemMeta();
        editPlayerAccessButtonMeta.displayName(translate("gui.main.buttons.access.name"));
        editPlayerAccessButtonMeta.lore(translateList("gui.main.buttons.access.lore"));
        editPlayerAccessButton.setItemMeta(editPlayerAccessButtonMeta);
        gui.setItem(2, 3, PaperItemBuilder.from(editPlayerAccessButton).asGuiItem(event -> {
            GuiHandler.generateProtectionAccessMenu(player, protection, protectionLocation);
        }));

        // Change Mode Button
        final ItemStack modeChangeButton = new ItemStack(Material.HOPPER);
        final ItemMeta modeChangeMeta = modeChangeButton.getItemMeta();
        modeChangeMeta.displayName(translate("gui.main.buttons.mode.name"));
        modeChangeMeta.lore(
            Translation.ofList("gui.main.buttons.mode.lore")
                .stream()
                .map(s -> {
                    return ColorParser.of(s)
                        .with("mode_name", Translation.as("gui.main.modes." + protection + ".name"))
                        .with("mode_description", Translation.as("gui.main.modes." + protection + ".description"))
                        .build();
                })
                .toList()
        );
        modeChangeButton.setItemMeta(modeChangeMeta);
        gui.setItem(2, 5, PaperItemBuilder.from(modeChangeButton).asGuiItem(event -> {
            if (event.isLeftClick()) {
                protection.setType(getNextMode(protection.getType()));
            } else if (event.isRightClick()) {
                protection.setType(getPreviousMode(protection.getType()));
            }
            boltPlugin.saveProtection(protection);
            modeChangeMeta.lore(
                Translation.ofList("gui.main.buttons.mode.lore")
                    .stream()
                    .map(s -> {
                        return ColorParser.of(s)
                            .with("mode_name", Translation.as("gui.main.modes." + protection + ".name"))
                            .with("mode_description", Translation.as("gui.main.modes." + protection + ".description"))
                            .build();
                    })
                    .toList()
            );
            modeChangeButton.setItemMeta(modeChangeMeta);
            gui.updateItem(2, 5, modeChangeButton);
        }));

        // Trust Settings Button
        final ItemStack globalSettingsButton = new ItemStack(Material.HEART_OF_THE_SEA);
        final ItemMeta globalSettingsButtonMeta = globalSettingsButton.getItemMeta();
        globalSettingsButtonMeta.displayName(translate("gui.main.buttons.trust.name"));
        globalSettingsButtonMeta.lore(translateList("gui.main.buttons.trust.lore"));
        globalSettingsButton.setItemMeta(globalSettingsButtonMeta);
        gui.setItem(2, 7, PaperItemBuilder.from(globalSettingsButton).asGuiItem(event -> {
            GuiHandler.generateTrustMenu(player, protection, protectionLocation);
        }));

        // Transfer Ownership Button
        final ItemStack transferButton = new ItemStack(Material.GOLDEN_HELMET);
        final ItemMeta transferButtonMeta = transferButton.getItemMeta();
        transferButtonMeta.displayName(translate("gui.main.buttons.transfer.name"));
        transferButtonMeta.lore(translateList("gui.main.buttons.transfer.lore"));
        transferButton.setItemMeta(transferButtonMeta);
        gui.setItem(4, 4, PaperItemBuilder.from(transferButton).asGuiItem(event -> {
            GuiHandler.generateTransferMenu(player, protection, protectionLocation);
        }));

        // Unlock/Delete Button
        final ItemStack unlockButton = new ItemStack(Material.IRON_AXE);
        final ItemMeta unlockButtonMeta = unlockButton.getItemMeta();
        unlockButtonMeta.displayName(translate("gui.main.buttons.delete.name"));
        unlockButtonMeta.lore(translateList("gui.main.buttons.delete.lore"));
        unlockButton.setItemMeta(unlockButtonMeta);
        gui.setItem(4, 6, PaperItemBuilder.from(unlockButton).asGuiItem(event -> {
            if (Settings.isLockItemEnabled() && Settings.getLockItemEnabledWorlds().contains(player.getWorld())) {
                player.getWorld().dropItemNaturally(protectionLocation, BoltUXAPI.getLockItem());
            }
            boltPlugin.removeProtection(protection);
            gui.close(player);
        }));

        // Exit Icon
        gui.setItem(5, 9, PaperItemBuilder.from(closeButton()).asGuiItem(event -> {
            gui.close(player);
        }));
    }

    private static String getNextMode(String currentMode) {
        final int index = modes.indexOf(currentMode);
        return modes.get((index + 1) % modes.size());
    }

    private static String getPreviousMode(String currentMode) {
        final int index = modes.indexOf(currentMode);
        return modes.get((index - 1 + modes.size()) % modes.size());
    }
}

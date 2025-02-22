package io.github.alathra.boltux.gui;

import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.PaginatedGui;
import io.github.alathra.boltux.gui.edit.ProtectionEditAddMenu;
import io.github.alathra.boltux.gui.edit.ProtectionEditListMenu;
import io.github.alathra.boltux.gui.edit.ProtectionEditMenu;
import io.github.alathra.boltux.gui.transfer.TransferMenu;
import io.github.alathra.boltux.gui.trust.TrustAddMenu;
import io.github.alathra.boltux.gui.trust.TrustListMenu;
import io.github.alathra.boltux.gui.trust.TrustMenu;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.popcraft.bolt.protection.Protection;

public class GuiHandler {
    public static void generateMainMenu(Player player, Protection protection, Location protectionLocation) {
        Gui mainMenu = MainMenu.generateBase();
        MainMenu.generateButtons(mainMenu, player, protection, protectionLocation);
        mainMenu.open(player);
    }

    public static void generateProtectionEditMenu(Player player, Protection protection, Location protectionLocation) {
        Gui protectionEditMenu = ProtectionEditMenu.generateBase();
        ProtectionEditMenu.generateButtons(protectionEditMenu, player, protection, protectionLocation);
        protectionEditMenu.open(player);
    }

    public static void generateProtectionEditListMenu(Player player, Protection protection, Location protectionLocation) {
        PaginatedGui protectionEditListMenu = ProtectionEditListMenu.generateBase(player, protection, protectionLocation);
        protectionEditListMenu.open(player);
    }

    public static void generateProtectionEditAddMenu(Player player, Protection protection, Location protectionLocation) {
        PaginatedGui protectionEditAddMenu = ProtectionEditAddMenu.generateBase(player, protection, protectionLocation);
        protectionEditAddMenu.open(player);
    }

    public static void generateTransferMenu(Player player, Protection protection, Location protectionLocation) {
        PaginatedGui transferMenu = TransferMenu.generateBase(player, protection, protectionLocation);
        transferMenu.open(player);
    }

    public static void generateTrustMenu(Player player, Protection protection, Location protectionLocation) {
        Gui trustMenu = TrustMenu.generateBase();
        TrustMenu.generateButtons(trustMenu, player, protection, protectionLocation);
        trustMenu.open(player);
    }

    public static void generateTrustListMenu(Player player, Protection protection, Location protectionLocation) {
        PaginatedGui trustListMenu = TrustListMenu.generateBase(player, protection, protectionLocation);
        trustListMenu.open(player);
    }

    public static void generateTrustAddMenu(Player player, Protection protection, Location protectionLocation) {
        PaginatedGui trustAddMenu = TrustAddMenu.generateBase(player, protection, protectionLocation);
        trustAddMenu.open(player);
    }
}

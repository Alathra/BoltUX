package io.github.alathra.boltux.gui;

import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.PaginatedGui;
import io.github.alathra.boltux.gui.edit.ProtectionAccessAddMenu;
import io.github.alathra.boltux.gui.edit.ProtectionAccessListMenu;
import io.github.alathra.boltux.gui.edit.ProtectionAccessMenu;
import io.github.alathra.boltux.gui.transfer.TransferMenu;
import io.github.alathra.boltux.gui.trust.TrustAddMenu;
import io.github.alathra.boltux.gui.trust.TrustListMenu;
import io.github.alathra.boltux.gui.trust.TrustMenu;
import io.github.alathra.boltux.utility.BoltUtil;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.popcraft.bolt.protection.Protection;

public final class GuiHandler {
    public static void generateMainMenu(Player player, Protection protection, Location protectionLocation) {
        final Gui mainMenu = MainMenu.generateBase();
        MainMenu.generateButtons(mainMenu, player, protection, protectionLocation);
        mainMenu.open(player);
        BoltUtil.getGroupAccessSet(protection);
    }

    public static void generateProtectionAccessMenu(Player player, Protection protection, Location protectionLocation) {
        final Gui protectionAccessMenu = ProtectionAccessMenu.generateBase();
        ProtectionAccessMenu.generateButtons(protectionAccessMenu, player, protection, protectionLocation);
        protectionAccessMenu.open(player);
    }

    public static void generateProtectionAccessListMenu(Player player, Protection protection, Location protectionLocation) {
        final PaginatedGui protectionAccessListMenu = ProtectionAccessListMenu.generateBase(player, protection, protectionLocation);
        ProtectionAccessListMenu.populateContent(protectionAccessListMenu, protection);
        protectionAccessListMenu.open(player);
    }

    public static void generateProtectionAccessAddMenu(Player player, Protection protection, Location protectionLocation) {
        final PaginatedGui protectionAccessAddMenu = ProtectionAccessAddMenu.generateBase(player, protection, protectionLocation);
        ProtectionAccessAddMenu.populateContent(protectionAccessAddMenu, player, protection);
        protectionAccessAddMenu.open(player);
    }

    public static void generateTransferMenu(Player player, Protection protection, Location protectionLocation) {
        final PaginatedGui transferMenu = TransferMenu.generateBase(player, protection, protectionLocation);
        TransferMenu.populateContent(transferMenu, player, protection);
        transferMenu.open(player);
    }

    public static void generateTrustMenu(Player player, Protection protection, Location protectionLocation) {
        final Gui trustMenu = TrustMenu.generateBase();
        TrustMenu.generateButtons(trustMenu, player, protection, protectionLocation);
        trustMenu.open(player);
    }

    public static void generateTrustListMenu(Player player, Protection protection, Location protectionLocation) {
        final PaginatedGui trustListMenu = TrustListMenu.generateBase(player, protection, protectionLocation);
        TrustListMenu.populateContent(trustListMenu, protection, player);
        trustListMenu.open(player);
    }

    public static void generateTrustAddMenu(Player player, Protection protection, Location protectionLocation) {
        final PaginatedGui trustAddMenu = TrustAddMenu.generateBase(player, protection, protectionLocation);
        TrustAddMenu.populateContent(trustAddMenu, protection, player);
        trustAddMenu.open(player);
    }
}

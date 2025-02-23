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

public class GuiHandler {
    public static void generateMainMenu(Player player, Protection protection, Location protectionLocation) {
        Gui mainMenu = MainMenu.generateBase();
        MainMenu.generateButtons(mainMenu, player, protection, protectionLocation);
        mainMenu.open(player);
        BoltUtil.getGroupAccessSet(protection);
    }

    public static void generateProtectionAccessMenu(Player player, Protection protection, Location protectionLocation) {
        Gui protectionAccessMenu = ProtectionAccessMenu.generateBase();
        ProtectionAccessMenu.generateButtons(protectionAccessMenu, player, protection, protectionLocation);
        protectionAccessMenu.open(player);
    }

    public static void generateProtectionAccessListMenu(Player player, Protection protection, Location protectionLocation) {
        PaginatedGui protectionAccessListMenu = ProtectionAccessListMenu.generateBase(player, protection, protectionLocation);
        ProtectionAccessListMenu.populateContent(protectionAccessListMenu, protection);
        protectionAccessListMenu.open(player);
    }

    public static void generateProtectionAccessAddMenu(Player player, Protection protection, Location protectionLocation) {
        PaginatedGui protectionAccessAddMenu = ProtectionAccessAddMenu.generateBase(player, protection, protectionLocation);
        ProtectionAccessAddMenu.populateContent(protectionAccessAddMenu, player, protection);
        protectionAccessAddMenu.open(player);
    }

    public static void generateTransferMenu(Player player, Protection protection, Location protectionLocation) {
        PaginatedGui transferMenu = TransferMenu.generateBase(player, protection, protectionLocation);
        TransferMenu.populateContent(transferMenu, player, protection);
        transferMenu.open(player);
    }

    public static void generateTrustMenu(Player player, Protection protection, Location protectionLocation) {
        Gui trustMenu = TrustMenu.generateBase();
        TrustMenu.generateButtons(trustMenu, player, protection, protectionLocation);
        trustMenu.open(player);
    }

    public static void generateTrustListMenu(Player player, Protection protection, Location protectionLocation) {
        PaginatedGui trustListMenu = TrustListMenu.generateBase(player, protection, protectionLocation);
        TrustListMenu.populateContent(trustListMenu, player);
        trustListMenu.open(player);
    }

    public static void generateTrustAddMenu(Player player, Protection protection, Location protectionLocation) {
        PaginatedGui trustAddMenu = TrustAddMenu.generateBase(player, protection, protectionLocation);
        TrustAddMenu.populateContent(trustAddMenu, player);
        trustAddMenu.open(player);
    }

}

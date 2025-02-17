package io.github.alathra.boltux.gui;

import dev.triumphteam.gui.guis.Gui;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.popcraft.bolt.protection.Protection;

public class GuiHandler {
    public static void generateProtectionOwnerGUI(Player player, Protection protection, Location protectionLocation) {
        Gui protectionOwnerGui = ProtectionOwnerGUI.generateBase(player, protection);
        ProtectionOwnerGUI.generateButtons(protectionOwnerGui, player, protection, protectionLocation);
        protectionOwnerGui.open(player);
    }
}

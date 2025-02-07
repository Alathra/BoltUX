package io.github.alathra.boltux.gui;

import com.github.milkdrinkers.colorparser.ColorParser;
import dev.triumphteam.gui.guis.Gui;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;

public class ProtectionOwnerGUI {
    public static Gui generateBase(OfflinePlayer player, Block block) {
        Gui base;
        base = Gui.gui()
            .title(ColorParser.of(player.getName() + "'s Protected " + block.getType().name()).build())
            .rows(1)
            .disableItemPlace()
            .disableItemSwap()
            .disableItemDrop()
            .disableItemTake()
            .create();
        return base;
    }
}

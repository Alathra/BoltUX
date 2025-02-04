package io.github.alathra.boltux.config;

import io.github.alathra.boltux.BoltUX;
import io.github.alathra.boltux.core.ItemPlugin;
import io.github.alathra.boltux.utility.Logger;

public class Settings {
    public static BoltUX plugin;

    public static void init(BoltUX plugin) {
        Settings.plugin = plugin;
    }

    public static ItemPlugin getItemPlugin() {
        // Default to empty String, no plugin
        String itemPluginString = plugin.getConfigHandler().getConfig().getOrDefault("LockItem.itemPlugin", "");
        if (itemPluginString.isEmpty() || itemPluginString.equalsIgnoreCase("None")) {
            return ItemPlugin.NONE;
        } else if (itemPluginString.equalsIgnoreCase("ItemsAdder")) {
            return ItemPlugin.ITEMSADDER;
        } else if (itemPluginString.equalsIgnoreCase("Nexo")) {
            return ItemPlugin.NEXO;
        } else if (itemPluginString.equalsIgnoreCase("Oraxen")) {
            return ItemPlugin.ORAXEN;
        } else {
            Logger.get().warn("Invalid 'ItemPlugin' defined in config.yml. Defaulting to none...");
            return ItemPlugin.NONE;
        }
    }

    public static String getCustomLockItemID() {
        return plugin.getConfigHandler().getConfig().getOrDefault("LockItem.customLockItemID", "");
    }
}

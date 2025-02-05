package io.github.alathra.boltux.config;

import com.github.milkdrinkers.colorparser.ColorParser;
import io.github.alathra.boltux.BoltUX;
import io.github.alathra.boltux.core.ItemPlugin;
import io.github.alathra.boltux.utility.Logger;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;

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

    public static Material getDefaultLockItemMaterial() {
        String materialString = plugin.getConfigHandler().getConfig().getOrDefault("LockItem.DefaultLockItem.material", "IRON_INGOT");
        try {
            return Material.valueOf(materialString);
        } catch (IllegalArgumentException e) {
            return Material.IRON_INGOT;
        }
    }

    public static int getDefaultLockItemCustomModelData() {
        return plugin.getConfigHandler().getConfig().getOrDefault("LockItem.DefaultLockItem.customModelData", 8792);
    }

    public static Component getDefaultLockItemDisplayName() {
        return ColorParser.of(
            plugin.getConfigHandler().getConfig().getOrDefault("LockItem.DefaultLockItem.displayName", "<gray>Iron Lock</gray>")
        ).build().decoration(TextDecoration.ITALIC, false);
    }

    public static List<Component> getDefaultLockItemLore() {
        List<String> loreStrings = plugin.getConfigHandler().getConfig().getStringList("LockItem.DefaultLockItem.lore");
        List<Component> loreComponents = new ArrayList<>();
        if (loreStrings == null || loreStrings.isEmpty()) {
            return List.of(Component.empty());
        } else {
            for (String line : loreStrings) {
                loreComponents.add(ColorParser.of(line).build().decoration(TextDecoration.ITALIC, false));
            }
        }
        return loreComponents;
    }
}

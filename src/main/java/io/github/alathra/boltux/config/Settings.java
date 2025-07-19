package io.github.alathra.boltux.config;

import com.github.milkdrinkers.colorparser.ColorParser;
import io.github.alathra.boltux.BoltUX;
import io.github.alathra.boltux.data.ItemPlugin;
import io.github.alathra.boltux.utility.Cfg;
import io.github.alathra.boltux.utility.Logger;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.intellij.lang.annotations.Subst;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Settings {
    public static BoltUX plugin;

    public static void init(BoltUX plugin) {
        Settings.plugin = plugin;
    }

    /**
     * @return The time in seconds a locked block/entity glows for when interact is denied
     */
    public static int getGlowBlockTime() {
        return Cfg.get().getOrDefault("GeneralSettings.blockGlowTime", 5);
    }

    public static int getNearbyPlayersRange() {
        return Cfg.get().getOrDefault("GuiSettings.nearbyPlayersRange", 100);
    }

    public static boolean isLockItemEnabled() {
        return Cfg.get().getOrDefault("LockItem.enabled", false);
    }

    public static boolean isDefaultLockCraftingRecipeEnabled() {
        return Cfg.get().getOrDefault("LockItem.enableCraftingRecipe", false);
    }

    public static boolean isLockDroppingEnabled() {
        return Cfg.get().getOrDefault("LockItem.protectionsDropLocks", false);
    }

    public static boolean isLockingSoundEnabled() {
        return Cfg.get().getOrDefault("LockItem.sound.enabled", false);
    }

    public static Sound getLockingSound() {
        @Subst("minecraft:entity.zombie.attack_iron_door") String soundID = Cfg.get().getOrDefault("LockItem.sound.effect", "minecraft:entity.zombie.attack_iron_door");
        float volume = Cfg.get().getOrDefault("LockItem.sound.volume", 1.0).floatValue();
        float pitch = Cfg.get().getOrDefault("LockItem.sound.pitch", 1.0).floatValue();

        return Sound.sound()
            .type(Key.key(soundID))
            .source(Sound.Source.BLOCK)
            .volume(volume)
            .pitch(pitch)
            .build();
    }

    public static List<World> getLockItemEnabledWorlds() {
        return Cfg.get().getStringList("LockItem.enabledWorlds")
            .stream()
            .map(Bukkit::getWorld)
            .collect(Collectors.toList());
    }

    public static ItemPlugin getItemPlugin() {
        // Default to empty String, no plugin
        String itemPluginString = Cfg.get().getOrDefault("LockItem.itemPlugin", "");
        if (itemPluginString.isEmpty() || itemPluginString.equalsIgnoreCase("None")) {
            return ItemPlugin.NONE;
        } else if (itemPluginString.equalsIgnoreCase("ItemsAdder")) {
            return ItemPlugin.ITEMSADDER;
        } else if (itemPluginString.equalsIgnoreCase("MMOItems")) {
            return ItemPlugin.MMOITEMS;
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
        return Cfg.get().getOrDefault("LockItem.customLockItemID", "");
    }

    public static Material getDefaultLockItemMaterial() {
        String materialString = Cfg.get().getOrDefault("LockItem.DefaultLockItem.material", "IRON_INGOT");
        try {
            return Material.valueOf(materialString);
        } catch (IllegalArgumentException e) {
            return Material.IRON_INGOT;
        }
    }

    public static int getDefaultLockItemCustomModelData() {
        return Cfg.get().getOrDefault("LockItem.DefaultLockItem.customModelData", 8792);
    }

    public static Component getDefaultLockItemDisplayName() {
        return ColorParser.of(
            Cfg.get().getOrDefault("LockItem.DefaultLockItem.displayName", "<gray>Iron Lock</gray>")
        ).build().decoration(TextDecoration.ITALIC, false);
    }

    public static List<Component> getDefaultLockItemLore() {
        List<String> loreStrings = Cfg.get().getStringList("LockItem.DefaultLockItem.lore");
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

    public static boolean isLockingDisabledInOtherTowns() {
        return Cfg.get().getOrDefault("TownyCompatibility.disableLockingInOtherTowns", true);
    }

    public static boolean canMayorsAccessProtections() {
        return Cfg.get().getOrDefault("TownyCompatibility.allowMayorsToAccessLocked", true);
    }

}

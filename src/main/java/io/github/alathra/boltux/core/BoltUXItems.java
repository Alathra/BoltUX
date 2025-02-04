package io.github.alathra.boltux.core;

import com.github.milkdrinkers.colorparser.ColorParser;
import io.github.alathra.boltux.BoltUX;
import io.github.alathra.boltux.config.Settings;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class BoltUXItems {

    /**
     * Get the lock item
     * @return The lock item from the specified item plugin or the default (native) lock item
     */
    public static ItemStack getLockItem() {
        switch (Settings.getItemPlugin()) {
            case ITEMSADDER -> {
                if (BoltUX.getItemsAdderHook().isHookLoaded()) {
                    return BoltUX.getItemsAdderHook().getLockItem();
                }
                return getDefaultLockItem();
            }
            case NEXO -> {
                if (BoltUX.getNexoHook().isHookLoaded()) {
                    return BoltUX.getNexoHook().getLockItem();
                }
                return getDefaultLockItem();
            }
            case ORAXEN -> {
                if (BoltUX.getOraxenHook().isHookLoaded()) {
                    return BoltUX.getOraxenHook().getLockItem();
                }
                return getDefaultLockItem();
            }
            default -> {return getDefaultLockItem();}
        }
    }

    /**
     * Get the default lock item
     * @return The default (native) lock item if no item plugin is specified
     */
    private static ItemStack getDefaultLockItem() {
        ItemStack lockItem = new ItemStack(Material.IRON_INGOT);
        ItemMeta lockMeta = lockItem.getItemMeta();
        lockMeta.displayName(ColorParser.of("<gray>Iron Lock").build());
        lockMeta.lore(List.of(
            ColorParser.of("<yellow>Shift-Right Click on a container or door to use").build().decoration(TextDecoration.ITALIC, false)
        ));
        lockItem.setItemMeta(lockMeta);
        return lockItem;
    }
}

package io.github.alathra.boltux.api;

import io.github.alathra.boltux.BoltUX;
import io.github.alathra.boltux.config.Settings;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class BoltUXAPI {

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
     * Get if the provided ItemStack is a lock
     * @return If the ItemStack consists of lock items
     */
    public static boolean isLockItem(ItemStack itemStack) {
        return itemStack.isSimilar(getLockItem());
    }

    /**
     * Get the default lock item
     * @return The default (native) lock item if no item plugin is specified
     */
    private static ItemStack getDefaultLockItem() {
        ItemStack lockItem = new ItemStack(Settings.getDefaultLockItemMaterial());
        ItemMeta lockMeta = lockItem.getItemMeta();
        lockMeta.displayName(Settings.getDefaultLockItemDisplayName());
        lockMeta.lore(Settings.getDefaultLockItemLore());
        lockMeta.setCustomModelData(Settings.getDefaultLockItemCustomModelData());
        lockItem.setItemMeta(lockMeta);
        return lockItem;
    }
}

package io.github.alathra.boltux.api;

import io.github.alathra.boltux.BoltUX;
import io.github.alathra.boltux.config.Settings;
import io.github.alathra.boltux.hook.Hook;
import io.github.alathra.boltux.utility.Logger;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class BoltUXAPI {

    /**
     * Get the lock item
     * @param amount The amount of lock items in the resulting itemstack
     * @return The lock item from the specified item plugin or the default (native) lock item
     */
    public static ItemStack getLockItem(int amount) {
        ItemStack itemStack;
        switch (Settings.getItemPlugin()) {
            case ITEMSADDER -> {
                if (Hook.ItemsAdder.isLoaded()) {
                    itemStack = Hook.getItemsAdderHook().getLockItem();
                } else {
                    itemStack = getDefaultLockItem();
                }
            }
            case MMOITEMS -> {
                if (Hook.MMOItems.isLoaded()) {
                    itemStack = Hook.getMMOItemsHook().getLockItem();
                } else {
                    itemStack = getDefaultLockItem();
                }
            }
            case NEXO -> {
                if (Hook.Nexo.isLoaded()) {
                    itemStack = Hook.getNexoHook().getLockItem();
                } else {
                    itemStack = getDefaultLockItem();
                }
            }
            case ORAXEN -> {
                if (Hook.Oraxen.isLoaded()) {
                    itemStack = Hook.getOraxenHook().getLockItem();
                } else {
                    itemStack = getDefaultLockItem();
                }
            }
            default -> {itemStack = getDefaultLockItem();}
        }
        if (itemStack == null) {
            Logger.get().error("Lock item failed to load! If you are using an item plugin make sure the ID is correct");
            Logger.get().warn("Loading default lock item...");
            return getLockItem();
        }
        itemStack.setAmount(amount);
        return itemStack;
    }

    /**
     * Get the lock item
     * @return The lock item from the specified item plugin or the default (native) lock item
     */
    public static ItemStack getLockItem() {
        return getLockItem(1);
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

package io.github.alathra.boltux.hook.oraxen;

import io.github.alathra.boltux.BoltUX;
import io.github.alathra.boltux.config.Settings;
import io.github.alathra.boltux.hook.AbstractHook;
import io.github.alathra.boltux.hook.Hook;
import io.th0rgal.oraxen.api.OraxenItems;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

public class OraxenHook extends AbstractHook {

    public OraxenHook(BoltUX plugin) {
        super(plugin);
    }

    @Override
    public boolean isHookLoaded() {
        return isPluginEnabled(Hook.Oraxen.getPluginName());
    }

    @Override
    public void onLoad(BoltUX plugin) {
        if (!isHookLoaded()) return;
    }

    @Override
    public void onEnable(BoltUX plugin) {
        if (!isHookLoaded()) return;
    }

    @Override
    public void onDisable(BoltUX plugin) {
        if (!isHookLoaded()) return;
    }

    public @Nullable ItemStack getLockItem() {
        if (OraxenItems.exists(Settings.getCustomLockItemID())) {
            return (OraxenItems.getItemById(Settings.getCustomLockItemID()).build());
        }
        return null;
    }
}

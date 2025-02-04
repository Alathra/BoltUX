package io.github.alathra.boltux.hook;

import io.github.alathra.boltux.BoltUX;
import io.github.alathra.boltux.config.Settings;
import io.th0rgal.oraxen.api.OraxenItems;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

public class OraxenHook implements Hook {
    private final BoltUX plugin;

    public OraxenHook(BoltUX plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean isHookLoaded() {
        return plugin.getServer().getPluginManager().isPluginEnabled("Oraxen");
    }

    @Override
    public void onLoad() {

    }

    @Override
    public void onEnable() {

    }

    @Override
    public void onDisable() {

    }

    public @Nullable ItemStack getLockItem() {
        if (OraxenItems.exists(Settings.getCustomLockItemID())) {
            return (OraxenItems.getItemById(Settings.getCustomLockItemID()).build());
        }
        return null;
    }
}

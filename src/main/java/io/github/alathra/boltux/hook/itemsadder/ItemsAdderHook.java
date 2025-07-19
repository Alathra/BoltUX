package io.github.alathra.boltux.hook.itemsadder;

import dev.lone.itemsadder.api.CustomStack;
import io.github.alathra.boltux.BoltUX;
import io.github.alathra.boltux.config.Settings;
import io.github.alathra.boltux.hook.AbstractHook;
import io.github.alathra.boltux.hook.Hook;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

public class ItemsAdderHook extends AbstractHook {

    public ItemsAdderHook(BoltUX plugin) {
        super(plugin);
    }

    @Override
    public boolean isHookLoaded() {
        return isPluginEnabled(Hook.ItemsAdder.getPluginName());
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
        CustomStack customStack = CustomStack.getInstance(Settings.getCustomLockItemID());
        if (customStack == null) {
            return null;
        }
        return customStack.getItemStack();
    }
}

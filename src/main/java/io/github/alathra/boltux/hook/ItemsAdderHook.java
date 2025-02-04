package io.github.alathra.boltux.hook;

import dev.lone.itemsadder.api.CustomStack;
import io.github.alathra.boltux.BoltUX;
import io.github.alathra.boltux.config.Settings;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

public class ItemsAdderHook implements Hook {
    private final BoltUX plugin;

    public ItemsAdderHook(BoltUX plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean isHookLoaded() {
        return plugin.getServer().getPluginManager().isPluginEnabled("ItemsAdder");
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
        CustomStack customStack = CustomStack.getInstance(Settings.getCustomLockItemID());
        if (customStack == null) {
            return null;
        }
        return customStack.getItemStack();
    }
}

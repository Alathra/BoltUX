package io.github.alathra.boltux.hook.mmoitems;

import io.github.alathra.boltux.BoltUX;
import io.github.alathra.boltux.config.Settings;
import io.github.alathra.boltux.hook.AbstractHook;
import io.github.alathra.boltux.hook.Hook;
import net.Indyuce.mmoitems.MMOItems;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

public class MMOItemsHook extends AbstractHook {

    public MMOItemsHook(BoltUX plugin) {
        super(plugin);
    }

    @Override
    public boolean isHookLoaded() {
        return isPluginEnabled(Hook.MMOItems.getPluginName());
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
        if (!Settings.getCustomLockItemID().contains(".")) {
            return null;
        }
        String[] inputs = Settings.getCustomLockItemID().split("\\.", 2); // split result into TYPE.ID
        return MMOItems.plugin.getItem(inputs[0], inputs[1]);
    }
}

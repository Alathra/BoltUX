package io.github.alathra.boltux.hook.nexo;

import com.nexomc.nexo.api.NexoItems;
import io.github.alathra.boltux.BoltUX;
import io.github.alathra.boltux.config.Settings;
import io.github.alathra.boltux.hook.AbstractHook;
import io.github.alathra.boltux.hook.Hook;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class NexoHook extends AbstractHook {

    public NexoHook(BoltUX plugin) {
        super(plugin);
    }

    @Override
    public boolean isHookLoaded() {
        return isPluginEnabled(Hook.Nexo.getPluginName());
    }

    @Override
    public void onLoad(BoltUX plugin) {
        if (!isHookLoaded()) {
        }
    }

    @Override
    public void onEnable(BoltUX plugin) {
        if (!isHookLoaded()) {
        }
    }

    @Override
    public void onDisable(BoltUX plugin) {
        if (!isHookLoaded()) {
        }
    }

    public @Nullable ItemStack getLockItem() {
        return Objects.requireNonNull(NexoItems.itemFromId(Settings.getCustomLockItemID())).build();
    }
}

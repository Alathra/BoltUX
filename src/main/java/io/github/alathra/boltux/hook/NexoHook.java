package io.github.alathra.boltux.hook;

import com.nexomc.nexo.api.NexoItems;
import io.github.alathra.boltux.BoltUX;
import io.github.alathra.boltux.config.Settings;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class NexoHook implements Hook {
    private final BoltUX plugin;

    public NexoHook(BoltUX plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean isHookLoaded() {
        return plugin.getServer().getPluginManager().isPluginEnabled("Nexo");
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
        try {
            return Objects.requireNonNull(NexoItems.itemFromId(Settings.getCustomLockItemID())).build();
        } catch (NullPointerException e) {
            return null;
        }
    }
}

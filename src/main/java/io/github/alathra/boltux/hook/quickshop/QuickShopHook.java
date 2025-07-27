package io.github.alathra.boltux.hook.quickshop;

import com.ghostchu.quickshop.QuickShop;
import com.ghostchu.quickshop.api.QuickShopAPI;
import io.github.alathra.boltux.BoltUX;
import io.github.alathra.boltux.hook.AbstractHook;
import io.github.alathra.boltux.hook.Hook;
import org.bukkit.Location;

public class QuickShopHook extends AbstractHook {

    public QuickShopHook(BoltUX plugin) {
        super(plugin);
    }

    @Override
    public boolean isHookLoaded() {
        return isPluginEnabled(Hook.QuickShop.getPluginName());
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

    public boolean isQuickShop(Location location) {
        return QuickShopAPI.getInstance().getShopManager().getShop(location) != null;
    }

}

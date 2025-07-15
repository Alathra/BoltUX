package io.github.alathra.boltux.hook;

import com.nexomc.nexo.api.NexoItems;
import io.github.alathra.boltux.BoltUX;
import io.github.alathra.boltux.config.Settings;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.Type;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

public class MMOItemsHook implements Hook {

    private BoltUX plugin;

    public MMOItemsHook(BoltUX plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onLoad() {
        if (!isHookLoaded())
            return;
    }

    @Override
    public void onEnable() {
        if (!isHookLoaded())
            return;
    }

    @Override
    public void onDisable() {
        if (!isHookLoaded())
            return;
    }

    @Override
    public boolean isHookLoaded() {
        return Bukkit.getPluginManager().isPluginEnabled("MMOItems");
    }

    public @Nullable ItemStack getLockItem() {
        if (!Settings.getCustomLockItemID().contains(".")) {
            return null;
        }
        String[] inputs = Settings.getCustomLockItemID().split("\\.", 2); // split result into TYPE.ID
        return MMOItems.plugin.getItem(inputs[0], inputs[1]);
    }
}

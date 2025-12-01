package io.github.alathra.boltux.lock;

import io.github.alathra.boltux.BoltUX;
import io.github.alathra.boltux.Reloadable;
import io.github.alathra.boltux.api.BoltUXAPI;
import io.github.alathra.boltux.config.Settings;
import io.github.alathra.boltux.data.ItemPlugin;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;

public final class CraftingHandler implements Reloadable {
    private final BoltUX plugin;

    public CraftingHandler(BoltUX plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onEnable(BoltUX plugin) {
        if (Settings.isDefaultLockCraftingRecipeEnabled() && Settings.isLockItemEnabled() && Settings.getItemPlugin().equals(ItemPlugin.NONE)) {
            if (plugin.getServer().getRecipe(new NamespacedKey(BoltUX.getInstance(), "lock")) == null) {
                plugin.getServer().addRecipe(getLockRecipe());
            }
        }
    }

    public static Recipe getLockRecipe() {
        final NamespacedKey key = new NamespacedKey(BoltUX.getInstance(), "lock");
        final ShapedRecipe lockRecipe = new ShapedRecipe(key, BoltUXAPI.getLockItem());
        lockRecipe.shape(" % ", "@ @", "@@@");
        lockRecipe.setIngredient('@', Material.IRON_INGOT);
        lockRecipe.setIngredient('%', Material.IRON_NUGGET);
        return lockRecipe;
    }
}

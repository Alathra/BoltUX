package io.github.alathra.boltux.crafting;

import io.github.alathra.boltux.BoltUX;
import io.github.alathra.boltux.api.BoltUXAPI;
import io.github.alathra.boltux.config.Settings;
import io.github.alathra.boltux.utility.Reloadable;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;

public class CraftingHandler implements Reloadable {

    private final BoltUX plugin;

    public CraftingHandler (BoltUX plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onLoad() {
    }

    @Override
    public void onEnable() {
        if (Settings.isDefaultLockCraftingRecipeEnabled()) {
            loadLockRecipe();
        }
    }

    @Override
    public void onDisable() {
    }

    public static Recipe getLockRecipe() {
        NamespacedKey key = new NamespacedKey(BoltUX.getInstance(), "lock");
        ShapedRecipe lockRecipe = new ShapedRecipe(key, BoltUXAPI.getLockItem());
        lockRecipe.shape(" % ", "@ @", "@@@");
        lockRecipe.setIngredient('@', Material.IRON_INGOT);
        lockRecipe.setIngredient('%', Material.IRON_NUGGET);
        return lockRecipe;
    }

    public void loadLockRecipe() {
        plugin.getServer().addRecipe(getLockRecipe());
    }
}

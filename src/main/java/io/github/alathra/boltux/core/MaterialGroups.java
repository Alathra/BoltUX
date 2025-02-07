package io.github.alathra.boltux.core;

import com.destroystokyo.paper.MaterialTags;
import org.bukkit.Material;

import java.util.HashSet;
import java.util.Set;

public class MaterialGroups {

    public static Set<Material> inventoryBlocks = new HashSet<>();
    public static Set<Material> interactableBlocks = new HashSet<Material>();

    static {
        inventoryBlocks.addAll(
            Set.of(
                Material.ANVIL,
                Material.BLAST_FURNACE,
                Material.BARREL,
                Material.BEACON,
                Material.BREWING_STAND,
                Material.CHEST,
                Material.CRAFTER,
                Material.CHIPPED_ANVIL,
                Material.DAMAGED_ANVIL,
                Material.DISPENSER,
                Material.DROPPER,
                // Enchanting table can't be locked with Bolt as of now
                //Material.ENCHANTING_TABLE,
                Material.ENDER_CHEST,
                Material.FURNACE,
                Material.HOPPER,
                Material.TRAPPED_CHEST)
        );
        inventoryBlocks.addAll(MaterialTags.SHULKER_BOXES.getValues());

        interactableBlocks.addAll(MaterialTags.DOORS.getValues());
        interactableBlocks.addAll(MaterialTags.FENCE_GATES.getValues());
        interactableBlocks.addAll(MaterialTags.TRAPDOORS.getValues());
        interactableBlocks.addAll(
            Set.of(
                Material.CAULDRON,
                Material.CHISELED_BOOKSHELF,
                Material.COMPOSTER,
                Material.DECORATED_POT,
                Material.JUKEBOX,
                Material.LECTERN,
                Material.NOTE_BLOCK
            )
        );
    }

}

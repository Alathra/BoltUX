package io.github.alathra.boltux.data;

import com.destroystokyo.paper.MaterialTags;
import org.bukkit.Material;

import java.util.HashSet;
import java.util.Set;

public class MaterialGroups {

    public static Set<Material> containerBlocks = new HashSet<>();
    public static Set<Material> interactableBlocks = new HashSet<>();
    public static Set<Material> otherBlocks = new HashSet<>();

    static {
        containerBlocks.addAll(Set.of(
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
            Material.TRAPPED_CHEST
        ));
        containerBlocks.addAll(MaterialTags.SHULKER_BOXES.getValues());

        interactableBlocks.addAll(MaterialTags.DOORS.getValues());
        interactableBlocks.addAll(MaterialTags.FENCE_GATES.getValues());
        interactableBlocks.addAll(MaterialTags.TRAPDOORS.getValues());
        interactableBlocks.addAll(MaterialTags.SIGNS.getValues());
        interactableBlocks.addAll(Set.of(
            Material.CAULDRON,
            Material.CHISELED_BOOKSHELF,
            Material.COMPOSTER,
            Material.DECORATED_POT,
            Material.JUKEBOX,
            Material.LECTERN,
            Material.NOTE_BLOCK
        ));

        otherBlocks.addAll(Set.of(
            Material.BLACK_BANNER,
            Material.BLACK_WALL_BANNER,
            Material.BLUE_BANNER,
            Material.BLUE_WALL_BANNER,
            Material.BROWN_BANNER,
            Material.BROWN_WALL_BANNER,
            Material.CYAN_BANNER,
            Material.CYAN_WALL_BANNER,
            Material.GRAY_BANNER,
            Material.GRAY_WALL_BANNER,
            Material.GREEN_BANNER,
            Material.GREEN_WALL_BANNER,
            Material.LIGHT_BLUE_BANNER,
            Material.LIGHT_BLUE_WALL_BANNER,
            Material.LIGHT_GRAY_BANNER,
            Material.LIGHT_GRAY_WALL_BANNER,
            Material.LIME_BANNER,
            Material.LIME_WALL_BANNER,
            Material.MAGENTA_BANNER,
            Material.MAGENTA_WALL_BANNER,
            Material.ORANGE_BANNER,
            Material.ORANGE_WALL_BANNER,
            Material.PINK_BANNER,
            Material.PINK_WALL_BANNER,
            Material.PURPLE_BANNER,
            Material.PURPLE_WALL_BANNER,
            Material.RED_BANNER,
            Material.RED_WALL_BANNER,
            Material.WHITE_BANNER,
            Material.WHITE_WALL_BANNER,
            Material.YELLOW_BANNER,
            Material.YELLOW_WALL_BANNER
        ));
    }

}

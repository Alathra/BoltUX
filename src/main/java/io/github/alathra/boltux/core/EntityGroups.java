package io.github.alathra.boltux.core;

import org.bukkit.entity.EntityType;

import java.util.HashSet;
import java.util.Set;

public class EntityGroups {

    public static Set<EntityType> chestBoats = new HashSet<>();

    public static Set<EntityType> containerMinecarts = new HashSet<>();

    public static Set<EntityType> otherInteractableEntities = new HashSet<>();

    public static Set<EntityType> otherEntities = new HashSet<>();

    static {
        chestBoats.addAll(Set.of(
            EntityType.ACACIA_CHEST_BOAT,
            EntityType.BAMBOO_CHEST_RAFT,
            EntityType.BIRCH_CHEST_BOAT,
            EntityType.CHERRY_CHEST_BOAT,
            EntityType.DARK_OAK_CHEST_BOAT,
            EntityType.JUNGLE_CHEST_BOAT,
            EntityType.MANGROVE_CHEST_BOAT,
            EntityType.OAK_CHEST_BOAT,
            EntityType.PALE_OAK_CHEST_BOAT,
            EntityType.SPRUCE_CHEST_BOAT
        ));

        containerMinecarts.addAll(Set.of(
            EntityType.CHEST_MINECART,
            EntityType.FURNACE_MINECART,
            EntityType.HOPPER_MINECART
        ));

        otherInteractableEntities.addAll(Set.of(
            EntityType.ARMOR_STAND,
            EntityType.ITEM_FRAME,
            EntityType.GLOW_ITEM_FRAME,
            EntityType.LEASH_KNOT
        ));

        otherEntities.addAll(Set.of(
            EntityType.PAINTING
        ));
    }
}

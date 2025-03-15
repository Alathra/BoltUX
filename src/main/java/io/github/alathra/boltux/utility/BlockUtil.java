package io.github.alathra.boltux.utility;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.Nullable;

public class BlockUtil {

    // Helper function to get the block of the other chest in the double chest
    public static @Nullable Block getConnectedDoubleChest(Block chestBlock) {
        // Check neighboring blocks for another chest
        Block[] neighbors = {
            chestBlock.getRelative(1, 0, 0),  // Check to the east
            chestBlock.getRelative(-1, 0, 0), // Check to the west
            chestBlock.getRelative(0, 0, 1),  // Check to the south
            chestBlock.getRelative(0, 0, -1)  // Check to the north
        };

        for (Block neighbor : neighbors) {
            if (neighbor.getType().equals(Material.CHEST) || neighbor.getType().equals(Material.TRAPPED_CHEST)) {
                Chest neighborChest = (Chest) neighbor.getState();
                Inventory neighborInventory = neighborChest.getInventory();

                // If the neighboring chest has a double chest size, it's connected
                if (isDoubleChest(neighborInventory)) {
                    return neighbor;  // Return the connected chest block
                }
            }
        }
        return null;  // Return null if no connected chest was found
    }

    // Helper function to check if the chest is part of a double chest
    public static boolean isDoubleChest(Inventory inventory) {
        return inventory.getSize() == 54;  // Double chest size is 54 (27 slots each for two chests)
    }
}

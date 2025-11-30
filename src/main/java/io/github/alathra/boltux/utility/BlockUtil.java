package io.github.alathra.boltux.utility;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.inventory.DoubleChestInventory;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

@ApiStatus.Internal
public final class BlockUtil {
    /**
     * Get the block of the other chest in the double chest.
     *
     * @param chestBlock a block containing a chesty
     * @return the 2nd chest block or null
     */
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

    /**
     * Check if an inventory is part of a double chest.
     *
     * @param inventory inventory
     * @return true if a double chest
     */
    public static boolean isDoubleChest(Inventory inventory) {
        return inventory instanceof DoubleChestInventory || inventory.getSize() == 54;
    }

    /**
     * Normalize a chest block so both halves of a double chest map to the same "base" block.
     */
    public static Block normalizeChestBlock(Block block) {
        if (!(block.getType() == Material.CHEST || block.getType() == Material.TRAPPED_CHEST)) {
            return block;
        }

        final Chest chest = (Chest) block.getState();
        if (chest.getInventory() instanceof DoubleChestInventory dblInv) {
            Block left = ((Chest) dblInv.getLeftSide()).getBlock();
            Block right = ((Chest) dblInv.getRightSide()).getBlock();
            return getLowerCoordinateBlock(left, right);
        }

        return block;
    }

    /**
     * Returns the "lower" of two blocks by comparing coordinates in order: X, then Z, then Y.
     *
     * @param a a
     * @param b b
     * @return the smallest of two blocks
     */
    private static Block getLowerCoordinateBlock(Block a, Block b) {
        if (a.getX() != b.getX()) {
            return a.getX() < b.getX() ? a : b;
        }
        if (a.getZ() != b.getZ()) {
            return a.getZ() < b.getZ() ? a : b;
        }
        return a.getY() <= b.getY() ? a : b;
    }
}

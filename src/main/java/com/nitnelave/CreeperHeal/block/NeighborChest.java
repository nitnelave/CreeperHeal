package com.nitnelave.CreeperHeal.block;

import org.bukkit.block.Block;
import org.bukkit.block.BlockState;

/**
 * Represents the second part of a double chest, the first one being a
 * CreeperChest.
 *
 * @author nitnelave
 *
 */
public class NeighborChest
{

    /*
     * The chest itself.
     */
    private final BlockState chest;
    /*
     * Whether it is the right part of the double chest or the left one.
     */
    private final boolean right;

    /**
     * Constructor.
     *
     * @param chest
     *            The block where the chest is.
     * @param right
     *            Whether the block is the right part of the double chest.
     */
    public NeighborChest(Block chest, boolean right)
    {
        this(chest.getState(), right);
    }

    /**
     * Constructor.
     *
     * @param chest
     *            The blockState representing the chest.
     * @param right
     *            Whether the block is the right part of the double chest.
     */
    public NeighborChest(BlockState chest, boolean right)
    {
        this.chest = chest;
        this.right = right;
    }

    /**
     * Get whether the block is the right part of the double chest.
     *
     * @return Whether the block is the right part of the double chest.
     */
    public boolean isRight()
    {
        return right;
    }

    /**
     * Get the blockState representing the chest.
     *
     * @return The blockState representing the chest.
     */
    public BlockState getChest()
    {
        return chest;
    }

    /**
     * Gets the block where the chest is.
     *
     * @return The block.
     */
    public Block getBlock()
    {
        return chest.getBlock();
    }

    public void update(boolean b)
    {
        chest.update(true);
        chest.getBlock().setType(chest.getType());
    }
}

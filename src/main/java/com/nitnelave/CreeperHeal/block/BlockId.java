package com.nitnelave.CreeperHeal.block;

import org.bukkit.block.Block;
import org.bukkit.Material;

/**
 * A utility class to represent a block type along with optional data.
 *
 * @author nitnelave
 *
 */
public class BlockId
{
    private Material type;

    /**
     * Only id is specified, data comparison accepts any value.
     *
     * @param id
     *            The block id.
     */
    public BlockId(Material type)
    {
        this.type = type;
    }

    /**
     * Constructor from string. The string is parsed in an attempt to get the
     * info. The string format accepted is a simple number for just the id, or
     * id:data for both.
     *
     * @param str
     *            The string to be parsed.
     * @throws NumberFormatException
     *             If the string does not match any pattern.
     */
    public BlockId(String str) throws NumberFormatException
    {
        type = Material.valueOf(str.trim());
    }

    /**
     * Get the id and data from a block.
     *
     * @param block
     *            The block.
     */
    public BlockId(Block block)
    {
        this(block.getType());
    }

    public Material getType()
    {
        return type;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        return getType().toString();
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj)
    {
        if (obj == this)
            return true;

        if (!(obj instanceof BlockId))
            return false;

        BlockId block = (BlockId) obj;
        return block.type == type;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode()
    {
        return type.hashCode();
    }
}

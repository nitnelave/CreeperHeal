package com.nitnelave.CreeperHeal.block;

import org.bukkit.block.Block;

/**
 * A utility class to represent a block type along with optional data.
 * 
 * @author nitnelave
 * 
 */
public class BlockId
{
    private int id;
    private byte data;
    private boolean hasData;

    /**
     * Only id is specified, data comparison accepts any value.
     * 
     * @param id
     *            The block id.
     */
    public BlockId(int id)
    {
        this(id, (byte) -1);
    }

    /**
     * Id and data are specified. If data == -1, then it is considered as
     * unspecified.
     * 
     * @param id
     *            The block id.
     * @param data
     *            The block data.
     */
    public BlockId(int id, byte data)
    {
        this(id, data, data != -1);
    }

    /**
     * Id and data are specified, and hasData specifies if the data is used.
     * 
     * @param id
     *            The block id.
     * @param data
     *            The block data.
     * @param hasData
     *            Whether the data should be used in comparing.
     */
    public BlockId(int id, byte data, boolean hasData)
    {
        this.id = id;
        this.data = data;
        this.hasData = hasData && data != -1;
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
        String res = str.trim();
        try
        {
            id = Integer.parseInt(res);
            data = -1;
            hasData = false;
        } catch (NumberFormatException e)
        {
            String[] split = res.split(":");
            if (split.length == 2)
            {
                id = Integer.parseInt(split[0]);
                data = Byte.parseByte(split[1]);
                hasData = true;
            }
            else
                throw new NumberFormatException();
        }
    }

    /**
     * Get the id and data from a block.
     * 
     * @param block
     *            The block.
     */
    public BlockId(Block block)
    {
        this(block.getTypeId(), block.getData());
    }

    /**
     * Get the id stored.
     * 
     * @return The id stored.
     */
    public int getId()
    {
        return id;
    }

    /**
     * Get the block data. Check for hasData first.
     * 
     * @return The block data.
     */
    public byte getData()
    {
        return data;
    }

    /**
     * Whether the block's data is used in comparing.
     * 
     * @return Whether the block's data is used in comparing.
     */
    public boolean hasData()
    {
        return hasData;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        String str = String.valueOf(id);
        if (hasData)
            str += ":" + String.valueOf(data);
        return str;
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
        if (block.id != id)
            return false;
        //same id
        if (!(block.hasData && hasData))
            return true;
        //both have data
        return block.data == data;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode()
    {
        return id;
    }
}

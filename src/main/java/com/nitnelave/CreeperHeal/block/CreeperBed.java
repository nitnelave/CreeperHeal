package com.nitnelave.CreeperHeal.block;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;

/**
 * Bed implementation of CreeperBlock.
 * 
 * @author nitnelave
 * 
 */
class CreeperBed extends CreeperBlock
{

    /*
     * The direction the bed is facing.
     */
    private final BlockFace orientation;

    /*
     * Constructor.
     */
    protected CreeperBed(BlockState blockState)
    {
        orientation = getFacing(blockState.getRawData());
        Block block = blockState.getBlock();
        if ((blockState.getRawData() & 8) == 0)
            block = block.getRelative(orientation.getOppositeFace());
        this.blockState = block.getState();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.nitnelave.CreeperHeal.block.CreeperBlock#update()
     */
    @Override
    public void update()
    {
        super.update();
        byte data = (byte) (getRawData() & 3);
        BlockFace face = getFacing(data);
        getBlock().getRelative(face).setTypeIdAndData(getTypeId(), data, false);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.nitnelave.CreeperHeal.block.CreeperBlock#remove()
     */
    @Override
    public void remove()
    {
        getBlock().getRelative(orientation).setType(Material.AIR);
        getBlock().setType(Material.AIR);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.nitnelave.CreeperHeal.block.CreeperBlock#getNeighbors()
     */
    @Override
    public List<NeighborBlock> getDependentNeighbors()
    {
        return new ArrayList<NeighborBlock>();
    }

    private BlockFace getFacing(byte data)
    {
        switch (data & 3)
        {
        case 0:
            return BlockFace.NORTH;
        case 1:
            return BlockFace.EAST;
        case 2:
            return BlockFace.SOUTH;
        default:
            return BlockFace.WEST;
        }

    }

}

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
class CreeperBed extends CreeperBlock {

    /*
     * The direction the bed is facing.
     */
    private final BlockFace orientation;

    /*
     * Constructor.
     */
    protected CreeperBed (BlockState blockState) {
        byte faceData = (byte) (blockState.getRawData () & 3);
        switch (faceData)
        {
            case 0:
                orientation = BlockFace.NORTH;
                break;
            case 1:
                orientation = BlockFace.EAST;
                break;
            case 2:
                orientation = BlockFace.SOUTH;
                break;
            default:
                orientation = BlockFace.WEST;
        }
        Block block = blockState.getBlock ();
        if ((blockState.getRawData () & 8) == 0)
            block = block.getRelative (orientation.getOppositeFace ());
        this.blockState = block.getState ();
    }

    /*
     * (non-Javadoc)
     * @see com.nitnelave.CreeperHeal.block.CreeperBlock#update()
     */
    @Override
    public void update () {
        byte data = (byte) (getRawData () & 3);
        BlockFace face;
        if (data == 0)
            face = BlockFace.NORTH;
        else if (data == 1)
            face = BlockFace.EAST;
        else if (data == 2)
            face = BlockFace.SOUTH;
        else
            face = BlockFace.WEST;
        blockState.update (true);
        getBlock ().getRelative (face).setTypeIdAndData (getTypeId (), data, false);
    }

    /*
     * (non-Javadoc)
     * @see com.nitnelave.CreeperHeal.block.CreeperBlock#remove()
     */
    @Override
    public void remove () {
        getBlock ().getRelative (orientation).setType (Material.AIR);
        getBlock ().setType (Material.AIR);
    }

    /*
     * (non-Javadoc)
     * @see com.nitnelave.CreeperHeal.block.CreeperBlock#getNeighbors()
     */
    @Override
    public List<NeighborBlock> getDependentNeighbors () {
        return new ArrayList<NeighborBlock> ();
    }

}

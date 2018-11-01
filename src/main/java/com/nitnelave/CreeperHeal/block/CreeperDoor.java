package com.nitnelave.CreeperHeal.block;

import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.material.Door;

import java.util.ArrayList;
import java.util.List;

/**
 * Door implementation of the CreeperBlock.
 *
 * @author nitnelave
 *
 */
class CreeperDoor extends CreeperMultiblock
{

    /*
     * Constructor.
     */
    CreeperDoor(BlockState blockState)
    {
        super(blockState);
        Door data = castData(blockState, Door.class);
        
        if (data.isTopHalf())
        {
            BlockState bottom = blockState.getBlock().getRelative(BlockFace.DOWN).getState();
            if (bottom.getData() instanceof Door && !((Door) bottom.getData()).isTopHalf())
            {
                this.blockState = bottom;
                this.dependents.add(blockState);
            }
        }
        else
        {
            BlockState top = blockState.getBlock().getRelative(BlockFace.UP).getState();
            if (top.getData() instanceof Door && ((Door) top.getData()).isTopHalf())
            {
                this.blockState = blockState;
                this.dependents.add(top);
            }
            
        }
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

}

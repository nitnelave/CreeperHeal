package com.nitnelave.CreeperHeal.block;

import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.material.Door;
import org.bukkit.block.data.Bisected;

import java.util.ArrayList;
import java.util.List;

/**
 * Bisected block implementation of the CreeperBlock.
 *
 * @author nitnelave
 *
 */
class CreeperBisected extends CreeperMultiblock
{

    /*
     * Constructor.
     */
    CreeperBisected(BlockState blockState, Bisected bisected)
    {
        super(blockState);

        if (bisected.getHalf() == Bisected.Half.TOP)
        {
            BlockState bottom = blockState.getBlock().getRelative(BlockFace.DOWN).getState();
            this.blockState = bottom;
            this.blockData = bottom.getBlockData();
            addDependent(blockState);
        }
        else
        {
            BlockState top = blockState.getBlock().getRelative(BlockFace.UP).getState();
            addDependent(top);

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

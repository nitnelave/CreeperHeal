package com.nitnelave.CreeperHeal.block;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;

import java.util.ArrayList;
import java.util.List;

class CreeperFlower extends CreeperMultiblock
{

    /*
     * Constructor.
     */
    CreeperFlower(BlockState blockState)
    {
        super(blockState);
        Block block = blockState.getBlock();
        if ((blockState.getRawData() & 8) != 0)
            block = block.getRelative(BlockFace.DOWN);
        this.blockState = block.getState();
        BlockState relative = block.getRelative(BlockFace.UP).getState();
        if ((relative.getRawData() & 1) == 0)
            this.dependents.add(relative);
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

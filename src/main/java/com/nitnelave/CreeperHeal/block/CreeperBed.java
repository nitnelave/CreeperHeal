package com.nitnelave.CreeperHeal.block;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.type.Bed;

import java.util.ArrayList;
import java.util.List;

/**
 * Bed implementation of CreeperBlock.
 *
 * @author nitnelave
 *
 */
class CreeperBed extends CreeperMultiblock
{
    private final Bed bedData;
    /*
     * Constructor.
     */
    CreeperBed(BlockState blockState)
    {
        super(blockState);
        Bed bedData = (Bed) blockData;
        Block block = blockState.getBlock();
        if (bedData.getPart() == Bed.Part.FOOT) {
            addDependent(block.getState());
            block = block.getRelative(bedData.getFacing());
        } else {
            addDependent(block.getRelative(bedData.getFacing().getOppositeFace()).getState());
        }
        this.blockState = block.getState();
        this.blockData = block.getState().getBlockData();
        this.bedData = (Bed)this.blockData;
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

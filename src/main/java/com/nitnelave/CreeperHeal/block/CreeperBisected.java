package com.nitnelave.CreeperHeal.block;

import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.Bisected;
import org.bukkit.block.data.BlockData;

import java.util.Collections;
import java.util.List;

/**
 * Bisected implementation of a CreeperMultiblock.
 *
 * @author Jikoo
 */
class CreeperBisected extends CreeperMultiblock
{

    CreeperBisected(BlockState blockState)
    {
        super(blockState);

        BlockData blockData = blockState.getBlockData();
        if (!(blockData instanceof Bisected))
            throw new IllegalArgumentException("Invalid BlockData: " + blockData.getClass().getName());

        BlockState top, bottom;

        if (((Bisected) blockData).getHalf() == Bisected.Half.BOTTOM)
        {
            bottom = blockState;
            top = blockState.getBlock().getRelative(BlockFace.UP).getState();
        }
        else 
        {
            top = blockState;
            bottom = blockState.getBlock().getRelative(BlockFace.DOWN).getState();
        }

        if (top.getType() != bottom.getType())
            return;

        BlockData topData = top.getBlockData();
        BlockData bottomData = bottom.getBlockData();


        if (!(topData instanceof Bisected) || !(bottomData instanceof Bisected)
                || ((Bisected) topData).getHalf() == ((Bisected) bottomData).getHalf())
            return;

        this.blockState = bottom;
        this.dependents.add(top);
    }

    @Override
    public List<NeighborBlock> getDependentNeighbors()
    {
        return Collections.emptyList();
    }

}

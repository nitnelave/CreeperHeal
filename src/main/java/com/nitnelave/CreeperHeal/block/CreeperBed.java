package com.nitnelave.CreeperHeal.block;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.material.Bed;

import java.util.ArrayList;
import java.util.List;

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
    CreeperBed(BlockState blockState)
    {
        super(blockState);
        Bed bedData = castData(blockState, Bed.class);
        orientation = bedData.getFacing();
        Block block = blockState.getBlock();
        if (!bedData.isHeadOfBed())
            block = block.getRelative(orientation);
        this.blockState = block.getState();
    }

    /**
     * The blockstate is always the head of the bed, this gets the foot.
     *
     * @return the foot of the bed.
     */
    public Block getFoot()
    {
        return getBlock().getRelative(orientation.getOppositeFace());
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
        Block foot = getFoot();
        foot.setType(Material.BED_BLOCK, false);
        BlockState fs = foot.getState();
        Bed d = castData(blockState, Bed.class);
        d.setHeadOfBed(false);
        d.setFacingDirection(orientation);
        fs.setData(d);
        fs.update(true, false);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.nitnelave.CreeperHeal.block.CreeperBlock#remove()
     */
    @Override
    public void remove()
    {
        getFoot().setType(Material.AIR, false);
        getBlock().setType(Material.AIR, false);
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

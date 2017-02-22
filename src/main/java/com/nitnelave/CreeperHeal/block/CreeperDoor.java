package com.nitnelave.CreeperHeal.block;

import org.bukkit.Material;
import org.bukkit.block.Block;
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
class CreeperDoor extends CreeperBlock
{

    private final Door data;

    /*
     * Constructor.
     */
    CreeperDoor(BlockState blockState)
    {
        data = castData(blockState, Door.class);
        Block block = blockState.getBlock();
        if (data.isTopHalf())
            block = block.getRelative(BlockFace.DOWN);
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
        Block blockUp = blockState.getBlock().getRelative(BlockFace.UP);
        if (checkForDrop(blockUp))
            return;

        super.update();
        blockUp.setType(blockState.getType(), false);
        Door d = data;
        d.setTopHalf(true);
        BlockState s = blockUp.getState();
        s.setData(d);
        s.update(true);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.nitnelave.CreeperHeal.block.CreeperBlock#remove()
     */
    @Override
    public void remove()
    {
        getBlock().setType(Material.AIR);
        getBlock().getRelative(BlockFace.UP).setType(Material.AIR);
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

package com.nitnelave.CreeperHeal.block;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;

/**
 * Door implementation of the CreeperBlock.
 * 
 * @author nitnelave
 * 
 */
class CreeperDoor extends CreeperBlock
{

    private final boolean hingeRight;

    /*
     * Constructor.
     */
    protected CreeperDoor(BlockState blockState)
    {
        Block block = blockState.getBlock();
        if ((blockState.getRawData() & 8) != 0)
            block = block.getRelative(BlockFace.DOWN);
        this.blockState = block.getState();
        hingeRight = (block.getRelative(BlockFace.UP).getState().getRawData() & 1) == 0;
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
        byte b = (byte) (8 + (hingeRight ? 0 : 1));
        blockUp.setTypeIdAndData(getTypeId(), b, false);
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

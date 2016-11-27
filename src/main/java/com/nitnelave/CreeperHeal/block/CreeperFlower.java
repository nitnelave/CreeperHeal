package com.nitnelave.CreeperHeal.block;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;

import java.util.ArrayList;
import java.util.List;

class CreeperFlower extends CreeperBlock
{

    /*
     * The Relative Flower blocks.
     */
    private final boolean relative;

    /*
     * Constructor.
     */
    CreeperFlower(BlockState blockState)
    {
    	Block block = blockState.getBlock();
        if ((blockState.getRawData() & 8) != 0)
            block = block.getRelative(BlockFace.DOWN);
        this.blockState = block.getState();
        relative = (block.getRelative(BlockFace.UP).getState().getRawData() & 1) == 0;
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
        byte b = (byte) (8 + (relative ? 0 : 1));
        blockUp.setTypeIdAndData(getType().getId(), b, false);
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

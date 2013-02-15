package com.nitnelave.CreeperHeal.block;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;

import com.nitnelave.CreeperHeal.config.CreeperConfig;

/**
 * Door implementation of the CreeperBlock.
 * 
 * @author nitnelave
 * 
 */
public class CreeperDoor extends CreeperBlock {

    private final boolean hingeRight;

    /*
     * Constructor.
     */
    protected CreeperDoor(BlockState blockState) {
        Block block = blockState.getBlock ();
        if ((blockState.getRawData () & 8) != 0)
            block = block.getRelative (BlockFace.DOWN);
        this.blockState = block.getState ();
        hingeRight = (getBlock ().getRelative (BlockFace.UP).getState ().getRawData () & 1) == 0;
    }

    /*
     * (non-Javadoc)
     * @see com.nitnelave.CreeperHeal.block.CreeperBlock#update(boolean)
     */
    @Override
    public void update () {
        Block blockUp = blockState.getBlock().getRelative(BlockFace.UP);
        if (!CreeperConfig.overwriteBlocks && !EMPTY_BLOCKS.contains (blockUp.getTypeId ()))
        {
            if(CreeperConfig.dropDestroyedBlocks)
                drop ();
            return;
        }
        else if(CreeperConfig.overwriteBlocks && !EMPTY_BLOCKS.contains(blockUp.getTypeId()) && CreeperConfig.dropDestroyedBlocks)
        {
            CreeperBlock.newBlock (blockUp.getState ()).drop ();
            blockUp.setTypeIdAndData(0, (byte)0, false);
        }
        blockState.update(true);
        byte b = (byte) (8 + (hingeRight ? 0 : 1));
        blockUp.setTypeIdAndData(getTypeId(), b, false);
    }

    /*
     * (non-Javadoc)
     * @see com.nitnelave.CreeperHeal.block.CreeperBlock#remove()
     */
    @Override
    protected void remove () {
        getBlock ().setType (Material.AIR);
        getBlock ().getRelative (BlockFace.UP).setType (Material.AIR);
    }
}

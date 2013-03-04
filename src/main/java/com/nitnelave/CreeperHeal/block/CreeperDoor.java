package com.nitnelave.CreeperHeal.block;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;

import com.nitnelave.CreeperHeal.config.CfgVal;
import com.nitnelave.CreeperHeal.config.CreeperConfig;

/**
 * Door implementation of the CreeperBlock.
 * 
 * @author nitnelave
 * 
 */
class CreeperDoor extends CreeperBlock {

    private final boolean hingeRight;

    /*
     * Constructor.
     */
    protected CreeperDoor (BlockState blockState) {
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
    //TODO: check for duplicate code.
    @Override
    public void update () {
        Block blockUp = blockState.getBlock ().getRelative (BlockFace.UP);
        if (!CreeperConfig.getBool (CfgVal.OVERWRITE_BLOCKS) && !EMPTY_BLOCKS.contains (blockUp.getTypeId ()))
        {
            if (CreeperConfig.getBool (CfgVal.DROP_DESTROYED_BLOCKS))
                drop (true);
            return;
        }
        else if (CreeperConfig.getBool (CfgVal.OVERWRITE_BLOCKS) && !EMPTY_BLOCKS.contains (blockUp.getTypeId ())
                && CreeperConfig.getBool (CfgVal.DROP_DESTROYED_BLOCKS))
        {
            CreeperBlock.newBlock (blockUp.getState ()).drop (true);
            blockUp.setTypeIdAndData (0, (byte) 0, false);
        }
        blockState.update (true);
        byte b = (byte) (8 + (hingeRight ? 0 : 1));
        blockUp.setTypeIdAndData (getTypeId (), b, false);
    }

    /*
     * (non-Javadoc)
     * @see com.nitnelave.CreeperHeal.block.CreeperBlock#remove()
     */
    @Override
    public void remove () {
        getBlock ().setType (Material.AIR);
        getBlock ().getRelative (BlockFace.UP).setType (Material.AIR);
    }

    /*
     * (non-Javadoc)
     * @see com.nitnelave.CreeperHeal.block.CreeperBlock#getNeighbors()
     */
    @Override
    public List<NeighborBlock> getDependentNeighbors () {
        return new ArrayList<NeighborBlock> ();
    }

}

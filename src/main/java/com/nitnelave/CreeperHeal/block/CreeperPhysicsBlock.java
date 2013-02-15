package com.nitnelave.CreeperHeal.block;

import org.bukkit.block.BlockState;

import com.nitnelave.CreeperHeal.config.CreeperConfig;

/**
 * Implementation of CreeperBlock for block affected by gravity.
 * 
 * @author nitnelave
 * 
 */
public class CreeperPhysicsBlock extends CreeperBlock {

    /*
     * Constructor.
     */
    protected CreeperPhysicsBlock (BlockState blockState) {
        super (blockState);
    }

    /*
     * (non-Javadoc)
     * @see com.nitnelave.CreeperHeal.block.CreeperBlock#update()
     */
    @Override
    public void update () {
        if (CreeperConfig.preventBlockFall)
            BlockManager.putFallPrevention (getBlock ().getLocation ());
        blockState.update (true);
    }

}

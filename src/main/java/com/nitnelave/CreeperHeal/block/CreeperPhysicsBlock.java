package com.nitnelave.CreeperHeal.block;

import org.bukkit.block.BlockState;

import com.nitnelave.CreeperHeal.config.CfgVal;
import com.nitnelave.CreeperHeal.config.CreeperConfig;

/**
 * Implementation of CreeperBlock for block affected by gravity.
 * 
 * @author nitnelave
 * 
 */
class CreeperPhysicsBlock extends CreeperBlock
{

    /*
     * Constructor.
     */
    protected CreeperPhysicsBlock(BlockState blockState)
    {
        super(blockState);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.nitnelave.CreeperHeal.block.CreeperBlock#update()
     */
    @Override
    public void update()
    {
        if (CreeperConfig.getBool(CfgVal.PREVENT_BLOCK_FALL))
            FallIndex.putFallPrevention(getBlock().getLocation());
        super.update();
    }

}

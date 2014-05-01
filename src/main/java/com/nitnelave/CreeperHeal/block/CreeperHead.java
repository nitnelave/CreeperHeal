package com.nitnelave.CreeperHeal.block;

import org.bukkit.block.BlockState;
import org.bukkit.block.Skull;

/**
 * Skull implementation of CreeperBlock, to store and replace the orientation,
 * the owner, etc...
 * 
 * @author nitnelave
 * 
 */
class CreeperHead extends CreeperBlock
{

    /*
     * Constructor.
     */
    protected CreeperHead(BlockState blockState)
    {
        super(blockState);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.nitnelave.CreeperHeal.block.CreeperBlock#update(boolean)
     */
    @Override
    public void update()
    {
        super.update();
        Skull skull = (Skull) blockState;
        Skull newSkull = ((Skull) blockState.getBlock().getState());
        newSkull.setRotation(skull.getRotation());
        newSkull.setSkullType(skull.getSkullType());
        if (skull.hasOwner())
            newSkull.setOwner(skull.getOwner());
        newSkull.update(true);
    }

}

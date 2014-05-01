package com.nitnelave.CreeperHeal.block;

import org.bukkit.block.BlockState;

class CreeperPlate extends CreeperBlock
{

    protected CreeperPlate(BlockState blockState)
    {
        super(blockState);
        blockState.setRawData((byte) 0);
    }

}

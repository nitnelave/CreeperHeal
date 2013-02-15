package com.nitnelave.CreeperHeal.block;

import org.bukkit.block.BlockState;

import com.nitnelave.CreeperHeal.config.CreeperConfig;

public class CreeperBrick extends CreeperBlock {

    protected CreeperBrick (BlockState blockState) {
        super (blockState);

        if (CreeperConfig.crackDestroyedBricks && getRawData () == (byte) 0)
            blockState.setRawData ((byte) 2);
    }

}

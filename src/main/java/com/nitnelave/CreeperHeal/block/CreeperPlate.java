package com.nitnelave.CreeperHeal.block;

import org.bukkit.block.BlockState;

public class CreeperPlate extends CreeperBlock {

    protected CreeperPlate (BlockState blockState) {
        super (blockState);
        blockState.setRawData ((byte) 0);
    }

}

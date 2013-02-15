package com.nitnelave.CreeperHeal.block;

import org.bukkit.Material;
import org.bukkit.block.BlockState;

import com.nitnelave.CreeperHeal.config.CreeperConfig;

public class CreeperGrass extends CreeperBlock {

    protected CreeperGrass (BlockState blockState) {
        super (blockState);
        if (CreeperConfig.loadWorld (getWorld ()).grassToDirt)
            blockState.setType (Material.DIRT);
    }

}

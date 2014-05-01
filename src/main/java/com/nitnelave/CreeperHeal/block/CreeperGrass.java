package com.nitnelave.CreeperHeal.block;

import org.bukkit.Material;
import org.bukkit.block.BlockState;

import com.nitnelave.CreeperHeal.config.CreeperConfig;
import com.nitnelave.CreeperHeal.config.WCfgVal;

class CreeperGrass extends CreeperBlock
{

    protected CreeperGrass(BlockState blockState)
    {
        super(blockState);
        if (CreeperConfig.getWorld(getWorld()).getBool(WCfgVal.GRASS_TO_DIRT))
            blockState.setType(Material.DIRT);
    }

}

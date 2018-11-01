package com.nitnelave.CreeperHeal.block;

import org.bukkit.Material;
import org.bukkit.block.BlockState;

import com.nitnelave.CreeperHeal.config.CfgVal;
import com.nitnelave.CreeperHeal.config.CreeperConfig;

class CreeperStone extends CreeperBlock
{

    CreeperStone(BlockState blockState)
    {
        super(blockState);

        if (CreeperConfig.getBool(CfgVal.STONE_TO_COBBLE))
            blockState.setType(Material.COBBLESTONE);
    }

}

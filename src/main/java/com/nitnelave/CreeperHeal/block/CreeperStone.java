package com.nitnelave.CreeperHeal.block;

import org.bukkit.Material;
import org.bukkit.block.BlockState;

import com.nitnelave.CreeperHeal.config.CfgVal;
import com.nitnelave.CreeperHeal.config.CreeperConfig;

public class CreeperStone extends CreeperBlock {

    protected CreeperStone (BlockState blockState) {
        super (blockState);

        if (CreeperConfig.getBool (CfgVal.STONE_TO_COBBLE))
            blockState.setType (Material.COBBLESTONE);
    }

}

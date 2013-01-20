package com.nitnelave.CreeperHeal.block;

import org.bukkit.block.BlockState;

public class CreeperPiston extends CreeperBlock {


	protected CreeperPiston(BlockState blockState) {
		super(blockState);
		blockState.setRawData((byte) (blockState.getRawData() & 7));
	}
	
}

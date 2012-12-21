package com.nitnelave.CreeperHeal.block;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.v1_4_5.block.CraftBlockState;

public class Door extends CraftBlockState{
	private boolean hingeRight;

	public Door(Block block) {
		super(block);
		hingeRight = (block.getRelative(BlockFace.UP).getState().getRawData() & 1) == 0;
	}

	public boolean isHingeRight() {
		return hingeRight;
	}

}

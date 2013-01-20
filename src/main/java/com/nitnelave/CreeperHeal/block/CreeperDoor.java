package com.nitnelave.CreeperHeal.block;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;

import com.nitnelave.CreeperHeal.config.CreeperConfig;

public class CreeperDoor extends CreeperBlock {
	
	private boolean hingeRight;

	protected CreeperDoor(BlockState blockState) {
		super(blockState);
		hingeRight = (blockState.getBlock().getRelative(BlockFace.UP).getState().getRawData() & 1) == 0;
	}

	public boolean isHingeRight() {
		return hingeRight;
	}
	
	@Override
	public void update(boolean force) {
		Block blockUp = blockState.getBlock().getRelative(BlockFace.UP);
		if(!CreeperConfig.overwriteBlocks && !EMPTY_BLOCKS.contains(blockUp.getTypeId())) {        //drop an item on the spot
			if(CreeperConfig.dropDestroyedBlocks)
				dropBlock();
			return;
		}
		else if(CreeperConfig.overwriteBlocks && !EMPTY_BLOCKS.contains(blockUp.getTypeId()) && CreeperConfig.dropDestroyedBlocks)
		{
			CreeperBlock.newBlock(blockUp.getState()).dropBlock();
			blockUp.setTypeIdAndData(0, (byte)0, false);
		}
		blockState.update(true);
		byte b = (byte)(8 + (isHingeRight()?0:1));
		blockUp.setTypeIdAndData(getTypeId(), b, false);
	}

}

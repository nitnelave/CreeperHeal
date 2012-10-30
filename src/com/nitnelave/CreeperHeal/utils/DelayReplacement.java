package com.nitnelave.CreeperHeal.utils;

import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.material.Attachable;

import com.nitnelave.CreeperHeal.block.BlockManager;

public class DelayReplacement implements Runnable
{
	private int counter;
	private BlockState blockState;

	public DelayReplacement(BlockState blockState, int i)
	{
		this.blockState = blockState;
		this.counter = ++i;
	}

	@Override
	public void run()
	{
		if(counter < 50)
		{
			if(blockState instanceof Attachable && blockState.getBlock().getRelative(((Attachable) blockState).getAttachedFace()).getType() == Material.AIR)
				BlockManager.delay_replacement(blockState, counter);
			else if(blockState.getBlock().getRelative(BlockFace.DOWN).getType() == Material.AIR)
				BlockManager.delay_replacement(blockState, counter);
			else
				BlockManager.block_state_replace(blockState);
		}
		else
			BlockManager.block_state_replace(blockState);

	}

}

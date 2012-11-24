package com.nitnelave.CreeperHeal.utils;

import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.material.Attachable;

import com.nitnelave.CreeperHeal.block.CreeperBlock;

public class DelayReplacement implements Runnable
{
	private int counter;
	private CreeperBlock blockState;

	public DelayReplacement(CreeperBlock creeperBlock, int i)
	{
		this.blockState = creeperBlock;
		this.counter = ++i;
	}

	@Override
	public void run()
	{
		if(counter < 50)
		{
			if(blockState instanceof Attachable && blockState.getBlock().getRelative(((Attachable) blockState).getAttachedFace()).getType() == Material.AIR)
				blockState.delay_replacement(counter);
			else if(blockState.getBlock().getRelative(BlockFace.DOWN).getType() == Material.AIR)
				blockState.delay_replacement(counter);
			else
				blockState.replace();
		}
		else
			blockState.replace();

	}

}

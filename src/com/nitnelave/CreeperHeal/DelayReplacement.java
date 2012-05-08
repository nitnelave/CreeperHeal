package com.nitnelave.CreeperHeal;

import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.material.Attachable;

public class DelayReplacement implements Runnable
{
	private int counter;
	private BlockState blockState;
	private CreeperHeal plugin;

	public DelayReplacement(CreeperHeal plugin, BlockState blockState, int i)
	{
		this.blockState = blockState;
		this.counter = ++i;
		this.plugin = plugin;
	}

	@Override
	public void run()
	{
		if(counter < 50)
		{
			if(blockState instanceof Attachable && blockState.getBlock().getRelative(((Attachable) blockState).getAttachedFace()).getType() == Material.AIR)
				plugin.delay_replacement(blockState, counter);
			else if(blockState.getBlock().getRelative(BlockFace.DOWN).getType() == Material.AIR)
				plugin.delay_replacement(blockState, counter);
			else
				plugin.block_state_replace(blockState);
		}
		else
			plugin.block_state_replace(blockState);

	}

}

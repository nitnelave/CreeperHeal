package com.nitnelave.CreeperHeal.utils;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.material.Attachable;

import com.nitnelave.CreeperHeal.CreeperHeal;
import com.nitnelave.CreeperHeal.block.Replaceable;
import com.nitnelave.CreeperHeal.config.CreeperConfig;

public class DelayReplacement implements Runnable
{
	private int counter;
	private Replaceable blockState;

	public DelayReplacement(Replaceable creeperBlock, int i)
	{
		this.blockState = creeperBlock;
		this.counter = ++i;
	}

	@Override
	public void run()
	{
		if(counter < 150)
		{
			if(blockState instanceof Attachable && blockState.getBlock().getRelative(((Attachable) blockState).getAttachedFace()).getType() == Material.AIR)
				delay_replacement();
			else if(blockState.getBlock().getRelative(BlockFace.DOWN).getType() == Material.AIR)
				delay_replacement();
			else
				blockState.replace(true);
		}
		else
			blockState.replace(true);

	}
	
	private void delay_replacement() {
		counter++;
		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(CreeperHeal.getInstance(), this, (long) Math.ceil((double)CreeperConfig.blockPerBlockInterval / 20));

	}

}

package com.nitnelave.CreeperHeal;

import java.util.Date;

import org.bukkit.block.BlockState;

public class CreeperBurntBlock
{
	private BlockState blockState;
	private Date time;

	public CreeperBurntBlock(Date now, BlockState state)
    {
		setBlockState(state);
		setTime(now);
    }
	
	public void addTime(int delay)
	{
		time = new Date(time.getTime() + delay);
	}

	public Date getTime()
    {
	    return time;
    }

	public void setTime(Date time)
    {
	    this.time = time;
    }

	public BlockState getBlockState()
    {
	    return blockState;
    }

	public void setBlockState(BlockState blockState)
    {
	    this.blockState = blockState;
    }

	
	
}

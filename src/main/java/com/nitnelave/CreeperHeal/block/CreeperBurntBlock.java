package com.nitnelave.CreeperHeal.block;

import java.util.Date;

import org.bukkit.block.BlockState;

public class CreeperBurntBlock extends CreeperBlock
{
	private Date time;

	public CreeperBurntBlock(Date now, BlockState state)
    {
		super(state);
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

}

package com.nitnelave.CreeperHeal.block;

import java.util.Date;
import java.util.List;

import org.bukkit.Location;

public class CreeperExplosion
{
	private Date time;
	private List<CreeperBlock> blockList;
	private Location loc;
	private double radius;
	
	public CreeperExplosion(Date time, List<CreeperBlock> blockList, Location loc, double radius)
	{
		this.time = time;
		this.blockList = blockList;
		this.loc = loc;
		this.radius = radius;
	}

	public List<CreeperBlock> getBlockList()
    {
	    return blockList;
    }

	public Date getTime()
    {
	    return time;
    }


	public Location getLocation()
    {
	    return loc;
    }


	public double getRadius() {
		return radius;
	}

	

}

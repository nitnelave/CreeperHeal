package com.nitnelave.CreeperHeal.block;

import java.util.Date;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.block.BlockState;

public class CreeperExplosion
{
	private Date time;
	private List<BlockState> blockList;
	private Location loc;
	private double radius;
	
	public CreeperExplosion(Date time, List<BlockState> blockList, Location loc)
	{
		this.setTime(time);
		this.setBlockList(blockList);
		this.setLocation(loc);
		computeRadius();
	}

	public List<BlockState> getBlockList()
    {
	    return blockList;
    }

	public void setBlockList(List<BlockState> blockList)
    {
	    this.blockList = blockList;
    }

	public Date getTime()
    {
	    return time;
    }

	public void setTime(Date time)
    {
	    this.time = time;
    }

	public Location getLocation()
    {
	    return loc;
    }

	public void setLocation(Location loc)
    {
	    this.loc = loc;
    }

	public double getRadius() {
		return radius;
	}

	public void computeRadius() {
		double r = 0;
		for(BlockState bs : blockList) {
			Location bl = bs.getLocation();
			r = Math.max(r, loc.distance(bl));
		}
		radius = r;
	}

}

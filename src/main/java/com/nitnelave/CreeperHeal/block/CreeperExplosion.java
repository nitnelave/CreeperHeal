package com.nitnelave.CreeperHeal.block;

import java.util.Date;
import java.util.List;

import org.bukkit.Location;

public class CreeperExplosion
{
	private Date time;
	private List<Replaceable> blockList;
	private Location loc;
	private double radius;
	
	public CreeperExplosion(Date time, List<Replaceable> blockList, Location loc, double radius)
	{
		this.time = time;
		this.blockList = blockList;
		this.loc = loc;
		this.radius = radius;
	}

	public List<Replaceable> getBlockList()
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

	@Override
	public boolean equals (Object o) {
		if (!(o instanceof CreeperExplosion))
			return false;
		CreeperExplosion e = (CreeperExplosion) o;
		return e.time == time && e.loc == loc && e.radius == radius;
	}
	
	@Override
	public int hashCode ()
	{
		return (int) (time.hashCode() + radius + loc.hashCode());
	}
	

}

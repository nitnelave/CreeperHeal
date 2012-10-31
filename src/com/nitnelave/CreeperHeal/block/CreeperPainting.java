package com.nitnelave.CreeperHeal.block;

import java.util.Date;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Hanging;

public class CreeperPainting
{
	private Hanging hanging;
	private Date date;
	private boolean fire;
	
	public CreeperPainting(Hanging p, Date d, boolean f)
	{
		hanging = p;
		date = d;
		fire = f;
	}
	
	public Date getDate()
	{
		return date;
	}
	
	public void setDate(Date d)
	{
		date = d;
	}
	
	public boolean isBurnt()
	{
		return fire;
	}
	
	public Hanging getHanging()
	{
		return hanging;
	}
	
	public World getWorld()
	{
		return hanging.getWorld();
	}
	
	public Location getLocation()
	{
		return hanging.getLocation();
	}
	
	public void postPone(int delay)
	{
		date = new Date(date.getTime() + 1000 * delay);
	}
}

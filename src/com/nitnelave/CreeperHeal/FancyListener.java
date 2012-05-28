package com.nitnelave.CreeperHeal;

import java.util.Set;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.material.Rails;

public class FancyListener implements Listener
{
	private CreeperHeal plugin;


	public FancyListener(CreeperHeal instance)
	{
		plugin = instance;
	}

	@EventHandler
	public void onBlockPhysics(BlockPhysicsEvent event)
	{
		if(event.isCancelled())
			return;
		
		Block b = event.getBlock();
		if(b.getState() instanceof Rails)
		{
			if(plugin.preventUpdate.containsKey(b.getState()))
				event.setCancelled(true);
		}
		else if(b.getType() == Material.VINE)
		{
			Location vineLoc = b.getLocation();
			World w = vineLoc.getWorld();
			synchronized(plugin.explosionList)
			{
				for(CreeperExplosion cEx : plugin.explosionList)
				{
					Location loc = cEx.getLocation();
					if(loc.getWorld() == w)
					{
						if(loc.distance(vineLoc) < 20)
						{
							event.setCancelled(true);
							return;
						}
					}
				}
			}
			synchronized(plugin.fireList)
			{
				for(Location loc : plugin.fireList.keySet())
				{
					if(loc.getWorld() == w)
					{
						if(loc.distance(vineLoc) < 10)
						{
							event.setCancelled(true);
							return;
						}
					}
				}
			}
		}
		else if(CreeperHeal.blocks_physics.contains(b.getTypeId()))
		{
			Location bLoc = b.getLocation();
			World w = bLoc.getWorld();
			Set<Location> set = plugin.preventBlockFall.keySet();
			if(plugin.config.preventBlockFall)
			{
				synchronized(plugin.explosionList)
				{
					for(CreeperExplosion cEx : plugin.explosionList)
					{
						try{
							set.add(cEx.getLocation());}
						catch(UnsupportedOperationException e){continue;}
					}
				}
			}

			synchronized(plugin.preventBlockFall)
			{
				for(Location loc : plugin.preventBlockFall.keySet())
				{
					if(loc.getWorld() == w)
					{
						if(loc.distance(bLoc) < 10)
						{
							event.setCancelled(true);
							return;
						}
					}
				}
			}
		}
	}

	@EventHandler
	synchronized public void onLeavesDecay(LeavesDecayEvent e)
	{
		if(e.isCancelled())
			return;
		
		Location leafLoc = e.getBlock().getLocation();
		World w = leafLoc.getWorld();
		for(CreeperExplosion cEx : plugin.explosionList)
		{
			Location loc = cEx.getLocation();
			if(loc.getWorld() == w)
			{
				if(loc.distance(leafLoc) < 20)
				{
					e.setCancelled(true);
					return;
				}
			}
		}
		for(Location loc : plugin.fireList.keySet())
		{
			if(loc.getWorld() == w)
			{
				if(loc.distance(leafLoc) < 5)
				{
					e.setCancelled(true);
					return;
				}
			}
		}
	}

}

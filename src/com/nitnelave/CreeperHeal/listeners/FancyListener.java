package com.nitnelave.CreeperHeal.listeners;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.material.Rails;

import com.nitnelave.CreeperHeal.CreeperHeal;
import com.nitnelave.CreeperHeal.block.BlockManager;
import com.nitnelave.CreeperHeal.block.CreeperExplosion;
import com.nitnelave.CreeperHeal.block.ExplodedBlockManager;
import com.nitnelave.CreeperHeal.config.CreeperConfig;

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
			if(plugin.getPreventUpdate().containsKey(b.getState()))
				event.setCancelled(true);
		}
		else if(b.getType() == Material.VINE)
		{
			Location vineLoc = b.getLocation();
			World w = vineLoc.getWorld();
			synchronized(ExplodedBlockManager.getExplosionList())
			{
				for(CreeperExplosion cEx : ExplodedBlockManager.getExplosionList())
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
			synchronized(CreeperHeal.getFireList())
			{
				for(Location loc : CreeperHeal.getFireList().keySet())
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
		else if(CreeperConfig.preventBlockFall && BlockManager.blocks_physics.contains(b.getTypeId()))
		{
			Location bLoc = b.getLocation();
			World w = bLoc.getWorld();
			if(plugin.getPreventBlockFall().containsKey(bLoc))
			{
				event.setCancelled(true);
				return;
			}
			
			synchronized(ExplodedBlockManager.getExplosionList())
			{
				for(CreeperExplosion c : ExplodedBlockManager.getExplosionList())
				{
					Location loc = c.getLocation();
					if(loc.getWorld() == w)
					{
						if(loc.distance(bLoc) < 20)
						{
							event.setCancelled(true);
							return;
						}
					}
				}
			}
			
			synchronized(plugin.getPreventBlockFall())
			{
				for(Location loc : plugin.getPreventBlockFall().keySet())
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
		for(CreeperExplosion cEx : ExplodedBlockManager.getExplosionList())
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
		for(Location loc : CreeperHeal.getFireList().keySet())
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

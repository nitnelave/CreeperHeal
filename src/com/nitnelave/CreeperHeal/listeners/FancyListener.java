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
import com.nitnelave.CreeperHeal.block.BurntBlockManager;
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
			if(ExplodedBlockManager.isNextToExplosion(b.getLocation()))
			{
				event.setCancelled(true);
				return;
			}
			if(BurntBlockManager.isNextToFire(b.getLocation()))
			{
				event.setCancelled(true);
				return;
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
	synchronized public void onLeavesDecay(LeavesDecayEvent event)
	{
		if(event.isCancelled())
			return;

		Block b = event.getBlock();
		if(ExplodedBlockManager.isNextToExplosion(b.getLocation()))
		{
			event.setCancelled(true);
			return;
		}
		if(BurntBlockManager.isNextToFire(b.getLocation()))
		{
			event.setCancelled(true);
			return;
		}
	}

}

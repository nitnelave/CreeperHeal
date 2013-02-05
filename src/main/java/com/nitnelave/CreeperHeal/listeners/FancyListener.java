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
import com.nitnelave.CreeperHeal.block.BurntBlockManager;
import com.nitnelave.CreeperHeal.block.CreeperBlock;
import com.nitnelave.CreeperHeal.block.ExplodedBlockManager;
import com.nitnelave.CreeperHeal.config.CreeperConfig;

public class FancyListener implements Listener
{


	@EventHandler (priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onBlockPhysics(BlockPhysicsEvent event)
	{
		Block b = event.getBlock();
		if(b.getState() instanceof Rails)
		{
			if(CreeperHeal.getPreventUpdate().containsKey(CreeperBlock.newBlock(b.getState())))
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
		else if(CreeperConfig.preventBlockFall && CreeperBlock.hasPhysics(b.getTypeId()))
		{
			Location bLoc = b.getLocation();
			World w = bLoc.getWorld();
			if(CreeperHeal.getPreventBlockFall().containsKey(bLoc))
			{
				event.setCancelled(true);
				return;
			}

			if (ExplodedBlockManager.isNextToExplosion(bLoc))
			{
				event.setCancelled(true);
				return;
			}


			synchronized(CreeperHeal.getPreventBlockFall())
			{
				for(Location loc : CreeperHeal.getPreventBlockFall().keySet())
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

	@EventHandler (priority = EventPriority.NORMAL, ignoreCancelled = true)
	synchronized public void onLeavesDecay(LeavesDecayEvent event)
	{
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

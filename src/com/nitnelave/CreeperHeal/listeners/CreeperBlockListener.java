package com.nitnelave.CreeperHeal.listeners;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Hanging;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockIgniteEvent.IgniteCause;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.hanging.HangingBreakEvent.RemoveCause;

import com.nitnelave.CreeperHeal.CreeperHeal;
import com.nitnelave.CreeperHeal.block.BlockId;
import com.nitnelave.CreeperHeal.block.BurntBlockManager;
import com.nitnelave.CreeperHeal.block.CreeperExplosion;
import com.nitnelave.CreeperHeal.block.ExplodedBlockManager;
import com.nitnelave.CreeperHeal.block.PaintingsManager;
import com.nitnelave.CreeperHeal.config.CreeperConfig;
import com.nitnelave.CreeperHeal.config.WorldConfig;
import com.nitnelave.CreeperHeal.utils.CreeperLog;
import com.nitnelave.CreeperHeal.utils.CreeperPermissionManager;
import com.nitnelave.CreeperHeal.utils.CreeperPlayer;
import com.nitnelave.CreeperHeal.utils.CreeperUtils;

public class CreeperBlockListener implements Listener{


	@EventHandler(priority = EventPriority.MONITOR)
	public void onHangingBreak(HangingBreakEvent e)
	{
		CreeperLog.debug("Hanging removed because of " + e.getCause());
		if(e.isCancelled())
			return;


		Hanging h = (Hanging) e.getEntity();
		if(e instanceof HangingBreakByEntityEvent)
		{
			CreeperLog.debug("HanginBreakByEntityEvent");
			HangingBreakByEntityEvent event = (HangingBreakByEntityEvent) e;
			Entity remover = event.getRemover();
			if(remover instanceof Creeper || remover instanceof TNTPrimed || remover instanceof Fireball || remover instanceof EnderDragon)
			{
				WorldConfig world = CreeperConfig.loadWorld(remover.getWorld());
				if(CreeperUtils.shouldReplace(remover, world)) {
					PaintingsManager.checkForPaintings(h, world.isRepairTimed(), false);
				}
			}
		}
		/*else if(e.getCause() == RemoveCause.FIRE)
		{
			WorldConfig world = CreeperConfig.loadWorld(p.getWorld());
			if(world.fire) {
				PaintingsManager.checkForPaintings(p, world.isRepairTimed(), true);
			}
		}*/
		else if(e.getCause() == RemoveCause.PHYSICS /*|| e.getCause() == RemoveCause.WATER*/)
		{
			CreeperLog.debug("Hanging removed because of physics");
			if(!CreeperConfig.lightweightMode)
			{
				Location paintLoc = h.getLocation();
				World w = paintLoc.getWorld();
				synchronized(ExplodedBlockManager.getExplosionList())
				{
					for(CreeperExplosion cEx : ExplodedBlockManager.getExplosionList())
					{
						Location loc = cEx.getLocation();
						if(loc.getWorld() == w)
						{
							if(loc.distance(paintLoc) < 20)
							{
								boolean should;
								WorldConfig world = CreeperConfig.loadWorld(w);
								if(world.replaceAbove)
								{
									if(paintLoc.getY() >= world.replaceLimit)
										should =  world.creepers;
									else
										should = false;
								}
								else
									should = world.creepers;
								if(should) 
									PaintingsManager.checkForPaintings(h, world.isRepairTimed(), false);
								return;
							}
						}
					}
				}
				if(BurntBlockManager.isNextToFire(paintLoc))
				{
					WorldConfig world = CreeperConfig.loadWorld(w);

					if(world.fire) 
						PaintingsManager.checkForPaintings(h, world.isRepairTimed(), true);
					return;
				}
			}
		}
		else
			CreeperLog.logInfo("Hanging removed for another reason", 2);
	}




	@EventHandler(priority = EventPriority.MONITOR)
	public void onBlockBurn(BlockBurnEvent event) {
		CreeperLog.logInfo("BlockBurntEvent", 3);
		if(event.isCancelled())
			return;

		WorldConfig world = CreeperConfig.loadWorld(event.getBlock().getLocation().getWorld());

		if(world.fire)
			BurntBlockManager.recordBurn(event.getBlock());

		if(world.preventFireSpread)
			event.getBlock().setTypeIdAndData(0, (byte) 0, false);
	}


	@EventHandler(priority = EventPriority.NORMAL)
	public void onBlockPlace(BlockPlaceEvent event)
	{
		if(event.isCancelled())
			return;

		Player player = event.getPlayer();
		WorldConfig world = CreeperConfig.loadWorld(player.getWorld());
		if(event.getBlockPlaced().getType() == Material.TNT && !CreeperPermissionManager.checkPermissions(player, true, "bypass.place-tnt"))
		{
			boolean blocked = world.blockTNT;
			if(blocked)
				event.setCancelled(true);
			if(world.warnTNT)
				CreeperHeal.warn(CreeperPlayer.WarningCause.TNT, player, blocked, null);
		}
		else if(world.blockBlackList)
		{
			if(world.placeList.contains(new BlockId(event.getBlock().getTypeId(), event.getBlock().getData())) && !CreeperPermissionManager.checkPermissions(player, true, "bypass.place-blacklist"))
			{
				boolean blocked = world.blockBlackList;
				if(blocked)
					event.setCancelled(true);
				if(world.warnBlackList)
					CreeperHeal.warn(CreeperPlayer.WarningCause.BLACKLIST, player, blocked, event.getBlockPlaced().getType().toString());
			}
		}

	}



	@EventHandler(priority = EventPriority.NORMAL)
	public void onBlockIgnite(BlockIgniteEvent event)
	{
		if(event.isCancelled())
			return;

		WorldConfig world = CreeperConfig.loadWorld(event.getBlock().getWorld());

		/*Player player = event.getPlayer();
		if(player != null)
		{
			if(!plugin.getPermissionManager().checkPermissions(player, true, "ignite"))
			{
				boolean blocked = world.blockIgnite;
				if(blocked)
					event.setCancelled(true);
				if(world.warnIgnite)
					plugin.warn(CreeperPlayer.WarningCause.FIRE, player, blocked, null);
			}
		}
		else */
		//CreeperHeal.log.info(event.getCause().name() + " ; " + world.preventFireSpread);
		if(event.getCause() == IgniteCause.SPREAD && world.preventFireSpread)
			event.setCancelled(true);
		else if(event.getCause() == IgniteCause.LAVA && world.preventFireLava)
			event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onBlockSpread(BlockSpreadEvent event) {
		if(event.isCancelled() || !event.getBlock().getType().equals(Material.FIRE))
			return;
		WorldConfig world = CreeperConfig.loadWorld(event.getBlock().getWorld());

		CreeperLog.logInfo("Fire Spread!", 2);
		event.getBlock().setTypeId(0);
		event.getSource().setTypeId(0);

		if(world.preventFireSpread)
			event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onEntityExplode(EntityExplodeEvent event) {//explosion
		CreeperLog.logInfo("EntityExplodeEvent", 3);
		if(event.isCancelled())
			return;
		CreeperLog.logInfo("explosion not cancelled", 3);
		WorldConfig world = CreeperConfig.loadWorld(event.getLocation().getWorld());

		if (CreeperHeal.getFactionHandler().shouldIgnore(event.blockList(), world)) {
			return;
		}

		CreeperLog.logInfo("faction handler says ok", 3);
		Entity entity = event.getEntity();
		if(CreeperUtils.shouldReplace(entity, world))
			ExplodedBlockManager.recordBlocks(event, world);
	}

}

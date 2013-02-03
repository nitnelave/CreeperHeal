package com.nitnelave.CreeperHeal.listeners;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockIgniteEvent.IgniteCause;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.entity.EntityExplodeEvent;

import com.nitnelave.CreeperHeal.CreeperHeal;
import com.nitnelave.CreeperHeal.PluginHandler;
import com.nitnelave.CreeperHeal.block.BlockId;
import com.nitnelave.CreeperHeal.block.BurntBlockManager;
import com.nitnelave.CreeperHeal.block.ExplodedBlockManager;
import com.nitnelave.CreeperHeal.config.CreeperConfig;
import com.nitnelave.CreeperHeal.config.WorldConfig;
import com.nitnelave.CreeperHeal.utils.CreeperLog;
import com.nitnelave.CreeperHeal.utils.CreeperPermissionManager;
import com.nitnelave.CreeperHeal.utils.CreeperPlayer;

public class CreeperBlockListener implements Listener{





	@EventHandler(priority = EventPriority.MONITOR)
	public void onBlockBurn(BlockBurnEvent event) {
		CreeperLog.logInfo("BlockBurntEvent", 3);
		if(event.isCancelled())
			return;

		WorldConfig world = CreeperConfig.loadWorld(event.getBlock().getLocation().getWorld());

		if(world.fire)
		{
			if(!CreeperConfig.lightweightMode)
			{
				if(BurntBlockManager.wasRecentlyBurnt(event.getBlock().getLocation()))
				{
					event.setCancelled(true);
					return;
				}
			}
			BurntBlockManager.recordBurn(event.getBlock());
		}

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
		else if(world.griefBlockList)
		{
			if(world.placeList.contains(new BlockId(event.getBlock().getTypeId(), event.getBlock().getData())) && !CreeperPermissionManager.checkPermissions(player, true, "bypass.place-blacklist"))
			{
				boolean blocked = world.griefBlockList;
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

		if (PluginHandler.getFactionHandler().shouldIgnore(event.blockList(), world)) {
			return;
		}

		CreeperLog.logInfo("faction handler says ok", 3);
		Entity entity = event.getEntity();
		if(world.shouldReplace(entity))
			ExplodedBlockManager.recordBlocks(event, world);
	}

}

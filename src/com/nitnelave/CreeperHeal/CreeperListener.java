package com.nitnelave.CreeperHeal;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Enderman;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Painting;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockIgniteEvent.IgniteCause;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.painting.PaintingBreakByEntityEvent;
import org.bukkit.event.painting.PaintingBreakEvent;
import org.bukkit.event.painting.PaintingBreakEvent.RemoveCause;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;


public class CreeperListener implements Listener{

	private static CreeperHeal plugin;


	public CreeperListener(CreeperHeal instance)        //declaration of the plugin dependence, or something like that
	{
		plugin = instance;
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onEntityExplode(EntityExplodeEvent event) {//explosion
		if(event.isCancelled())
			return;

		WorldConfig world = getWorld(event.getLocation().getWorld());

		Entity entity = event.getEntity();
		if(CreeperUtils.shouldReplace(entity, world))
			recordBlocks(event, world);


	}

	private void recordBlocks(EntityExplodeEvent event, WorldConfig world) {
		plugin.recordBlocks(event, world);
	}

	private WorldConfig getWorld(World w) {
		return plugin.loadWorld(w);
	}


	@EventHandler(priority = EventPriority.NORMAL)
	public void onEntityDamage(EntityDamageEvent event)
	{
		if(event.isCancelled())
			return;

		Entity en = event.getEntity();
		if(en instanceof Painting)
		{
			if(event instanceof EntityDamageByEntityEvent)
			{
				EntityDamageByEntityEvent e = (EntityDamageByEntityEvent)event;
				Entity entity = e.getDamager();
				if(entity instanceof Creeper || entity instanceof TNTPrimed || entity instanceof Fireball || entity instanceof EnderDragon)
				{
					WorldConfig world = getWorld(entity.getWorld());
					if(CreeperUtils.shouldReplace(entity, world))         //if it's a creeper, and creeper explosions are recorded
						plugin.checkForPaintings((Painting)en, world.isRepairTimed(), false);
				}
			}
			else
			{
				plugin.log_info("Painting destroyed by block?", 1);
			}
		}
		else 
		{
			if(en instanceof Player)

			{
				Player offender = null;
				String message = "";
				WorldConfig world = getWorld(event.getEntity().getWorld());
				DamageCause cause = event.getCause();
				if(cause == DamageCause.ENTITY_ATTACK)
				{
					Entity attacker = ((EntityDamageByEntityEvent)event).getDamager();
					if(attacker instanceof Player)
					{
						offender = (Player) attacker;
						message = offender.getItemInHand().getType().toString();
						
					}
				}
				else if(cause == DamageCause.PROJECTILE)
				{
					Projectile projectile = (Projectile) ((EntityDamageByEntityEvent) event).getDamager();
					Entity attacker = projectile.getShooter();
					if(attacker instanceof Player)
					{
						offender = (Player) attacker;
						message = projectile.getType().toString();
					}
				}
				else if(cause == DamageCause.MAGIC)
				{
					Projectile projectile = (Projectile) ((EntityDamageByEntityEvent) event).getDamager();
					if(projectile instanceof ThrownPotion)
					{
						Entity attacker = projectile.getShooter();
						if(attacker instanceof Player)
						{
							offender = (Player) attacker;
							message = "magic potion";
						}
					}
				}
				if(offender != null && !plugin.getPermissionManager().checkPermissions(offender, true, "bypass.pvp"))
				{						
					boolean blocked = world.blockPvP;
					if(blocked)
						event.setCancelled(true);
					if(world.warnPvP)
						plugin.warn(CreeperPlayer.WarningCause.PVP, offender, blocked, message);
				}
			}
		}
	}




	@EventHandler(priority = EventPriority.MONITOR)
	public void onPaintingBreak(PaintingBreakEvent e)
	{
		if(e.isCancelled())
			return;

		if(e.getCause() == RemoveCause.ENTITY)
		{
			PaintingBreakByEntityEvent event = (PaintingBreakByEntityEvent)e;
			Entity remover = event.getRemover();
			if(remover instanceof Creeper || remover instanceof TNTPrimed || remover instanceof Fireball || remover instanceof EnderDragon)
			{
				WorldConfig world = getWorld(remover.getWorld());
				if(CreeperUtils.shouldReplace(remover, world))         //if it's a creeper, and creeper explosions are recorded
					plugin.checkForPaintings(event.getPainting(), world.isRepairTimed(), false);
			}
		}
		else if(e.getCause() == RemoveCause.FIRE)
		{
			WorldConfig world = getWorld(e.getPainting().getWorld());
			if(world.fire)
				plugin.checkForPaintings(e.getPainting(), world.isRepairTimed(), true);
		}
		else if(e.getCause() == RemoveCause.PHYSICS || e.getCause() == RemoveCause.WATER)
		{
			if(!plugin.config.lightweightMode)
			{
				Location paintLoc = e.getPainting().getLocation();
				World w = paintLoc.getWorld();
				synchronized(plugin.explosionList)
				{
					for(CreeperExplosion cEx : plugin.explosionList)
					{
						Location loc = cEx.getLocation();
						if(loc.getWorld() == w)
						{
							if(loc.distance(paintLoc) < 20)
							{
								boolean should;
								WorldConfig world = getWorld(w);
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
									plugin.checkForPaintings(e.getPainting(), world.isRepairTimed(), false);
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
							if(loc.distance(paintLoc) < 10)
							{
								WorldConfig world = getWorld(w);

								if(world.fire)
									plugin.checkForPaintings(e.getPainting(), world.isRepairTimed(), true);
								return;
							}
						}
					}
				}
			}
		}
	}




	@EventHandler(priority = EventPriority.MONITOR)
	public void onBlockBurn(BlockBurnEvent event) {
		if(event.isCancelled())
			return;

		WorldConfig world = plugin.loadWorld( event.getBlock().getLocation().getWorld());

		if(world.fire)
			plugin.record_burn(event.getBlock());
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onEndermanPickup(EntityChangeBlockEvent event) {//enderman pickup
		if(event.isCancelled())
			return;

		if (event.getEntity() instanceof Enderman)
		{
			WorldConfig world = getWorld(event.getBlock().getWorld());
			if(world.enderman)
				event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerInteract(PlayerInteractEvent event)
	{
		if(event.isCancelled())
			return;

		Player player = event.getPlayer();
		WorldConfig world = getWorld(player.getWorld());

		ItemStack item = event.getItem();
		if(item == null)
			return;

		if(item.getType() == Material.MONSTER_EGG && !plugin.getPermissionManager().checkPermissions(player, true, "bypass.spawnEgg"))
		{
			String entityType = CreeperUtils.getEntityNameFromId(event.getItem().getData().getData());

			boolean blocked = world.blockSpawnEggs;
			if(blocked)
				event.setCancelled(true);
			if(world.warnSpawnEggs)
				plugin.warn(CreeperPlayer.WarningCause.SPAWN_EGG, player, blocked, entityType);
		}
		if(item.getType() == Material.FLINT_AND_STEEL && !plugin.getPermissionManager().checkPermissions(player, true, "bypass.ignite"))
		{
			boolean blocked = world.blockIgnite;
			if(blocked)
				event.setCancelled(true);
			if(world.warnIgnite)
				plugin.warn(CreeperPlayer.WarningCause.FIRE, player, blocked, null);
		}
	}



	@EventHandler(priority = EventPriority.NORMAL)
	public void onBlockIgnite(BlockIgniteEvent event)
	{
		if(event.isCancelled())
			return;

		WorldConfig world = getWorld(event.getBlock().getWorld());

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
	public void onPlayerBucketEmpty(PlayerBucketEmptyEvent event)
	{
		if(event.isCancelled())
			return;

		WorldConfig world = getWorld(event.getPlayer().getWorld());

		Player player = event.getPlayer();
		if(event.getBucket() == Material.LAVA_BUCKET && !plugin.getPermissionManager().checkPermissions(player, true, "bypass.place-lava"))
		{
			boolean blocked = world.blockLava;
			if(blocked)
				event.setCancelled(true);
			if(world.warnLava)
				plugin.warn(CreeperPlayer.WarningCause.LAVA, player, blocked, null);
		}
	}


	@EventHandler(priority = EventPriority.NORMAL)
	public void onBlockPlace(BlockPlaceEvent event)
	{
		if(event.isCancelled())
			return;

		Player player = event.getPlayer();
		WorldConfig world = getWorld(player.getWorld());
		if(event.getBlockPlaced().getType() == Material.TNT && !plugin.getPermissionManager().checkPermissions(player, true, "bypass.place-tnt"))
		{
			boolean blocked = world.blockTNT;
			if(blocked)
				event.setCancelled(true);
			if(world.warnTNT)
				plugin.warn(CreeperPlayer.WarningCause.TNT, player, blocked, null);
		}
		else if(world.blockBlackList)
		{
			if(world.placeList.contains(new BlockId(event.getBlock().getTypeId(), event.getBlock().getData())) && !plugin.getPermissionManager().checkPermissions(player, true, "bypass.place-blacklist"))
			{
				boolean blocked = world.blockBlackList;
				if(blocked)
					event.setCancelled(true);
				if(world.warnBlackList)
					plugin.warn(CreeperPlayer.WarningCause.BLACKLIST, player, blocked, event.getBlockPlaced().getType().toString());
			}
		}

	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerJoin(PlayerJoinEvent event)
	{
		if(plugin.checkPermissions(event.getPlayer(), false, "warn.*", "warn.lava", "warn.fire", "warn.tnt", "warn.blacklist", "warn.spawnEggs"))
		{
			plugin.warnList.add(new CreeperPlayer(event.getPlayer(), plugin));
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerQuit(PlayerQuitEvent event)
	{
		String name = event.getPlayer().getName();
		for(CreeperPlayer cp : plugin.warnList)
		{
			if(cp.getPlayer().getName().equals(name))
			{
				plugin.warnList.remove(cp);
				return;
			}
		}
	}

}

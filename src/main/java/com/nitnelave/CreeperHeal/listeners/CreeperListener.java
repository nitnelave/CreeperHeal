package com.nitnelave.CreeperHeal.listeners;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Enderman;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Hanging;
import org.bukkit.entity.Painting;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.hanging.HangingBreakEvent.RemoveCause;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import com.nitnelave.CreeperHeal.CreeperHeal;
import com.nitnelave.CreeperHeal.block.BurntBlockManager;
import com.nitnelave.CreeperHeal.block.CreeperBlock;
import com.nitnelave.CreeperHeal.block.CreeperExplosion;
import com.nitnelave.CreeperHeal.block.ExplodedBlockManager;
import com.nitnelave.CreeperHeal.block.PaintingsManager;
import com.nitnelave.CreeperHeal.config.CreeperConfig;
import com.nitnelave.CreeperHeal.config.WorldConfig;
import com.nitnelave.CreeperHeal.utils.CreeperLog;
import com.nitnelave.CreeperHeal.utils.CreeperPermissionManager;
import com.nitnelave.CreeperHeal.utils.CreeperPlayer;
import com.nitnelave.CreeperHeal.utils.CreeperUtils;



public class CreeperListener implements Listener{

	private static CreeperHeal plugin;


	public CreeperListener(CreeperHeal instance)        //declaration of the plugin dependence, or something like that
	{
		plugin = instance;
	}



	private WorldConfig getWorld(World w) {
		return CreeperConfig.loadWorld(w);
	}



	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onHangingBreak(HangingBreakEvent e)
	{
		Hanging h = (Hanging) e.getEntity();
		if(e instanceof HangingBreakByEntityEvent)
		{
			HangingBreakByEntityEvent event = (HangingBreakByEntityEvent) e;
			Entity remover = event.getRemover();
			if(remover instanceof Creeper || remover instanceof TNTPrimed || remover instanceof Fireball || remover instanceof EnderDragon)
			{
				WorldConfig world = CreeperConfig.loadWorld(remover.getWorld());
				if(world.shouldReplace(remover))
					PaintingsManager.checkPainting(h, world.isRepairTimed(), false);
			}
		}
		else if(e.getCause() == RemoveCause.EXPLOSION)
		{
			WorldConfig world = CreeperConfig.loadWorld(e.getEntity().getWorld());
			PaintingsManager.checkPainting(h, world.isRepairTimed(), false);
		}
		else if(e.getCause() == RemoveCause.PHYSICS && !CreeperConfig.lightweightMode)
		{
			Location paintLoc = h.getLocation();
			World w = paintLoc.getWorld();
			synchronized(ExplodedBlockManager.getExplosionList())
			{
				for(CreeperExplosion cEx : ExplodedBlockManager.getExplosionList())
				{
					Location loc = cEx.getLocation();
					if(loc.getWorld() == w && loc.distance(paintLoc) < 20)
					{
						WorldConfig world = CreeperConfig.loadWorld(w);
						boolean should = world.creepers;
						if(world.replaceAbove && paintLoc.getY() < world.replaceLimit)
							should = false;
						if(should)
							PaintingsManager.checkPainting(h, world.isRepairTimed(), false);
						return;
					}

				}
			}
			if(BurntBlockManager.isNextToFire(paintLoc))
			{
				WorldConfig world = CreeperConfig.loadWorld(w);

				if(world.fire)
					PaintingsManager.checkPainting(h, world.isRepairTimed(), true);
			}

		}
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onEntityDamage(EntityDamageEvent event)
	{
		Entity en = event.getEntity();
		if(en instanceof Painting && event instanceof EntityDamageByEntityEvent)
		{
			EntityDamageByEntityEvent e = (EntityDamageByEntityEvent)event;
			Entity entity = e.getDamager();
			if(entity instanceof Creeper || entity instanceof TNTPrimed || entity instanceof Fireball || entity instanceof EnderDragon)
			{
				WorldConfig world = getWorld(entity.getWorld());
				if(world.shouldReplace(entity))
					PaintingsManager.checkPainting((Painting)en, world.isRepairTimed(), false);
			}

		}
		else if(en instanceof Player){
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
			else if(cause == DamageCause.PROJECTILE && ((EntityDamageByEntityEvent) event).getDamager() instanceof Projectile)
			{
				Projectile projectile = (Projectile) ((EntityDamageByEntityEvent) event).getDamager();
				Entity attacker = projectile.getShooter();
				if(attacker instanceof Player)
				{
					offender = (Player) attacker;
					message = projectile.getType().toString();
				}

			}
			else if(cause == DamageCause.MAGIC && ((EntityDamageByEntityEvent) event).getDamager() instanceof Projectile)
			{
				Projectile projectile = (Projectile) ((EntityDamageByEntityEvent) event).getDamager();
				Entity attacker = projectile.getShooter();
				if(projectile instanceof ThrownPotion && attacker instanceof Player)
				{
					offender = (Player) attacker;
					message = "magic potion";
				}
			}
			if(offender != null && !CreeperPermissionManager.checkPermissions(offender, true, "bypass.pvp"))
			{
				boolean blocked = world.blockPvP;
				if(blocked)
					event.setCancelled(true);
				if(world.warnPvP)
					CreeperHeal.warn(CreeperPlayer.WarningCause.PVP, offender, blocked, message);
			}
		}
	}


	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onEndermanPickup(EntityChangeBlockEvent event) {//enderman pickup
		if (event.getEntity() instanceof Enderman)
		{
			WorldConfig world = getWorld(event.getBlock().getWorld());
			if(world.enderman)
				event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onPlayerInteract(PlayerInteractEvent event)
	{
		ItemStack item = event.getItem();
		if(item == null)
			return;

		Player player = event.getPlayer();
		WorldConfig world = getWorld(player.getWorld());

		if(item.getType() == Material.MONSTER_EGG && !CreeperPermissionManager.checkPermissions(player, true, "bypass.spawnEgg"))
		{
			String entityType = CreeperUtils.getEntityNameFromId(event.getItem().getData().getData());

			if(world.blockSpawnEggs)
				event.setCancelled(true);
			if(world.warnSpawnEggs)
				CreeperHeal.warn(CreeperPlayer.WarningCause.SPAWN_EGG, player, world.blockSpawnEggs, entityType);
		}
		else if(item.getType() == Material.FLINT_AND_STEEL && !CreeperPermissionManager.checkPermissions(player, true, "bypass.ignite"))
		{
			if(world.blockIgnite)
				event.setCancelled(true);
			if(world.warnIgnite)
				CreeperHeal.warn(CreeperPlayer.WarningCause.FIRE, player, world.blockIgnite, null);
		}
	}


	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onPlayerBucketEmpty(PlayerBucketEmptyEvent event)
	{
		WorldConfig world = getWorld(event.getPlayer().getWorld());

		Player player = event.getPlayer();
		if(event.getBucket() == Material.LAVA_BUCKET && !CreeperPermissionManager.checkPermissions(player, true, "bypass.place-lava"))
		{
			if(world.blockLava)
				event.setCancelled(true);
			if(world.warnLava)
				CreeperHeal.warn(CreeperPlayer.WarningCause.LAVA, player, world.blockLava, null);
		}
	}


	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerJoin(PlayerJoinEvent event)
	{
		if(CreeperPermissionManager.checkPermissions(event.getPlayer(), false, "warn.*", "warn.lava", "warn.fire", "warn.tnt", "warn.blacklist", "warn.spawnEggs"))
			CreeperHeal.getWarnList().add(new CreeperPlayer(event.getPlayer(), plugin));
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerQuit(PlayerQuitEvent event)
	{
		String name = event.getPlayer().getName();
		for(CreeperPlayer cp : CreeperHeal.getWarnList())
		{
			if(cp.getPlayer().getName().equals(name))
			{
				CreeperHeal.getWarnList().remove(cp);
				return;
			}
		}
	}


	@EventHandler (priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onEntityChangeBlock(EntityChangeBlockEvent e)
	{
		if (e.getBlock().getType() == Material.MONSTER_EGG || e.getEntityType() == EntityType.SILVERFISH)
			CreeperLog.debug("entity change block");
		if (CreeperBlock.hasPhysics(e.getBlock().getTypeId()))
		{
			Location l = e.getBlock().getLocation();
			World w = l.getWorld();
			synchronized(CreeperHeal.getPreventBlockFall())
			{
				for(Location loc : CreeperHeal.getPreventBlockFall().keySet())
				{
					if(loc.getWorld() == w && loc.distance(l) < 3)
					{
						e.setCancelled(true);
						return;
					}
				}
			}
			if (ExplodedBlockManager.isNextToExplosion(l))
			{
				e.setCancelled(true);
				return;
			}
		}
	}
}

package com.nitnelave.CreeperHeal.listeners;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Enderman;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Painting;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import com.nitnelave.CreeperHeal.CreeperHeal;
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
					if(CreeperUtils.shouldReplace(entity, world)) 
						PaintingsManager.checkForPaintings((Painting)en, world.isRepairTimed(), false);
				}
			}
			else
			{
				CreeperLog.logInfo("Painting destroyed by block?", 1);
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

		if(item.getType() == Material.MONSTER_EGG && !CreeperPermissionManager.checkPermissions(player, true, "bypass.spawnEgg"))
		{
			String entityType = CreeperUtils.getEntityNameFromId(event.getItem().getData().getData());

			boolean blocked = world.blockSpawnEggs;
			if(blocked)
				event.setCancelled(true);
			if(world.warnSpawnEggs)
				CreeperHeal.warn(CreeperPlayer.WarningCause.SPAWN_EGG, player, blocked, entityType);
		}
		if(item.getType() == Material.FLINT_AND_STEEL && !CreeperPermissionManager.checkPermissions(player, true, "bypass.ignite"))
		{
			boolean blocked = world.blockIgnite;
			if(blocked)
				event.setCancelled(true);
			if(world.warnIgnite)
				CreeperHeal.warn(CreeperPlayer.WarningCause.FIRE, player, blocked, null);
		}
	}


	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerBucketEmpty(PlayerBucketEmptyEvent event)
	{
		if(event.isCancelled())
			return;

		WorldConfig world = getWorld(event.getPlayer().getWorld());

		Player player = event.getPlayer();
		if(event.getBucket() == Material.LAVA_BUCKET && !CreeperPermissionManager.checkPermissions(player, true, "bypass.place-lava"))
		{
			boolean blocked = world.blockLava;
			if(blocked)
				event.setCancelled(true);
			if(world.warnLava)
				CreeperHeal.warn(CreeperPlayer.WarningCause.LAVA, player, blocked, null);
		}
	}


	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerJoin(PlayerJoinEvent event)
	{
		if(CreeperPermissionManager.checkPermissions(event.getPlayer(), false, "warn.*", "warn.lava", "warn.fire", "warn.tnt", "warn.blacklist", "warn.spawnEggs"))
		{
			CreeperHeal.getWarnList().add(new CreeperPlayer(event.getPlayer(), plugin));
		}
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
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onCreatureSpawn(CreatureSpawnEvent event) {
		if(event.isCancelled())
			return;
		
		WorldConfig w = CreeperConfig.loadWorld(event.getLocation().getWorld());
		if(event.getEntityType() == EntityType.WITHER && !w.spawnWither)
			event.setCancelled(true);
			
	}

}

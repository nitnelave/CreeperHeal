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
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.hanging.HangingBreakEvent.RemoveCause;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import com.nitnelave.CreeperHeal.block.BurntBlockManager;
import com.nitnelave.CreeperHeal.block.ExplodedBlockManager;
import com.nitnelave.CreeperHeal.block.HangingsManager;
import com.nitnelave.CreeperHeal.config.CreeperConfig;
import com.nitnelave.CreeperHeal.config.WorldConfig;
import com.nitnelave.CreeperHeal.utils.CreeperLog;
import com.nitnelave.CreeperHeal.utils.CreeperMessenger;
import com.nitnelave.CreeperHeal.utils.CreeperPermissionManager;
import com.nitnelave.CreeperHeal.utils.CreeperPlayer;
import com.nitnelave.CreeperHeal.utils.FactionHandler;

/**
 * Listener for the entity events.
 * 
 * @author nitnelave
 * 
 */
public class CreeperListener implements Listener {

    /**
     * Listener for the EntityExplodeEvent. Record when appropriate the
     * explosion for later replacement.
     * 
     * @param event
     */
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntityExplode (EntityExplodeEvent event) {
        WorldConfig world = CreeperConfig.loadWorld (event.getLocation ().getWorld ());

        if (FactionHandler.shouldIgnore (event.blockList (), world))
            return;

        Entity entity = event.getEntity ();
        if (world.shouldReplace (entity))
            ExplodedBlockManager.processExplosion (event, world);
    }

    /**
     * Listener for the HangingBreakEvent. If appropriate, the hanging is
     * recorded to be replaced later on.
     * 
     * @param event
     *            The HangingBreakEvent.
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onHangingBreak (HangingBreakEvent event) {
        Hanging h = event.getEntity ();
        if (event instanceof HangingBreakByEntityEvent)
        {
            Entity remover = ((HangingBreakByEntityEvent) event).getRemover ();
            if (remover instanceof Creeper || remover instanceof TNTPrimed || remover instanceof Fireball || remover instanceof EnderDragon)
            {
                WorldConfig world = CreeperConfig.loadWorld (remover.getWorld ());
                if (world.shouldReplace (remover))
                    HangingsManager.checkHanging (h, world.isRepairTimed (), false);
            }
        }
        else if (event.getCause () == RemoveCause.EXPLOSION)
        {
            WorldConfig world = CreeperConfig.loadWorld (event.getEntity ().getWorld ());
            HangingsManager.checkHanging (h, world.isRepairTimed (), false);
        }
        else if (event.getCause () == RemoveCause.PHYSICS && !CreeperConfig.lightweightMode)
        {
            Location paintLoc = h.getLocation ();
            World w = paintLoc.getWorld ();
            if (ExplodedBlockManager.isNextToExplosion (paintLoc))
            {
                WorldConfig world = CreeperConfig.loadWorld (w);
                boolean should = world.creepers;
                if (world.replaceAbove && paintLoc.getY () < world.replaceLimit)
                    should = false;
                if (should)
                    HangingsManager.checkHanging (h, world.isRepairTimed (), false);
            }
            else if (BurntBlockManager.isNextToFire (paintLoc))
            {
                WorldConfig world = CreeperConfig.loadWorld (w);
                if (world.fire)
                    HangingsManager.checkHanging (h, world.isRepairTimed (), true);
            }

        }
    }

    /**
     * Listener for the EntityDamageEvent. Control PVP and check for destroyed
     * paintings.
     * 
     * @param event
     *            The EntityDamageEvent.
     */
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onEntityDamage (EntityDamageEvent event) {
        Entity en = event.getEntity ();
        if (en instanceof Painting && event instanceof EntityDamageByEntityEvent)
        {
            EntityDamageByEntityEvent e = (EntityDamageByEntityEvent) event;
            Entity entity = e.getDamager ();
            if (entity instanceof Creeper || entity instanceof TNTPrimed || entity instanceof Fireball || entity instanceof EnderDragon)
            {
                WorldConfig world = CreeperConfig.loadWorld (entity.getWorld ());
                if (world.shouldReplace (entity))
                    HangingsManager.checkHanging ((Painting) en, world.isRepairTimed (), false);
            }

        }
        else if (en instanceof Player)
        {
            Player offender = null;
            String message = "";
            WorldConfig world = CreeperConfig.loadWorld (event.getEntity ().getWorld ());
            DamageCause cause = event.getCause ();
            if (cause == DamageCause.ENTITY_ATTACK)
            {
                Entity attacker = ((EntityDamageByEntityEvent) event).getDamager ();
                if (attacker instanceof Player)
                {
                    offender = (Player) attacker;
                    message = offender.getItemInHand ().getType ().toString ();
                }
            }
            else if (cause == DamageCause.PROJECTILE && ((EntityDamageByEntityEvent) event).getDamager () instanceof Projectile)
            {
                Projectile projectile = (Projectile) ((EntityDamageByEntityEvent) event).getDamager ();
                Entity attacker = projectile.getShooter ();
                if (attacker instanceof Player)
                {
                    offender = (Player) attacker;
                    message = projectile.getType ().toString ();
                }

            }
            else if (cause == DamageCause.MAGIC && ((EntityDamageByEntityEvent) event).getDamager () instanceof Projectile)
            {
                Projectile projectile = (Projectile) ((EntityDamageByEntityEvent) event).getDamager ();
                Entity attacker = projectile.getShooter ();
                if (projectile instanceof ThrownPotion && attacker instanceof Player)
                {
                    offender = (Player) attacker;
                    message = "magic potion";
                }
            }
            if (offender != null && !CreeperPermissionManager.checkPermissions (offender, true, "bypass.pvp"))
            {
                boolean blocked = world.blockPvP;
                if (blocked)
                    event.setCancelled (true);
                if (world.warnPvP)
                    CreeperMessenger.warn (CreeperPlayer.WarningCause.PVP, offender, blocked, message);
            }
        }
    }

    /**
     * Listener for the EntityChangeBlockEvent. Check for Endermen picking up
     * blocks.
     * 
     * @param event
     */
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onEndermanPickup (EntityChangeBlockEvent event) {
        if (event.getBlock ().getType () == Material.MONSTER_EGG || event.getEntityType () == EntityType.SILVERFISH)
            CreeperLog.debug ("silverfish entity change block");
        if (event.getEntity () instanceof Enderman)
        {
            WorldConfig world = CreeperConfig.loadWorld (event.getBlock ().getWorld ());
            if (world.enderman)
                event.setCancelled (true);
        }
    }

    /**
     * Listener for the PlayerInteractEvent. Check for monster egg use, and
     * block ignition.
     * 
     * @param event
     */
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPlayerInteract (PlayerInteractEvent event) {
        ItemStack item = event.getItem ();
        if (item == null)
            return;

        Player player = event.getPlayer ();
        WorldConfig world = CreeperConfig.loadWorld (player.getWorld ());

        if (item.getType () == Material.MONSTER_EGG && !CreeperPermissionManager.checkPermissions (player, true, "bypass.spawnEgg"))
        {
            String entityType = EntityType.fromId (event.getItem ().getData ().getData ()).getName ();

            if (world.blockSpawnEggs)
                event.setCancelled (true);
            if (world.warnSpawnEggs)
                CreeperMessenger.warn (CreeperPlayer.WarningCause.SPAWN_EGG, player, world.blockSpawnEggs, entityType);
        }
        else if (item.getType () == Material.FLINT_AND_STEEL && !CreeperPermissionManager.checkPermissions (player, true, "bypass.ignite"))
        {
            if (world.blockIgnite)
                event.setCancelled (true);
            if (world.warnIgnite)
                CreeperMessenger.warn (CreeperPlayer.WarningCause.FIRE, player, world.blockIgnite, null);
        }
    }

    /**
     * Listener for the PlayerBucketEmptyEvent. Check for lava placement.
     * 
     * @param event
     */
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPlayerBucketEmpty (PlayerBucketEmptyEvent event) {
        WorldConfig world = CreeperConfig.loadWorld (event.getPlayer ().getWorld ());

        Player player = event.getPlayer ();
        if (event.getBucket () == Material.LAVA_BUCKET && !CreeperPermissionManager.checkPermissions (player, true, "bypass.place-lava"))
        {
            if (world.blockLava)
                event.setCancelled (true);
            if (world.warnLava)
                CreeperMessenger.warn (CreeperPlayer.WarningCause.LAVA, player, world.blockLava, null);
        }
    }

    /**
     * Listener for the PlayerJoinEvent. Add when appropriate to the warning
     * list.
     * 
     * @param event
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin (PlayerJoinEvent event) {
        CreeperMessenger.registerPlayer (new CreeperPlayer (event.getPlayer ()));
    }

    /**
     * Listener for the PlayerQuitEvent. Remove when appropriate from the
     * warning list.
     * 
     * @param event
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit (PlayerQuitEvent event) {
        CreeperMessenger.removeFromWarnList (new CreeperPlayer (event.getPlayer ()));
    }

}
package com.nitnelave.CreeperHeal;

import org.bukkit.Material;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.EnderDragon;
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
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockIgniteEvent.IgniteCause;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import com.nitnelave.CreeperHeal.block.HangingsManager;
import com.nitnelave.CreeperHeal.config.CreeperConfig;
import com.nitnelave.CreeperHeal.config.WorldConfig;
import com.nitnelave.CreeperHeal.utils.CreeperMessenger;
import com.nitnelave.CreeperHeal.utils.CreeperPermissionManager;
import com.nitnelave.CreeperHeal.utils.CreeperPlayer;

public class GriefListener implements Listener {

    /**
     * Listener for the BlockPlaceEvent. If the player does not have the rights
     * to place a block, the event is cancelled, and the appropriate warnings
     * are fired.
     * 
     * @param event
     *            The BlockPlaceEvent.
     */
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onBlockPlace (BlockPlaceEvent event) {
        Player player = event.getPlayer ();
        WorldConfig world = CreeperConfig.loadWorld (player.getWorld ());
        if (event.getBlockPlaced ().getType () == Material.TNT && !CreeperPermissionManager.checkPermissions (player, false, "bypass.place-tnt"))
        {
            boolean blocked = world.blockTNT;
            if (blocked)
                event.setCancelled (true);
            if (world.warnTNT)
                CreeperMessenger.warn (CreeperPlayer.WarningCause.TNT, player, blocked, null);
        }
        else if (world.isBlackListed (event.getBlock ()) && !CreeperPermissionManager.checkPermissions (player, false, "bypass.place-blacklist"))
        {
            boolean blocked = world.griefBlockList;
            if (blocked)
                event.setCancelled (true);
            if (world.warnBlackList)
                CreeperMessenger.warn (CreeperPlayer.WarningCause.BLACKLIST, player, blocked, event.getBlockPlaced ().getType ().toString ());
        }

    }

    /**
     * Listener for the BlockIgniteEvent. If fire spreading or fire from lava is
     * disabled, cancel the event.
     * 
     * @param event
     *            The BlockIgniteEvent.
     */
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onBlockIgnite (BlockIgniteEvent event) {
        WorldConfig world = CreeperConfig.loadWorld (event.getBlock ().getWorld ());

        if (event.getCause () == IgniteCause.SPREAD && world.preventFireSpread)
            event.setCancelled (true);
        else if (event.getCause () == IgniteCause.LAVA && world.preventFireLava)
            event.setCancelled (true);
    }

    /**
     * Listener for the BlockSpreadEvent. If the event concerns fire and fire
     * spreading is disabled, cancel the event.
     * 
     * @param event
     *            The BlockSpreadEvent.
     */
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onBlockSpread (BlockSpreadEvent event) {
        if (!event.getBlock ().getType ().equals (Material.FIRE))
            return;
        WorldConfig world = CreeperConfig.loadWorld (event.getBlock ().getWorld ());

        event.getBlock ().setTypeId (0);
        event.getSource ().setTypeId (0);

        if (world.preventFireSpread)
            event.setCancelled (true);
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

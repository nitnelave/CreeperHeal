package com.nitnelave.CreeperHeal.listeners;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockIgniteEvent.IgniteCause;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import com.nitnelave.CreeperHeal.config.CreeperConfig;
import com.nitnelave.CreeperHeal.config.WCfgVal;
import com.nitnelave.CreeperHeal.config.WorldConfig;
import com.nitnelave.CreeperHeal.utils.CreeperMessenger;
import com.nitnelave.CreeperHeal.utils.CreeperPermissionManager;
import com.nitnelave.CreeperHeal.utils.CreeperPlayer;
import org.bukkit.projectiles.ProjectileSource;

/**
 * Listener for grief-related events.
 */
public class GriefListener implements Listener
{

    /**
     * Listener for the BlockPlaceEvent. If the player does not have the rights
     * to place a block, the event is cancelled, and the appropriate warnings
     * are fired.
     *
     * @param event
     *            The BlockPlaceEvent.
     */
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent event)
    {
        Player player = event.getPlayer();
        WorldConfig world = CreeperConfig.getWorld(player.getWorld());
        if (event.getBlockPlaced().getType() == Material.TNT
            && !CreeperPermissionManager.checkPermissions(player, false, "bypass.place-tnt"))
        {
            boolean blocked = world.getBool(WCfgVal.BLOCK_TNT);
            if (blocked)
                event.setCancelled(true);
            if (world.getBool(WCfgVal.WARN_TNT))
                CreeperMessenger.warn(CreeperPlayer.WarningCause.TNT, player, blocked, null);
        }
        else if (world.isGriefBlackListed(event.getBlock())
                 && !CreeperPermissionManager.checkPermissions(player, false, "bypass.place-blacklist"))
        {
            boolean blocked = world.getBool(WCfgVal.GRIEF_BLOCK_BLACKLIST);
            if (blocked)
                event.setCancelled(true);
            if (world.getBool(WCfgVal.WARN_BLACKLIST))
                CreeperMessenger.warn(CreeperPlayer.WarningCause.BLACKLIST, player, blocked, event.getBlockPlaced().getType().toString());
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
    public void onBlockIgnite(BlockIgniteEvent event)
    {
        WorldConfig world = CreeperConfig.getWorld(event.getBlock().getWorld());

        if (event.getCause() == IgniteCause.SPREAD && world.getBool(WCfgVal.PREVENT_FIRE_SPREAD))
            event.setCancelled(true);
        else if (event.getCause() == IgniteCause.LAVA && world.getBool(WCfgVal.PREVENT_FIRE_LAVA))
            event.setCancelled(true);
    }

    /**
     * Listener for the EntityDamageByEntityEvent. Control PVP and check for
     * destroyed paintings.
     *
     * @param event
     *            The EntityDamageByEntityEvent.
     */
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event)
    {
        if (event.getEntity() instanceof Player)
        {
            Player attacked = (Player) event.getEntity();
            Player offender = null;
            String message = attacked.getDisplayName();
            Entity attacker;
            switch (event.getCause())
            {
            case ENTITY_ATTACK:
                attacker = event.getDamager();
                if (attacker instanceof Player)
                    offender = (Player) attacker;
                break;
            case PROJECTILE:
            case MAGIC:
                Entity damager = event.getDamager();
                if (damager instanceof Projectile)
                {
                    ((Projectile) damager).getShooter();
                    ProjectileSource source = ((Projectile) damager).getShooter();
                    if (source instanceof Player)
                        offender = (Player) source;
                }
                break;
            default:
            }
            if (offender != null && !offender.equals(attacked)
                && !CreeperPermissionManager.checkPermissions(offender, true, "bypass.pvp"))
            {
                WorldConfig world = CreeperConfig.getWorld(event.getEntity().getWorld());
                boolean blocked = world.getBool(WCfgVal.BLOCK_PVP);
                if (blocked)
                    event.setCancelled(true);
                if (world.getBool(WCfgVal.WARN_PVP))
                    CreeperMessenger.warn(CreeperPlayer.WarningCause.PVP, offender, blocked, message);
            }
        }
    }

    /**
     * Listener for the PlayerBucketEmptyEvent. Check for lava placement.
     *
     * @param event
     *            The PlayerBucketEmpty event.
     */
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPlayerBucketEmpty(PlayerBucketEmptyEvent event)
    {
        WorldConfig world = CreeperConfig.getWorld(event.getPlayer().getWorld());

        Player player = event.getPlayer();
        if (event.getBucket() == Material.LAVA_BUCKET
            && !CreeperPermissionManager.checkPermissions(player, true, "bypass.place-lava"))
        {
            boolean blocked = world.getBool(WCfgVal.BLOCK_LAVA);
            if (blocked)
                event.setCancelled(true);
            if (world.getBool(WCfgVal.WARN_LAVA))
                CreeperMessenger.warn(CreeperPlayer.WarningCause.LAVA, player, blocked, null);
        }
    }

    /**
     * Listener for the PlayerInteractEvent. Check for monster egg use, and
     * block ignition.
     *
     * @param event
     *            The PlayerInteract event.
     */
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPlayerInteract(PlayerInteractEvent event)
    {
        ItemStack item = event.getItem();
        if (item == null)
            return;

        Player player = event.getPlayer();
        WorldConfig world = CreeperConfig.getWorld(player.getWorld());

        if (item.getType() == Material.MONSTER_EGG
            && !CreeperPermissionManager.checkPermissions(player, true, "bypass.spawnEgg"))
        {
            String entityType = EntityType.fromId(event.getItem().getData().getData()).getName();

            boolean blocked = world.getBool(WCfgVal.BLOCK_SPAWN_EGGS);
            if (blocked)
                event.setCancelled(true);
            if (world.getBool(WCfgVal.WARN_SPAWN_EGGS))
                CreeperMessenger.warn(CreeperPlayer.WarningCause.SPAWN_EGG, player, blocked, entityType);
        }
        else if (item.getType() == Material.FLINT_AND_STEEL
                 && !CreeperPermissionManager.checkPermissions(player, true, "bypass.ignite"))
        {
            boolean blocked = world.getBool(WCfgVal.BLOCK_IGNITE);
            if (blocked)
                event.setCancelled(true);
            if (world.getBool(WCfgVal.WARN_IGNITE))
                CreeperMessenger.warn(CreeperPlayer.WarningCause.FIRE, player, blocked, null);
        }
    }

    /**
     * Listener for the PlayerJoinEvent. Add when appropriate to the warning
     * list.
     *
     * @param event
     *            The PlayerJoin event.
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event)
    {
        CreeperMessenger.registerPlayer(new CreeperPlayer(event.getPlayer()));
    }

    /**
     * Listener for the PlayerQuitEvent. Remove when appropriate from the
     * warning list.
     *
     * @param event
     *            The PlayerQuit event.
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event)
    {
        CreeperMessenger.removeFromWarnList(new CreeperPlayer(event.getPlayer()));
    }

}

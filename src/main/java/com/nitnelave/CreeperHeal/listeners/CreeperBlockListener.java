package com.nitnelave.CreeperHeal.listeners;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockIgniteEvent.IgniteCause;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockSpreadEvent;

import com.nitnelave.CreeperHeal.block.BlockId;
import com.nitnelave.CreeperHeal.block.BurntBlockManager;
import com.nitnelave.CreeperHeal.config.CreeperConfig;
import com.nitnelave.CreeperHeal.config.WorldConfig;
import com.nitnelave.CreeperHeal.utils.CreeperMessenger;
import com.nitnelave.CreeperHeal.utils.CreeperPermissionManager;
import com.nitnelave.CreeperHeal.utils.CreeperPlayer;

/**
 * Listener for all block-related events.
 * 
 * @author nitnelave
 * 
 */
public class CreeperBlockListener implements Listener {

    /**
     * Listener for the BlockBurntEvent. If appropriate, the block is recorded
     * for future replacement.
     * 
     * @param event
     *            The BlockBurntEvent.
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockBurn (BlockBurnEvent event) {
        WorldConfig world = CreeperConfig.loadWorld (event.getBlock ().getLocation ().getWorld ());

        if (world.fire)
        {
            if (!CreeperConfig.lightweightMode)
                if (BurntBlockManager.wasRecentlyBurnt (event.getBlock ()))
                {
                    event.setCancelled (true);
                    return;
                }
            BurntBlockManager.recordBurn (event.getBlock ());
        }

    }

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
        else if (world.griefBlockList)
            if (world.placeList.contains (new BlockId (event.getBlock ().getTypeId (), event.getBlock ().getData ()))
                    && !CreeperPermissionManager.checkPermissions (player, false, "bypass.place-blacklist"))
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

}

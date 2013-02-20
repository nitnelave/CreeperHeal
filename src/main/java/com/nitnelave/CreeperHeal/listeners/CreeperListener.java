package com.nitnelave.CreeperHeal.listeners;

import org.bukkit.Material;
import org.bukkit.entity.Enderman;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Hanging;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.hanging.HangingBreakEvent;

import com.nitnelave.CreeperHeal.block.BurntBlockManager;
import com.nitnelave.CreeperHeal.block.ExplodedBlockManager;
import com.nitnelave.CreeperHeal.block.HangingsManager;
import com.nitnelave.CreeperHeal.config.CreeperConfig;
import com.nitnelave.CreeperHeal.config.WorldConfig;
import com.nitnelave.CreeperHeal.utils.CreeperLog;
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
        WorldConfig world = CreeperConfig.loadWorld (h.getWorld ());
        switch (event.getCause ())
        {
            case EXPLOSION:
                HangingsManager.checkHanging (h, false);
                break;
            case PHYSICS:
                if (!CreeperConfig.lightweightMode && BurntBlockManager.isNextToFire (h.getLocation ()) && world.fire)
                    HangingsManager.checkHanging (h, true);
                break;
            case OBSTRUCTION:
                if (!CreeperConfig.lightweightMode && BurntBlockManager.isNextToFire (h.getLocation ()) && world.fire)
                    HangingsManager.checkHanging (h, true);
                break;
            default:
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

}
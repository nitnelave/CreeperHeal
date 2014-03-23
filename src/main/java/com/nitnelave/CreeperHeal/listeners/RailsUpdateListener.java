package com.nitnelave.CreeperHeal.listeners;

import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPhysicsEvent;

import com.nitnelave.CreeperHeal.block.CreeperBlock;
import com.nitnelave.CreeperHeal.block.RailsIndex;

/**
 * Listener for the rails' update event.
 * 
 * @author nitnelave
 */
public class RailsUpdateListener implements Listener
{

    /**
     * Listener for the blockPhysicsEvent, to prevent rails from updating.
     * 
     * @param event
     *            The BlockPhysicsEvent
     */
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onBlockPhysics(BlockPhysicsEvent event)
    {
        Block b = event.getBlock();
        if (RailsIndex.isUpdatePrevented(CreeperBlock.newBlock(b.getState())))
            event.setCancelled(true);
    }
}

package com.nitnelave.CreeperHeal.listeners;

import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockIgniteEvent;

import com.nitnelave.CreeperHeal.block.BurntBlockManager;
import com.nitnelave.CreeperHeal.block.CreeperBlock;

public class BlockIgniteListener implements Listener
{

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    void onBlockIgnite(BlockIgniteEvent e)
    {
        for (BlockFace b : CreeperBlock.CARDINALS)
            if (BurntBlockManager.wasRecentlyBurnt(e.getBlock().getRelative(b)))
            {
                e.setCancelled(true);
                return;
            }
    }

}

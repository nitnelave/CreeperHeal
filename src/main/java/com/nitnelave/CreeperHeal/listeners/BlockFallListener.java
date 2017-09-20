package com.nitnelave.CreeperHeal.listeners;

import com.nitnelave.CreeperHeal.block.BurntBlockManager;
import com.nitnelave.CreeperHeal.block.ExplodedBlockManager;
import com.nitnelave.CreeperHeal.block.FallIndex;
import com.nitnelave.CreeperHeal.config.CfgVal;
import com.nitnelave.CreeperHeal.config.CreeperConfig;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;

/**
 * Listener for all events related to blocks falling.
 * 
 * @author nitnelave
 */
public class BlockFallListener implements Listener
{

    /**
     * Listener for the BLockPhysicsEvent. Prevent block fall when appropriate
     * (close to an explosion).
     * 
     * @param event
     *            The BlockPhysics event.
     */
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockPhysics(BlockPhysicsEvent event)
    {
        Block b = event.getBlock();
        if (CreeperConfig.getBool(CfgVal.PREVENT_BLOCK_FALL) && b.getType().hasGravity())
        {
            Location bLoc = b.getLocation();
            if (FallIndex.isNextToFallPrevention(bLoc)
                || ExplodedBlockManager.isNextToExplosion(bLoc)
                || BurntBlockManager.isNextToFire(bLoc))
                event.setCancelled(true);
        }
    }

    /**
     * Listener for the EntityChangeBlockEvent. Prevents blocks from falling
     * when replaced.
     * 
     * @param event
     *            The EntityChangeBlock event.
     */
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntityChangeBlock(EntityChangeBlockEvent event)
    {
        if (event.getEntityType() != EntityType.ENDERMAN && event.getBlock().getType().hasGravity())
        {
            Location l = event.getBlock().getLocation();
            if (FallIndex.isNextToFallPrevention(l) || ExplodedBlockManager.isNextToExplosion(l)
                || BurntBlockManager.isNextToFire(l))
                event.setCancelled(true);
        }
    }

}

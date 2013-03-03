package com.nitnelave.CreeperHeal.listeners;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.material.Rails;

import com.nitnelave.CreeperHeal.block.BlockManager;
import com.nitnelave.CreeperHeal.block.BurntBlockManager;
import com.nitnelave.CreeperHeal.block.CreeperBlock;
import com.nitnelave.CreeperHeal.block.ExplodedBlockManager;
import com.nitnelave.CreeperHeal.config.CfgVal;
import com.nitnelave.CreeperHeal.config.CreeperConfig;

/**
 * Listeners to fine tune the block replacement process, at the cost of some
 * performance.
 * 
 * @author nitnelave
 * 
 */
public class FancyListener implements Listener {

    /**
     * Listener for the BLockPhysicsEvent. Prevent rails redirection, vine
     * disappearance and block fall when appropriate (close to an explosion).
     * 
     * @param event
     */
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onBlockPhysics (BlockPhysicsEvent event) {
        Block b = event.getBlock ();
        if (b.getState () instanceof Rails && BlockManager.isUpdatePrevented (CreeperBlock.newBlock (b.getState ())))
            event.setCancelled (true);
        else if (b.getType () == Material.VINE
                && (ExplodedBlockManager.isNextToExplosion (b.getLocation ()) || BurntBlockManager.isNextToFire (b.getLocation ())))
            event.setCancelled (true);
        else if (CreeperConfig.getBool (CfgVal.PREVENT_BLOCK_FALL) && CreeperBlock.hasPhysics (b.getTypeId ()))
        {
            Location bLoc = b.getLocation ();
            if (BlockManager.isNextToFallPrevention (bLoc) || ExplodedBlockManager.isNextToExplosion (bLoc) || BurntBlockManager.isNextToFire (bLoc))
                event.setCancelled (true);
        }
    }

    /**
     * Listener for the LeavesDecayEvent. Prevents leaves from decaying if they
     * are close to an explosion or a fire.
     * 
     * @param event
     */
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onLeavesDecay (LeavesDecayEvent event) {
        Block b = event.getBlock ();
        if (ExplodedBlockManager.isNextToExplosion (b.getLocation ()) || BurntBlockManager.isNextToFire (b.getLocation ()))
            event.setCancelled (true);
    }

    /**
     * Listener for the EntityChangeBlockEvent. Prevents blocks from falling
     * when replaced.
     * 
     * @param event
     */
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onEntityChangeBlock (EntityChangeBlockEvent event) {
        if (event.getEntityType () != EntityType.ENDERMAN && CreeperBlock.hasPhysics (event.getBlock ().getTypeId ()))
        {
            Location l = event.getBlock ().getLocation ();
            if (BlockManager.isNextToFallPrevention (l) || ExplodedBlockManager.isNextToExplosion (l) || BurntBlockManager.isNextToFire (l))
                event.setCancelled (true);
        }
    }

}

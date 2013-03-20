package com.nitnelave.CreeperHeal.listeners;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.LeavesDecayEvent;

import com.nitnelave.CreeperHeal.block.BurntBlockManager;
import com.nitnelave.CreeperHeal.block.ExplodedBlockManager;

public class LeavesListener implements Listener {

    /**
     * Listener for the BLockPhysicsEvent. Prevent rails redirection, vine
     * disappearance and block fall when appropriate (close to an explosion).
     * 
     * @param event
     *            The BlockPhysics event.
     */
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onBlockPhysics (BlockPhysicsEvent event) {
        Block b = event.getBlock ();
        if (b.getType () == Material.VINE && (ExplodedBlockManager.isNextToExplosion (b.getLocation ()) || BurntBlockManager.isNextToFire (b.getLocation ())))
            event.setCancelled (true);
    }

    /**
     * Listener for the LeavesDecayEvent. Prevents leaves from decaying if they
     * are close to an explosion or a fire.
     * 
     * @param event
     *            The LeavesDecay event.
     */
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onLeavesDecay (LeavesDecayEvent event) {
        Block b = event.getBlock ();
        if (ExplodedBlockManager.isNextToExplosion (b.getLocation ()) || BurntBlockManager.isNextToFire (b.getLocation ()))
            event.setCancelled (true);
    }

}

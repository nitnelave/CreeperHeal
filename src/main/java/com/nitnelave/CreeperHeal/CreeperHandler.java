package com.nitnelave.CreeperHeal;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.event.entity.EntityExplodeEvent;

import com.nitnelave.CreeperHeal.block.BurntBlockManager;
import com.nitnelave.CreeperHeal.block.ExplodedBlockManager;
import com.nitnelave.CreeperHeal.config.CreeperConfig;

/**
 * Public API interface.
 * 
 * @author nitnelave
 * 
 */
public abstract class CreeperHandler {

    /**
     * Record all the blocks in the list as for an explosion.
     * 
     * @param list
     *            The list of blocks.
     */
    public static void recordBlocks (List<Block> list) {
        recordBlocks (list, list.get (0).getLocation ());
    }

    /**
     * Record all the blocks in the list as for an explosion. The location of
     * the explosion is provided.
     * 
     * @param list
     *            The list of blocks.
     * @param location
     *            The location of the explosion.
     */
    public static void recordBlocks (List<Block> list, Location location) {
        ExplodedBlockManager.processExplosion (list, location);
    }

    /**
     * Record the explosion as a normal one.
     * 
     * @param event
     *            The event corresponding to an explosion.
     */
    public static void recordBlocks (EntityExplodeEvent event) {
        ExplodedBlockManager.processExplosion (event, CreeperConfig.getWorld (event.getLocation ().getWorld ()));
    }

    /**
     * Record a block to be replaced later, and remove it from the world. The
     * surrounding blocks, if dependent on it, are taken care of.
     * 
     * @param block
     *            The block to record.
     */
    public static void recordBlock (Block block) {
        BurntBlockManager.recordBurntBlock (block);
    }

    /**
     * Get whether the explosion created by this entity should remove protection
     * of blocks protected by LWC.
     * 
     * @param entity
     *            The source of the explosion.
     * @return Whether the protection should be removed.
     */
    public static boolean shouldRemoveLWCProtection (Entity entity) {
        return !CreeperConfig.getWorld (entity.getWorld ()).shouldReplace (entity);
    }

}

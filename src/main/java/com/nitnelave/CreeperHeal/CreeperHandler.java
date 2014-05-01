package com.nitnelave.CreeperHeal;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.event.entity.EntityExplodeEvent;

import com.nitnelave.CreeperHeal.block.BurntBlockManager;
import com.nitnelave.CreeperHeal.block.ExplodedBlockManager;
import com.nitnelave.CreeperHeal.config.CreeperConfig;
import com.nitnelave.CreeperHeal.events.CHExplosionRecordEvent;
import com.nitnelave.CreeperHeal.utils.CreeperUtils;

/**
 * Public API interface.
 * 
 * @author nitnelave
 * 
 */
public abstract class CreeperHandler
{

    /**
     * Record all the blocks in the list as for an explosion.
     * 
     * @param list
     *            The list of blocks.
     */
    public static void recordBlocks(List<Block> list)
    {
        recordBlocks(list, list.get(0).getLocation());
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
    public static void recordBlocks(List<Block> list, Location location)
    {
        ExplodedBlockManager.processExplosion(list, location,
                                              CHExplosionRecordEvent.ExplosionReason.OTHER);
    }

    /**
     * Record all the blocks in the list as for an explosion. The location of
     * the explosion is provided, as well as the reason for the explosion.
     * 
     * @param list
     *            The list of blocks.
     * @param location
     *            The location of the explosion.
     * @param reason
     *            The cause of the explosion.
     */
    public static void
        recordBlocks(List<Block> list, Location location,
                     CHExplosionRecordEvent.ExplosionReason reason)
    {
        ExplodedBlockManager.processExplosion(list, location, reason);
    }

    /**
     * Record the explosion for an event.
     * 
     * @param event
     *            The event corresponding to an explosion.
     */
    public static void recordBlocks(EntityExplodeEvent event)
    {
        ExplodedBlockManager.processExplosion(event, CreeperUtils.getReason(event.getEntity()));
    }

    /**
     * Record the explosion for an event, but specify the reason.
     * 
     * @param event
     *            The event corresponding to an explosion.
     * @param reason
     *            The cause of the explosion.
     */
    public static void
        recordBlocks(EntityExplodeEvent event,
                     CHExplosionRecordEvent.ExplosionReason reason)
    {
        ExplodedBlockManager.processExplosion(event, reason);
    }

    /**
     * Record a block to be replaced later, and remove it from the world. The
     * surrounding blocks, if dependent on it, are taken care of.
     * 
     * @param block
     *            The block to record.
     */
    public static void recordBlock(Block block)
    {
        BurntBlockManager.recordBurntBlock(block);
    }

    /**
     * Get whether the explosion created by this entity should remove protection
     * of blocks protected by LWC.
     * 
     * @param entity
     *            The source of the explosion.
     * @return Whether the protection should be removed.
     */
    public static boolean shouldRemoveLWCProtection(Entity entity)
    {
        return !CreeperConfig.getWorld(entity.getWorld()).shouldReplace(entity);
    }

}

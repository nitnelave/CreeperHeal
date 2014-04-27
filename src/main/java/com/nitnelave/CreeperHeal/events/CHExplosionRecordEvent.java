package com.nitnelave.CreeperHeal.events;

import com.google.common.collect.ImmutableList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;


/**
 * CHExplosionRecordEvent is fired as soon as CreeperHeal starts processing an
 * EntityExplodeEvent, listening allows you to manipulate the blocks which will
 * be or healed.
 **/
public class CHExplosionRecordEvent extends Event implements Cancellable
{
    private boolean cancelled = false;
    private static final HandlerList handlers = new HandlerList();
    private List<Block> healBlocks;
    private final Location location;
    private final ExplosionReason reason;

    public CHExplosionRecordEvent(List<Block> healBlocks, Location location, ExplosionReason reason)
    {
        this.healBlocks = healBlocks;
        this.location = location;
        this.reason = reason;
    }

    @Override
    public boolean isCancelled()
    {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled)
    {
        this.cancelled = cancelled;
    }

    @Override
    public HandlerList getHandlers()
    {
        return handlers;
    }

    public static HandlerList getHandlerList()
    {
        return handlers;
    }
    
    /** 
     * Now Deprecated due to ambiguity. 
     * Behavior has NOT been maintained, but now works as originally expected.
     **/
    @Deprecated
    public List<Block> getBlocks()
    { 
        return healBlocks;
    }
    
    /** 
     * Deprecated due to changed behavior. Now acts as it originally should have.
     **/
    @Deprecated
    public void setBlocks(List<Block> blockList)
    { 
        healBlocks = blockList;
    }
    
    /**
     * Blocks removed from this list will explode naturally if they are inside the
     * {@link getExplosionBlocks} {@.
     * 
     * Adding blocks to this list will mark them for deletion (if normally exploded
     * (config dependent)), then healed when appropriate.
     * 
     * Recommended to use protectBlock and naturalizeBlock for readability.
     * 
     * @return A mutable list of blocks that are currently listed for CreeperHeal
     * processing (not guaranteed to heal).
     **/
    public List<Block> getHealBlocks()
    {
        return healBlocks;
    }
    
    /**
     * @return The location of the entity causing the explosion.
     */
    public Location getLocation()
    {
        return location;
    }

    public ExplosionReason getReason()
    {
        return reason;
    }
    
    /**
     * Adds a block to the explosion, and adds it to the list for CreeperHeal to
     * process.
     */
    public void healBlock(Block block)
    {
        if(!healBlocks.contains(block))
            healBlocks.add(block);
    }
    
    /** 
     * Prevents CreeperHeal processing the block, letting minecraft/bukkit/plugins
     * deal with the block as appropriate.
     */
    public void explodeBlock(Block block)
    {
        if(healBlocks.contains(block))
            healBlocks.remove(block);
    }

    public enum ExplosionReason
    {
        CREEPER,
        TNT,
        DRAGON,
        OTHER,
        GHAST
    }

}

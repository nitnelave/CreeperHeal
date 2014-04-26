package com.nitnelave.CreeperHeal.events;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;


/**
 * CHExplosionRecordEvent is fired as soon as creeperheal starts processing an
 * EntityExplodeEvent.
 **/
public class CHExplosionRecordEvent extends Event implements Cancellable
{
    private boolean cancelled = false;
    private static final HandlerList handlers = new HandlerList();
    private List<Block> explosionBlocks;
    private List<Block> healBlocks;
    private final Location location;
    private final ExplosionReason reason;

    public CHExplosionRecordEvent(List<Block> explosionBlocks, List<Block> healBlocks, Location location, ExplosionReason reason)
    {
        this.explosionBlocks = explosionBlocks;
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
     @return a mutable list of blocks from the original explosion
     * removing blocks from this list will remove them from the explosion
     * protecting them.
     * 
     * Adding blocks to this list will include them in the explosion.
     * Recommended to use protectBlock and naturalizeBlock for readability.
     * 
     **/
    public List<Block> getExplosionBlocks()
    {
        return explosionBlocks;
    }

    /** Now Deprecated due to ambiguity. 
     * Behavior has NOT been maintained, but now works as original expected.
     **/
    @Deprecated
    public List<Block> getBlocks()
    { 
        return healBlocks;
    }
    
    /**
     @return A mutable list of blocks that are currently listed for CreeperHeal
     * processing (not guaranteed to heal).
     * 
     * Blocks removed from this list will explode naturally and filter through
     * the explosion event.
     * 
     * Adding blocks to this list will mark them for deletion (if normally exploded
     * (config dependent)), then healed when appropriate.
     * 
     * Recommended to use protectBlock and naturalizeBlock for readability.
     * 
     **/
    public List<Block> getHealBlocks()
    {
        return healBlocks;
    }
    
    public Location getLocation()
    {
        return location;
    }

    public ExplosionReason getReason()
    {
        return reason;
    }
    
    /** removes a block from the explosion protecting it, also prevents creeperheal
     * processing the block.
     **/
    public void protectBlock(Block block)
    {
        explosionBlocks.remove(block);
        healBlocks.remove(block);
    }
    
    /** prevents CreeperHeal processing the block, letting minecraft/bukkit/plugins
     * deal with the block as appropriate.
     * @param block 
     */
    public void naturalizeBlock(Block block)
    {
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

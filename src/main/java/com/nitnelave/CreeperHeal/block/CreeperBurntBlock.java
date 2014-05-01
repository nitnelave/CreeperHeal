package com.nitnelave.CreeperHeal.block;

import java.util.Date;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;

import com.nitnelave.CreeperHeal.config.CfgVal;
import com.nitnelave.CreeperHeal.config.CreeperConfig;
import com.nitnelave.CreeperHeal.events.CHBlockHealEvent;
import com.nitnelave.CreeperHeal.events.CHBlockHealEvent.CHBlockHealReason;

/**
 * This class represents a burnt block.
 * 
 * @author nitnelave
 * 
 */
public class CreeperBurntBlock
{
    private final Replaceable block;
    private final ReplacementTimer timer;
    private boolean replaced = false;

    /**
     * Constructor.
     * 
     * @param now
     *            The date the block has burnt.
     * @param block
     *            The burnt block.
     */
    public CreeperBurntBlock(Date now, Replaceable block)
    {
        this.block = block;
        boolean timed = block == null ? false : CreeperConfig.getWorld(getWorld()).isRepairTimed();
        timer = new ReplacementTimer(new Date(now.getTime() + 1000
                                              * CreeperConfig.getInt(CfgVal.WAIT_BEFORE_HEAL_BURNT)), timed);
    }

    /**
     * Constructor.
     * 
     * @param now
     *            The date at which the block burnt.
     * @param state
     *            The blockState to be represented.
     */
    public CreeperBurntBlock(Date now, BlockState state)
    {
        this(now, CreeperBlock.newBlock(state));
    }

    /**
     * Replace the block.
     * 
     * @param shouldDrop
     *            If true, in case of failure, the replacement isn't postponed,
     *            but the block is dropped.
     * @return False if the replacement was postponed.
     */
    public boolean replace(boolean shouldDrop)
    {
        CHBlockHealEvent event = new CHBlockHealEvent(block, shouldDrop, CHBlockHealReason.BURNT);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled())
            return event.shouldDrop();
        return block.replace(event.shouldDrop());
    }

    /**
     * Get the Replaceable represented.
     * 
     * @return The Replaceable represented.
     */
    public Replaceable getBlock()
    {
        return block;
    }

    /**
     * Get the block's world.
     * 
     * @return The block's world.
     * @throws NullPointerException
     *             If the block is invalid.
     */
    public World getWorld() throws NullPointerException
    {
        return block.getWorld();
    }

    /**
     * Get the block's type id.
     * 
     * @return The block's type id.
     * @throws NullPointerException
     *             If the block is invalid.
     */
    public int getTypeId() throws NullPointerException
    {
        return block.getTypeId();
    }

    /**
     * Get the block's attaching face.
     * 
     * @return The block's attaching face, or BlockFace.SELF if the block is
     *         invalid.
     */
    public BlockFace getAttachingFace()
    {
        if (block != null)
            return block.getAttachingFace();
        return BlockFace.SELF;
    }

    /**
     * Get the block's location.
     * 
     * @return The block's location.
     * @throws NullPointerException
     *             If the block is invalid.
     */
    public Location getLocation() throws NullPointerException
    {
        return block.getLocation();
    }

    /**
     * Remove the block from the world.
     */
    public void remove()
    {
        block.remove();
    }

    /**
     * Postpone the replacement of the block.
     * 
     * @param delay
     *            The amount of time to postpone by, in sec.
     * @return True if the block was postponed, false otherwise.
     */
    public boolean postPone(int delay)
    {
        if (!timer.postPone(delay))
        {
            block.drop(true);
            return false;
        }
        return true;
    }

    /**
     * Get the time after which the block should be replaced.
     * 
     * @return The time.
     */
    public Date getTime()
    {
        return timer.getTime();
    }

    /**
     * Check if the block should be replaced, and replace it.
     * 
     * @return False if it is not time to replace it yet.
     */
    public boolean checkReplace()
    {
        if (timer.checkReplace())
        {
            if (replace(false))
                replaced = true;
            else
                postPone(CreeperConfig.getInt(CfgVal.WAIT_BEFORE_HEAL));
            return true;
        }
        return false;
    }

    /**
     * Check if the block was already replaced.
     * 
     * @return True if the block was already replaced.
     */
    public boolean wasReplaced()
    {
        return replaced;
    }

}

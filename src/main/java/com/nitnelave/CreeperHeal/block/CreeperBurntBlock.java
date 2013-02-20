package com.nitnelave.CreeperHeal.block;

import java.util.Date;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;

import com.nitnelave.CreeperHeal.config.CreeperConfig;

/**
 * This class represents a burnt block.
 * 
 * @author nitnelave
 * 
 */
public class CreeperBurntBlock {
    private Date time;
    private final Replaceable block;
    private final boolean timed;

    /**
     * Constructor.
     * 
     * @param now
     *            The date the block has burnt.
     * @param block
     *            The burnt block.
     */
    public CreeperBurntBlock (Date now, Replaceable block) {
        this.block = block;
        time = new Date (now.getTime () + 1000 * CreeperConfig.waitBeforeHealBurnt);
        timed = block == null ? false : CreeperConfig.loadWorld (getWorld ()).isRepairTimed ();
    }

    public CreeperBurntBlock (Date now, BlockState state) {
        this (now, CreeperBlock.newBlock (state));
    }

    /**
     * Postpone the block's replacement.
     * 
     * @param delay
     *            The amount of time to postpone by, in milliseconds.
     */
    public void postPone (int delay) {
        time = new Date (time.getTime () + delay);
    }

    /**
     * Get the recorded time.
     * 
     * @return The recorded time. Either the time the block was burnt or later
     *         if the replacement has been delayed.
     */
    public Date getTime () {
        return time;
    }

    public boolean replace (boolean shouldDrop) {
        return block.replace (shouldDrop);
    }

    public Replaceable getBlock () {
        return block;
    }

    public World getWorld () {
        return block.getWorld ();
    }

    public int getTypeId () {
        return block.getTypeId ();
    }

    public BlockFace getAttachingFace () {
        if (block != null)
            return block.getAttachingFace ();
        return BlockFace.SELF;
    }

    public Location getLocation () {
        return block.getLocation ();
    }

    public void remove () {
        block.remove ();
    }

    /**
     * Check if the block should be repaired, and repair it.
     * 
     * @return False if it is not time to replace it yet.
     */
    public boolean checkReplace () {
        if (timed)
            return true;
        if (time.before (new Date ()))
        {
            replace (false);
            return true;
        }
        return false;
    }

}

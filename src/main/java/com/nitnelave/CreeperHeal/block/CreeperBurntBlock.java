package com.nitnelave.CreeperHeal.block;

import java.util.Date;

import org.bukkit.block.BlockState;

/**
 * This class represents a burnt block.
 * 
 * @author nitnelave
 * 
 */
public class CreeperBurntBlock extends CreeperBlock {
    private Date time;

    /**
     * Constructor.
     * 
     * @param now
     *            The date the block has burnt.
     * @param block
     *            The burnt block.
     */
    public CreeperBurntBlock (Date now, BlockState block) {
        super (block);
        time = now;
    }

    /**
     * Postpone the block's replacement.
     * 
     * @param delay
     *            The amount of time to postpone by, in milliseconds.
     */
    public void addTime (int delay) {
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

}

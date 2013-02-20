package com.nitnelave.CreeperHeal.block;

import java.util.Date;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;

/**
 * This class represents a burnt block.
 * 
 * @author nitnelave
 * 
 */
public class CreeperBurntBlock implements Replaceable {
    private Date time;
    private final CreeperBlock block;

    /**
     * Constructor.
     * 
     * @param now
     *            The date the block has burnt.
     * @param block
     *            The burnt block.
     */
    public CreeperBurntBlock (Date now, BlockState block) {
        this.block = CreeperBlock.newBlock (block);
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

    @Override
    public boolean replace (boolean shouldDrop) {
        return block.replace (shouldDrop);
    }

    @Override
    public Block getBlock () {
        return block.getBlock ();
    }

    @Override
    public World getWorld () {
        return block.getWorld ();
    }

    @Override
    public int getTypeId () {
        return block.getTypeId ();
    }

    @Override
    public BlockFace getAttachingFace () {
        if (block != null)
            return block.getAttachingFace ();
        return BlockFace.SELF;
    }

    @Override
    public Location getLocation () {
        return block.getLocation ();
    }

    @Override
    public boolean isDependent () {
        return block.isDependent ();
    }

    @Override
    public void drop () {
        block.drop ();
    }

    @Override
    public void remove () {
        block.remove ();
    }

}

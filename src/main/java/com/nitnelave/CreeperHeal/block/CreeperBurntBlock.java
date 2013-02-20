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
    public CreeperBurntBlock (Date now, Replaceable block) {
        this.block = block;
        timer = new ReplacementTimer (now, block == null ? false : CreeperConfig.loadWorld (getWorld ()).isRepairTimed ());
    }

    public CreeperBurntBlock (Date now, BlockState state) {
        this (now, CreeperBlock.newBlock (state));
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
     * Postpone the replacement of the block.
     * 
     * @param delay
     *            The amount of time to postpone by, in sec.
     */
    public void postPone (int delay) {
        if (!timer.postPone (delay))
            block.drop ();
    }

    public Date getTime () {
        return timer.getTime ();
    }

    public boolean checkReplace () {
        if (timer.isTimed ())
            return true;
        if (timer.checkReplace ())
        {
            if (replace (false))
                replaced = true;
            else
                postPone (CreeperConfig.waitBeforeHeal);
            return true;
        }
        return false;
    }

    public boolean wasReplaced () {
        return replaced;
    }

}

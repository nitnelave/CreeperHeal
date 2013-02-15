package com.nitnelave.CreeperHeal.utils;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.material.Attachable;

import com.nitnelave.CreeperHeal.CreeperHeal;
import com.nitnelave.CreeperHeal.block.Replaceable;
import com.nitnelave.CreeperHeal.config.CreeperConfig;

/**
 * This class is a task to replace a CreeperBlock later. If the block cannot be
 * safely replaced, then the replacement is postponed. After a numberof tries,
 * the block is dropped to the ground.
 * 
 * @author nitnelave
 * 
 */
public class DelayReplacement implements Runnable {
    /*
     * The block to be replaced.
     */
    private final Replaceable blockState;
    /*
     * The number of times a replacement has been attempted.
     */
    private int counter;

    /**
     * Constructor for a new task.
     * 
     * @param replaceable
     *            The block to be replaced.
     * @param replaced
     *            The number of times a replacement has already been attempted.
     */
    public DelayReplacement (Replaceable replaceable, int replaced) {
        this.blockState = replaceable;
        this.counter = replaced + 1;
    }

    /*
     * (non-Javadoc) The task attempts to replace the block, and in case of
     * failure either re-schedule the replacement for later or drop the block.
     */
    @Override
    public void run () {
        if (counter < 150)
        {
            if (blockState instanceof Attachable
                    && blockState.getBlock ().getRelative (((Attachable) blockState).getAttachedFace ()).getType () == Material.AIR)
                delay_replacement ();
            else if (blockState.getBlock ().getRelative (BlockFace.DOWN).getType () == Material.AIR)
                delay_replacement ();
            else
                blockState.replace (true);
        }
        else
            blockState.replace (true);

    }

    /**
     * Re-schedule the replacement later.
     */
    private void delay_replacement () {
        counter++;
        Bukkit.getServer ().getScheduler ()
                .scheduleSyncDelayedTask (CreeperHeal.getInstance (), this, (long) Math.ceil ((double) CreeperConfig.blockPerBlockInterval / 20));
    }

}

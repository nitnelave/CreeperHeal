package com.nitnelave.CreeperHeal.block;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.material.Attachable;

import com.nitnelave.CreeperHeal.events.CHBlockHealEvent;
import com.nitnelave.CreeperHeal.events.CHBlockHealEvent.CHBlockHealReason;

/**
 * This class is a task to replace a CreeperBlock later. If the block cannot be
 * safely replaced, then the replacement is postponed. After a numberof tries,
 * the block is dropped to the ground.
 * 
 * @author nitnelave
 * 
 */
public class DelayReplacement implements Runnable
{
    /*
     * The block to be replaced.
     */
    private final Replaceable blockState;
    private final int REPLACEMENT_THRESHOLD = 150;
    private final CHBlockHealReason reason;
    /*
     * The number of times a replacement has been attempted.
     */
    private int counter;
    private int id;

    /**
     * Constructor for a new task.
     * 
     * @param replaceable
     *            The block to be replaced.
     * @param replaced
     *            The number of times a replacement has already been attempted.
     * @param reason
     */
    public DelayReplacement(Replaceable replaceable, int replaced, CHBlockHealReason reason)
    {
        blockState = replaceable;
        counter = replaced + 1;
        this.reason = reason;
    }

    /*
     * (non-Javadoc) The task attempts to replace the block, and in case of
     * failure either re-schedule the replacement for later or drop the block.
     */
    @Override
    public void run()
    {
        if (counter < REPLACEMENT_THRESHOLD)
        {
            if ((blockState instanceof Attachable && blockState.getBlock().getRelative(((Attachable) blockState).getAttachedFace()).getType() == Material.AIR)
                || blockState.getBlock().getRelative(BlockFace.DOWN).getType() == Material.AIR)
                counter++;
            else
            {
                CHBlockHealEvent event = new CHBlockHealEvent(blockState, true, reason);
                Bukkit.getPluginManager().callEvent(event);
                if (!event.isCancelled())
                    blockState.replace(event.shouldDrop());
                Bukkit.getScheduler().cancelTask(id);
            }
        }
        else
        {
            CHBlockHealEvent event = new CHBlockHealEvent(blockState, true, reason);
            Bukkit.getPluginManager().callEvent(event);
            if (!event.isCancelled())
                blockState.replace(event.shouldDrop());
            Bukkit.getScheduler().cancelTask(id);
        }

    }

    public void setId(int id)
    {
        this.id = id;
    }

}

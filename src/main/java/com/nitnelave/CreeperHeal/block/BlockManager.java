package com.nitnelave.CreeperHeal.block;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;

import com.nitnelave.CreeperHeal.CreeperHeal;
import com.nitnelave.CreeperHeal.config.CreeperConfig;
import com.nitnelave.CreeperHeal.config.WorldConfig;
import com.nitnelave.CreeperHeal.utils.CreeperLog;

/**
 * Manager to gather block-related methods.
 * 
 * @author nitnelave
 * 
 */
public abstract class BlockManager {

    /*
     * Block to be replaced immediately after an explosion.
     */
    private static Map<Location, Replaceable> toReplace = new HashMap<Location, Replaceable> ();

    /*
     * Remember if time repairs have already been scheduled.
     */
    private static boolean timeRepairsScheduled = false;

    /**
     * Add a block to the list of blocks to be replaced immediately.
     * 
     * @param block
     *            The block to add.
     */
    protected static void addToReplace (CreeperBlock block) {
        toReplace.put (block.getLocation (), block);
    }

    /*
     * Replace the blocks that should be immediately replaced after an
     * explosion.
     */
    protected static void replaceProtected () {
        Iterator<Replaceable> iter = toReplace.values ().iterator ();
        while (iter.hasNext ())
            iter.next ().replace (true);
        toReplace.clear ();
    }

    /*
     * For each world, check if it is the time for timed repairs, and repair.
     */
    private static void checkReplaceTime () {
        for (WorldConfig w : CreeperConfig.getWorlds ())
        {
            long time = Bukkit.getServer ().getWorld (w.getName ()).getTime ();
            if (w.isRepairTimed () && ((Math.abs (w.getRepairTime () - time) < 600) || (Math.abs (Math.abs (w.getRepairTime () - time) - 24000)) < 600))
            {
                ExplodedBlockManager.forceReplace (w);
                BurntBlockManager.forceReplaceBurnt (w);
            }
        }
    }

    /**
     * Schedule the timed repair task.
     */
    public static void scheduleTimeRepairs () {
        if (!timeRepairsScheduled)
        {
            timeRepairsScheduled = true;
            Bukkit.getScheduler ().scheduleSyncRepeatingTask (CreeperHeal.getInstance (), new Runnable () {
                @Override
                public void run () {
                    checkReplaceTime ();
                }
            }, 200, 1200);

            CreeperLog.warning ("[CreeperHeal] Impossible to schedule the time-repair task. Time repairs will not work");
        }

    }

}

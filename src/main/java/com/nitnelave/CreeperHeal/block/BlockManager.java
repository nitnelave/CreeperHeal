package com.nitnelave.CreeperHeal.block;

import org.bukkit.Bukkit;

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
     * Remember if time repairs have already been scheduled.
     */
    private static boolean timeRepairsScheduled = false;

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

package com.nitnelave.CreeperHeal.block;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.bukkit.Bukkit;

import com.nitnelave.CreeperHeal.CreeperHeal;
import com.nitnelave.CreeperHeal.config.CfgVal;
import com.nitnelave.CreeperHeal.config.CreeperConfig;

/**
 * Contains and manages the list of rails whose update should be prevented.
 * 
 * @author nitnelave
 * 
 */
public class RailsIndex {

    /*
     * Block whose update should be prevented.
     */
    private static Map<CreeperRail, Date> railsIndex;

    static
    {
        railsIndex = new HashMap<CreeperRail, Date> ();

        Bukkit.getScheduler ().scheduleSyncRepeatingTask (CreeperHeal.getInstance (), new Runnable () {
            @Override
            public void run () {
                cleanUp ();
            }
        }, 500, 7200);
    }

    /**
     * Get whether the location is next to a block whose update is prevented.
     * 
     * @param block
     *            The block to check.
     * @return Whether the location is next to a block whose update is
     *         prevented.
     */
    public static boolean isUpdatePrevented (CreeperBlock block) {
        if (!(block instanceof CreeperRail))
            return false;
        return railsIndex.containsKey (block);
    }

    /**
     * Add the location to the list of blocks that shouldn't be updated. The
     * block's updates are prevented until after 200 times the block per block
     * replacement interval.
     * 
     * @param block
     *            The block.
     */
    public static void putUpdatePrevention (CreeperRail block) {
        if (CreeperConfig.getBool (CfgVal.RAIL_REPLACEMENT))
            railsIndex.put (block, new Date ());
    }

    private static void cleanUp () {
        if (CreeperConfig.getBool (CfgVal.RAIL_REPLACEMENT))
        {
            Date delay = new Date (new Date ().getTime () - 200 * CreeperConfig.getInt (CfgVal.BLOCK_PER_BLOCK_INTERVAL));
            Iterator<Date> iter;
            iter = railsIndex.values ().iterator ();
            while (iter.hasNext ())
            {
                Date date = iter.next ();
                if (date.before (delay))
                    iter.remove ();
                else
                    break;
            }
        }
    }
}

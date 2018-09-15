package com.nitnelave.CreeperHeal.block;

import com.nitnelave.CreeperHeal.CreeperHeal;
import com.nitnelave.CreeperHeal.config.CfgVal;
import com.nitnelave.CreeperHeal.config.CreeperConfig;
import org.bukkit.Bukkit;
import org.bukkit.Tag;
import org.bukkit.block.Block;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Contains and manages the list of rails whose update should be prevented.
 * 
 * @author nitnelave
 * 
 */
public class RailsIndex
{

    /*
     * Block whose update should be prevented.
     */
    private static final Map<Block, Date> railsIndex;

    static
    {
        railsIndex = new HashMap<>();

        Bukkit.getScheduler().scheduleSyncRepeatingTask(CreeperHeal.getInstance(), RailsIndex::cleanUp, 500, 7200);
    }

    /**
     * Get whether the location is next to a block whose update is prevented.
     * 
     * @param block
     *            The block to check.
     * @return Whether the location is next to a block whose update is
     *         prevented.
     */
    public static boolean isUpdatePrevented(Block block)
    {
        if (!Tag.RAILS.isTagged(block.getType()))
            return false;
        return railsIndex.containsKey(block);
    }

    /**
     * Add the location to the list of blocks that shouldn't be updated. The
     * block's updates are prevented until after 200 times the block per block
     * replacement interval.
     * 
     * @param block
     *            The block.
     */
    public static void putUpdatePrevention(Block block)
    {
        if (CreeperConfig.getBool(CfgVal.RAIL_REPLACEMENT))
            railsIndex.put(block, new Date());
    }

    private static void cleanUp()
    {
        if (!CreeperConfig.getBool(CfgVal.RAIL_REPLACEMENT))
            return;

        Date delay = new Date(new Date().getTime() - 200
                * CreeperConfig.getInt(CfgVal.BLOCK_PER_BLOCK_INTERVAL));
        railsIndex.values().removeIf(date -> date.before(delay));
    }
}

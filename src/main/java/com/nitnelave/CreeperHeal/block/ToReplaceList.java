package com.nitnelave.CreeperHeal.block;

import com.nitnelave.CreeperHeal.CreeperHeal;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.HashMap;
import java.util.Map;

/**
 * Class to handle the blocks to be replaced immediately.
 * 
 * @author nitnelave
 * 
 */
abstract class ToReplaceList
{

    /*
     * Block to be replaced immediately after an explosion.
     */
    private static final Map<Location, Replaceable> toReplace = new HashMap<>();

    /**
     * Add a block to the list of blocks to be replaced immediately.
     * 
     * @param block
     *            The block to add.
     */
    static void addToReplace(CreeperBlock block)
    {
        toReplace.put(block.getLocation(), block);
    }

    /**
     * Replace the blocks that should be immediately replaced after an
     * explosion, in a task run one tick later.
     */
    static void replaceProtected()
    {
        Bukkit.getScheduler().scheduleSyncDelayedTask(CreeperHeal.getInstance(), ToReplaceList::replaceAll);
    }

    /*
     * Replace all the blocks.
     */
    private static void replaceAll()
    {
        for (Replaceable replaceable : toReplace.values())
            replaceable.replace(true);
        toReplace.clear();
    }

}

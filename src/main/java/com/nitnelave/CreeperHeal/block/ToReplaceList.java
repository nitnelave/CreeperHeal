package com.nitnelave.CreeperHeal.block;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;

import com.nitnelave.CreeperHeal.CreeperHeal;
import com.nitnelave.CreeperHeal.utils.CreeperLog;

/**
 * Class to handle the blocks to be replaced immediately.
 * 
 * @author nitnelave
 * 
 */
public abstract class ToReplaceList
{

    /*
     * Block to be replaced immediately after an explosion.
     */
    private static Map<Location, Replaceable> toReplace = new HashMap<Location, Replaceable>();

    /**
     * Add a block to the list of blocks to be replaced immediately.
     * 
     * @param block
     *            The block to add.
     */
    protected static void addToReplace(CreeperBlock block)
    {
    	CreeperLog.debug("Added to replace List :" + block.getBlock().getType().name());
        toReplace.put(block.getLocation(), block);        
    }

    /**
     * Replace the blocks that should be immediately replaced after an
     * explosion, in a task run one tick later.
     */
    protected static void replaceProtected()
    {
        Bukkit.getScheduler().scheduleSyncDelayedTask(CreeperHeal.getInstance(), new Runnable()
        {
            @Override
            public void run()
            {
                replaceAll();
            }
        });
    }

    /*
     * Replace all the blocks.
     */
    private static void replaceAll()
    {
        Iterator<Replaceable> iter = toReplace.values().iterator();
        while (iter.hasNext())
            iter.next().replace(true);
        toReplace.clear();
    }

}

package com.nitnelave.CreeperHeal.utils;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.block.Block;

import com.massivecraft.factions.entity.BoardColls;
import com.massivecraft.mcore.ps.PS;
import com.nitnelave.CreeperHeal.PluginHandler;
import com.nitnelave.CreeperHeal.config.WCfgVal;
import com.nitnelave.CreeperHeal.config.WorldConfig;

/**
 * A handler for the Factions plugin.
 * 
 * @author nitnelave
 * 
 */
public abstract class FactionHandler
{

    private static boolean isFactionsEnabled = false;

    static
    {
        setFactionsEnabled(PluginHandler.isFactionsEnabled());
    }

    /**
     * Set whether the Factions plugin is enabled.
     * 
     * @param enabled
     *            Whether it is enabled.
     */
    public static void setFactionsEnabled(boolean enabled)
    {
        isFactionsEnabled = enabled;
    }

    /**
     * Check if an explosion should be ignored (blocks not replaced).
     * 
     * @param list
     *            The list of exploded blocks.
     * @param world
     *            The CH configuration for the world.
     * @return Whether the explosion should be ignored.
     */
    public static boolean shouldIgnore(List<Block> list, WorldConfig world)
    {
        if (!isFactionsEnabled)
            return false;

        boolean wild = world.getBool(WCfgVal.FACTIONS_IGNORE_WILDERNESS), territory = world.getBool(WCfgVal.FACTIONS_IGNORE_TERRITORY);
        if (wild == territory)
            return wild;

        for (Block block : list)
            if (wild != BoardColls.get().getFactionAt(PS.valueOf(block.getLocation())).isNone())
                return false;
        return true;
    }

    /**
     * Check if a block should be ignored (not replaced).
     * 
     * @param block
     *            The block to be checked.
     * @param world
     *            The CH configuration for the world.
     * @return Whether the replacement should be ignored.
     */
    public static boolean shouldIgnore(Block block, WorldConfig world)
    {
        ArrayList<Block> l = new ArrayList<Block>();
        l.add(block);
        return shouldIgnore(l, world);
    }

}

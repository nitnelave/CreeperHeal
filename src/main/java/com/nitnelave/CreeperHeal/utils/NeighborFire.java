package com.nitnelave.CreeperHeal.utils;

import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;

import org.bukkit.Location;
import org.bukkit.World;

import com.nitnelave.CreeperHeal.block.CreeperBurntBlock;
import com.nitnelave.CreeperHeal.config.CfgVal;
import com.nitnelave.CreeperHeal.config.CreeperConfig;

/**
 * Implementation of the NeighborFinder for burnt blocks.
 * 
 * @see com.nitnelave.CreeperHeal.utils.NeighborFinder
 * @see com.nitnelave.CreeperHeal.block.CreeperBurntBlock
 * @author nitnelave
 * 
 */
public class NeighborFire extends NeighborFinder<CreeperBurntBlock>
{

    private static final int DISTANCE_NEAR = 10;

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.nitnelave.CreeperHeal.utils.NeighborFinder#getNeighbor(org.bukkit
     * .Location, java.util.LinkedList)
     */
    @Override
    protected CreeperBurntBlock getNeighbor(Location loc, LinkedList<CreeperBurntBlock> list)
    {
        if (list != null)
        {
            World w = loc.getWorld();
            for (CreeperBurntBlock cB : list)
                if (cB.getWorld() == w && loc.distance(cB.getLocation()) < DISTANCE_NEAR)
                    return cB;
        }
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.nitnelave.CreeperHeal.utils.NeighborFinder#clean()
     */
    @Override
    public void clean()
    {
        Iterator<LinkedList<CreeperBurntBlock>> iter = map.values().iterator();
        Date delay = new Date(new Date().getTime() - 1000
                              * CreeperConfig.getInt(CfgVal.WAIT_BEFORE_HEAL_BURNT) + 4000000
                              * CreeperConfig.getInt(CfgVal.BLOCK_PER_BLOCK_INTERVAL));
        while (iter.hasNext())
        {
            LinkedList<CreeperBurntBlock> list = iter.next();
            Iterator<CreeperBurntBlock> it = list.iterator();
            while (it.hasNext())
            {
                Date date = it.next().getTime();
                if (date.before(delay))
                    it.remove();
                else
                    break;
            }
            if (list.isEmpty())
                iter.remove();
        }
    }

    /**
     * Removes a block from the index.
     * 
     * @param block
     *            The block to be removed.
     */
    public void removeElement(CreeperBurntBlock block)
    {
        Location l = block.getLocation();
        removeElement(block, l.getBlockX(), l.getBlockZ());
    }

    /**
     * Adds a block to the index.
     * 
     * @param block
     *            The block to add.
     */
    public void addElement(CreeperBurntBlock block)
    {
        Location l = block.getLocation();
        addElement(block, l.getBlockX(), l.getBlockZ());
    }

}

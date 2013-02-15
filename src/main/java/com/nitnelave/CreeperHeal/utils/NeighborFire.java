package com.nitnelave.CreeperHeal.utils;

import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;

import org.bukkit.Location;
import org.bukkit.World;

import com.nitnelave.CreeperHeal.block.CreeperBurntBlock;
import com.nitnelave.CreeperHeal.config.CreeperConfig;

/**
 * Implementation of the NeighborFinder for burnt blocks.
 * 
 * @see com.nitnelave.CreeperHeal.utils.NeighborFinder
 * @see com.nitnelave.CreeperHeal.block.CreeperBurntBlock
 * @author nitnelave
 * 
 */
public class NeighborFire extends NeighborFinder<CreeperBurntBlock> {

    /*
     * (non-Javadoc)
     * @see
     * com.nitnelave.CreeperHeal.utils.NeighborFinder#hasNeighbor(org.bukkit
     * .Location, java.util.LinkedList)
     */
    @Override
    protected boolean hasNeighbor (Location loc, LinkedList<CreeperBurntBlock> list) {
        if (list != null)
        {
            World w = loc.getWorld ();
            for (CreeperBurntBlock cB : list)
                if (cB.getWorld () == w && loc.distance (cB.getLocation ()) < 10)
                    return true;
        }
        return false;
    }

    /*
     * (non-Javadoc)
     * @see com.nitnelave.CreeperHeal.utils.NeighborFinder#clean()
     */
    @Override
    public void clean () {
        Iterator<LinkedList<CreeperBurntBlock>> iter = map.values ().iterator ();
        Date delay = new Date (new Date ().getTime () - 1000 * CreeperConfig.waitBeforeHealBurnt);
        while (iter.hasNext ())
        {
            LinkedList<CreeperBurntBlock> list = iter.next ();
            Iterator<CreeperBurntBlock> it = list.iterator ();
            while (it.hasNext ())
            {
                Date date = it.next ().getTime ();
                if (date.before (delay))
                    it.remove ();
                else
                    break;
            }
            if (list.isEmpty ())
                iter.remove ();
        }
    }

}

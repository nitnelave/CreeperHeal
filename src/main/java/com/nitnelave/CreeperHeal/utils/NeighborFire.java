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
     * com.nitnelave.CreeperHeal.utils.NeighborFinder#getNeighbor(org.bukkit
     * .Location, java.util.LinkedList)
     */
    @Override
    protected CreeperBurntBlock getNeighbor (Location loc, LinkedList<CreeperBurntBlock> list) {
        if (list != null)
        {
            World w = loc.getWorld ();
            for (CreeperBurntBlock cB : list)
                if (cB.getWorld () == w && loc.distance (cB.getLocation ()) < 10)
                    return cB;
        }
        return null;
    }

    /*
     * (non-Javadoc)
     * @see com.nitnelave.CreeperHeal.utils.NeighborFinder#clean()
     */
    @Override
    public void clean () {
        Iterator<LinkedList<CreeperBurntBlock>> iter = map.values ().iterator ();
        Date delay = new Date (new Date ().getTime () - 1000 * CreeperConfig.waitBeforeHealBurnt + 4000000 * CreeperConfig.blockPerBlockInterval);
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

    public void removeElement (CreeperBurntBlock cBlock) {
        Location l = cBlock.getLocation ();
        removeElement (cBlock, l.getBlockX (), l.getBlockZ ());
    }

    public void addElement (CreeperBurntBlock b) {
        Location l = b.getLocation ();
        addElement (b, l.getBlockX (), l.getBlockZ ());
    }

}

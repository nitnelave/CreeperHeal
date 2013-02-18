package com.nitnelave.CreeperHeal.utils;

import java.util.Iterator;
import java.util.LinkedList;

import org.bukkit.Location;
import org.bukkit.World;

import com.nitnelave.CreeperHeal.block.CreeperExplosion;

/**
 * Implementation of the NeighborFinder for explosions.
 * 
 * @see com.nitnelave.CreeperHeal.utils.NeighborFinder
 * @see com.nitnelave.CreeperHeal.block.CreeperExplosion
 * @author nitnelave
 * 
 */
public class NeighborExplosion extends NeighborFinder<CreeperExplosion> {

    /*
     * (non-Javadoc)
     * @see
     * com.nitnelave.CreeperHeal.utils.NeighborFinder#hasNeighbor(org.bukkit
     * .Location, java.util.LinkedList)
     */
    @Override
    protected boolean hasNeighbor (Location loc, LinkedList<CreeperExplosion> list) {
        if (list != null)
        {
            World w = loc.getWorld ();
            for (CreeperExplosion cEx : list)
            {
                Location l = cEx.getLocation ();
                if (l.getWorld () == w && loc.distance (cEx.getLocation ()) < cEx.getRadius ())
                    return true;
            }
        }
        return false;
    }

    /*
     * (non-Javadoc)
     * @see com.nitnelave.CreeperHeal.utils.NeighborFinder#clean()
     */
    @Override
    public void clean () {
        Iterator<LinkedList<CreeperExplosion>> iter = map.values ().iterator ();
        while (iter.hasNext ())
        {
            LinkedList<CreeperExplosion> list = iter.next ();
            Iterator<CreeperExplosion> it = list.iterator ();
            while (it.hasNext ())
            {
                CreeperExplosion e = it.next ();
                if (e.isEmpty ())
                    it.remove ();
            }
            if (list.isEmpty ())
                iter.remove ();
        }
    }

    /**
     * Remove an CreeperExplosion.
     * 
     * @param ex
     *            The explosion to remove.
     */
    public void removeElement (CreeperExplosion ex) {
        Location l = ex.getLocation ();
        removeElement (ex, l.getX (), l.getZ ());
    }

}

package com.nitnelave.CreeperHeal.utils;

import com.nitnelave.CreeperHeal.block.CreeperExplosion;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.ArrayList;

/**
 * Implementation of the NeighborFinder for explosions.
 * 
 * @see com.nitnelave.CreeperHeal.utils.NeighborFinder
 * @see com.nitnelave.CreeperHeal.block.CreeperExplosion
 * @author nitnelave
 * 
 */
public class NeighborExplosion extends NeighborFinder<CreeperExplosion>
{

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.nitnelave.CreeperHeal.utils.NeighborFinder#getNeighbor(org.bukkit
     * .Location, java.util.LinkedList)
     */
    @Override
    protected CreeperExplosion getNeighbor(Location loc, ArrayList<CreeperExplosion> list)
    {
        if (list != null)
        {
            World w = loc.getWorld();
            for (CreeperExplosion ex : list)
            {
                Location l = ex.getLocation();
                if (l.getWorld() == w && loc.distance(ex.getLocation()) < ex.getRadius())
                    return ex;
            }
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
        map.values().removeIf(list ->
        {
            list.removeIf(CreeperExplosion::isEmpty);
            return list.isEmpty();
        });
    }

    /**
     * Remove an CreeperExplosion.
     * 
     * @param ex
     *            The explosion to remove.
     */
    public void removeElement(CreeperExplosion ex)
    {
        Location l = ex.getLocation();
        removeElement(ex, l.getX(), l.getZ());
    }

}

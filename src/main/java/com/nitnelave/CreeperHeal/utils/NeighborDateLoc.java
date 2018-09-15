package com.nitnelave.CreeperHeal.utils;

import com.nitnelave.CreeperHeal.config.CfgVal;
import com.nitnelave.CreeperHeal.config.CreeperConfig;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.Date;

/**
 * Implementation of the NeighborFinder for DateLoc.
 * 
 * @author nitnelave
 */
public class NeighborDateLoc extends NeighborFinder<DateLoc>
{

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.nitnelave.CreeperHeal.utils.NeighborFinder#getNeighbor(org.bukkit
     * .Location, java.util.LinkedList)
     */
    @Override
    protected DateLoc getNeighbor(Location loc, ArrayList<DateLoc> list)
    {
        if (list != null)
        {
            World w = loc.getWorld();
            for (DateLoc dl : list)
                if (dl.getWorld() == w && loc.distance(dl.getLocation()) < 10)
                    return dl;
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
        Date delay = new Date(new Date().getTime() - 200
                              * CreeperConfig.getInt(CfgVal.BLOCK_PER_BLOCK_INTERVAL));
        map.values().removeIf(list ->{
            list.removeIf(dateLoc -> dateLoc.getTime().before(delay));
            return list.isEmpty();
        });
    }

}

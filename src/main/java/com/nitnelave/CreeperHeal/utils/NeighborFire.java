package com.nitnelave.CreeperHeal.utils;

import com.nitnelave.CreeperHeal.block.CreeperBurntBlock;
import com.nitnelave.CreeperHeal.config.CfgVal;
import com.nitnelave.CreeperHeal.config.CreeperConfig;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.Date;

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
    protected CreeperBurntBlock getNeighbor(Location loc, ArrayList<CreeperBurntBlock> list)
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
        Date delay = new Date(new Date().getTime() - 1000
                * CreeperConfig.getInt(CfgVal.WAIT_BEFORE_HEAL_BURNT) + 4000000
                * CreeperConfig.getInt(CfgVal.BLOCK_PER_BLOCK_INTERVAL));
        map.values().removeIf(list ->
        {
            list.removeIf(dateLoc -> dateLoc.getTime().before(delay));
            return list.isEmpty();
        });
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

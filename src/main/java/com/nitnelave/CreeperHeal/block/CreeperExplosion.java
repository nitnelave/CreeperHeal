package com.nitnelave.CreeperHeal.block;

import java.util.Date;
import java.util.List;

import org.bukkit.Location;

import com.nitnelave.CreeperHeal.config.CreeperConfig;

/**
 * Represents an explosion, with the list of blocks destroyed, the time of the
 * explosion, and the radius.
 * 
 * @author nitnelave
 * 
 */
public class CreeperExplosion
{
    private final Date time;
    private final List<Replaceable> blockList;
    private final Location loc;
    private final double radius;

    /**
     * Constructor.
     * 
     * @param time
     *            The time of the explosion.
     * @param blockList
     *            The list of destroyed blocks.
     * @param loc
     *            The location of the explosion.
     * @param radius
     *            The radius of the explosion.
     */
    public CreeperExplosion (Date time, List<Replaceable> blockList, Location loc)
    {
        this.time = time;
        this.blockList = blockList;
        this.loc = loc;
        radius = computeRadius (blockList, loc);
    }

    /**
     * Get the list of blocks destroyed still to be replaced.
     * 
     * @return The list of blocks still to be replaced.
     */
    public List<Replaceable> getBlockList () {
        return blockList;
    }

    /**
     * Get the time of the explosion.
     * 
     * @return The time of the explosion.
     */
    public Date getTime()
    {
        return time;
    }

    /*
     * Get the distance between the explosion's location and the furthest block.
     */
    private static double computeRadius (List<Replaceable> list, Location loc) {
        double r = 0;
        for (Replaceable b : list)
        {
            Location bl = b.getBlock ().getLocation ();
            r = Math.max (r, loc.distance (bl));
        }
        return r + 1;
    }

    /**
     * Get the location of the explosion.
     * 
     * @return The location of the explosion.
     */
    public Location getLocation()
    {
        return loc;
    }


    /**
     * Get the radius of the explosion (i.e. the distance between the location
     * and the furthest block).
     * 
     * @return The radius of the explosion.
     */
    public double getRadius() {
        return radius;
    }

    /*
     * Replace all the blocks in the list.
     */
    protected void replace_blocks () {
        for (Replaceable block : blockList)
            block.replace (true);
        blockList.clear ();

        if (CreeperConfig.teleportOnSuffocate)
            BlockManager.check_player_one_block (loc);
    }

    /**
     * Replace the first block of the list.
     * 
     * @return False if the list is now empty.
     */
    protected boolean replace_one_block () {
        blockList.get (0).replace (false);
        BlockManager.check_player_one_block (blockList.get (0).getBlock ().getLocation ());
        blockList.remove (0);
        return !blockList.isEmpty ();
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals (Object o) {
        if (!(o instanceof CreeperExplosion))
            return false;
        CreeperExplosion e = (CreeperExplosion) o;
        return e.time == time && e.loc == loc && e.radius == radius;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode ()
    {
        return (int) (time.hashCode() + radius + loc.hashCode());
    }


}

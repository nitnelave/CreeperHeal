package com.nitnelave.CreeperHeal.utils;

import org.bukkit.Location;
import org.bukkit.block.Block;

/**
 * Represent the minimum values (three integer coordinate) needed to represent a
 * block. Useful for HashSets to check for presence.
 * 
 * @author nitnelave
 * 
 */
public class ShortLocation {
    private final int x, y, z, hashCode;

    /**
     * Constructor.
     * 
     * @param l
     *            The location represented.
     */
    public ShortLocation(Location l) {
        x = l.getBlockX();
        y = l.getBlockY();
        z = l.getBlockZ();
        hashCode = 31 * x + 37 * y + 41 * z;
    }

    /**
     * Constructor.
     * 
     * @param block
     *            The block whose location is represented.
     */
    public ShortLocation(Block block) {
        this(block.getLocation());
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return hashCode;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj instanceof ShortLocation)
        {
            ShortLocation other = (ShortLocation) obj;
            if (x == other.x && y == other.y && z == other.z)
                return true;
        }
        return false;
    }

}

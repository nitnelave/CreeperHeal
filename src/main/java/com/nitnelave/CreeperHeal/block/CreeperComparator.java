package com.nitnelave.CreeperHeal.block;

import java.util.Comparator;

import org.bukkit.Location;

import com.nitnelave.CreeperHeal.config.CreeperConfig;

/**
 * Comparator to sort Replaceable in an explosion. Dependent blocks are put at
 * the end of the list, so they are replaced after the block they are dependent
 * upon. Otherwise, altitude is the only criterion.
 * 
 * @author nitnelave
 * 
 */
public class CreeperComparator implements Comparator<Replaceable> {

    private final Location loc;

    /**
     * Constructor. Order between blocks with the same dependency status and at
     * the same level is undefined.
     */
    public CreeperComparator () {
        loc = null;
    }

    /**
     * Constructor. The blocks are ordered as a last criterion by distance fron
     * the explosion, the closest first.
     * 
     * @param loc
     *            The center of the explosion.
     */
    public CreeperComparator (Location loc) {
        this.loc = loc;
    }

    @Override
    public int compare (Replaceable b1, Replaceable b2) {
        boolean d1 = b1.isDependent (), d2 = b2.isDependent ();
        if (d1 && !d2)
            return 1;
        else if (d2 && !d1)
            return -1;

        boolean p1 = b1.isDependent (), p2 = b2.isDependent ();
        if (p1 && !p2)
            return 1;
        else if (p2 && !p1)
            return -1;

        int pos1 = b1.getLocation ().getBlockY (), pos2 = b2.getLocation ().getBlockY ();
        if (pos1 > pos2)
            return 1;
        else if (pos1 < pos2)
            return -1;
        if (loc == null || CreeperConfig.lightweightMode)
            return 0;
        if (b1.getLocation ().distance (loc) < b2.getLocation ().distance (loc))
            return 1;
        return -1;
    }

}

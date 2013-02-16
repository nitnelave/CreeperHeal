package com.nitnelave.CreeperHeal.block;

import java.util.Comparator;


/**
 * Comparator to sort Replaceable in an explosion. Dependent blocks are put at
 * the end of the list, so they are replaced after the block they are dependent
 * upon. Otherwise, altitude is the only criterion.
 * 
 * @author nitnelave
 * 
 */
public class CreeperComparator implements Comparator<Replaceable> {

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
        else
            return 0;
    }

}

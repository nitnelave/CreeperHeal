package com.nitnelave.CreeperHeal.utils;

import java.util.Comparator;

import com.nitnelave.CreeperHeal.block.Replaceable;

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
        boolean c1 = b1.isDependent (), c2 = b2.isDependent ();
        if (c1 && !c2)
            return 1;
        else if (c2 && !c1)
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

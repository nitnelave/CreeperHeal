package com.nitnelave.CreeperHeal.block;

import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.bukkit.entity.Hanging;

import com.nitnelave.CreeperHeal.config.CreeperConfig;

/**
 * Manager for hangings.
 * 
 * @author nitnelave
 * 
 */
public abstract class HangingsManager {

    private static List<CreeperHanging> hangings = Collections.synchronizedList (new LinkedList<CreeperHanging> ()); //paintings to be replaced

    //TODO: This class shouldn't be needed, but paintings should be replaced as normal blocks.
    /**
     * Replace all the hangings destroyed since time for the burnt paintings, or
     * around time for the exploded ones.
     * 
     * @param time
     *            The time.
     */
    public static void replaceHangings (Date time) {
        Iterator<CreeperHanging> iter = hangings.iterator ();
        while (iter.hasNext ())
        {

            CreeperHanging cp = iter.next ();
            if (cp.isBurnt ())
            {
                if (cp.getDate ().getTime () - time.getTime () < 0)
                    if (cp.replace (false))
                        iter.remove ();
            }
            else if (Math.abs (cp.getDate ().getTime () - time.getTime ()) < 500 || ExplodedBlockManager.isExplosionListEmpty ())
            {
                if (!cp.replace (false))
                    cp.drop ();
                iter.remove ();
            }
        }
    }

    /**
     * Add the hanging to the list of hangings to be replaced.
     * 
     * @param h
     *            The hanging to add.
     * @param burnt
     *            Whether the hanging was destroyed by fire.
     */
    //TODO:date.
    public static void checkHanging (Hanging h, boolean burnt) {
        Date time = new Date ();
        if (CreeperConfig.loadWorld (h.getWorld ()).isRepairTimed ())
            time = new Date (time.getTime () + 1200000);
        time = burnt ? new Date (time.getTime () + 1000 * CreeperConfig.waitBeforeHealBurnt + 10000) : new Date (time.getTime () + 1000
                * CreeperConfig.waitBeforeHeal + 150000 * CreeperConfig.blockPerBlockInterval);

        CreeperHanging hang = CreeperHanging.newHanging (h, time, burnt);
        if (hang != null)
            hangings.add (hang);

        h.remove ();
    }

    /**
     * Replace all the hangings.
     */
    public static void replaceHangings () {
        for (CreeperHanging p : hangings)
            if (!p.replace (false))
                p.drop ();
        hangings.clear ();

    }

}

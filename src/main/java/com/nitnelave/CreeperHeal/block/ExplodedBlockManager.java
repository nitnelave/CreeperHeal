package com.nitnelave.CreeperHeal.block;

import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityExplodeEvent;

import com.nitnelave.CreeperHeal.CreeperHeal;
import com.nitnelave.CreeperHeal.PluginHandler;
import com.nitnelave.CreeperHeal.config.CreeperConfig;
import com.nitnelave.CreeperHeal.config.WorldConfig;
import com.nitnelave.CreeperHeal.utils.CreeperLog;
import com.nitnelave.CreeperHeal.utils.NeighborExplosion;

/**
 * Manager for the explosions list and the explosion index.
 * 
 * @author nitnelave
 * 
 */
public class ExplodedBlockManager {

    /*
     * List of explosions, to replace the blocks.
     */
    private static List<CreeperExplosion> explosionList = Collections.synchronizedList (new LinkedList<CreeperExplosion> ());
    /*
     * Map of the explosions, if the plugin is not in lightweight mode.
     */
    private static NeighborExplosion explosionIndex;

    static
    {
        if (!CreeperConfig.lightweightMode)
        {
            explosionIndex = new NeighborExplosion ();
            Bukkit.getScheduler ().runTaskTimerAsynchronously (CreeperHeal.getInstance (), new Runnable () {
                @Override
                public void run () {
                    cleanIndex ();
                }
            }, 200, 2400);
        }
        /*
         * Schedule the replacement of the blocks.
         */
        if (Bukkit.getServer ().getScheduler ().scheduleSyncRepeatingTask (CreeperHeal.getInstance (), new Runnable () {
            @Override
            public void run () {
                checkReplace (); //check to replace explosions/blocks
            }
        }, 200, CreeperConfig.blockPerBlock ? CreeperConfig.blockPerBlockInterval : 100) == -1)
            CreeperLog.warning ("[CreeperHeal] Impossible to schedule the re-filling task. Auto-refill will not work");

    }

    /**
     * Replace all the blocks of the explosions that happened near a player.
     * Near is defined in the config by the parameter "advanced.distance-near".
     * 
     * @param target
     *            The player around whom the explosions are replaced.
     */
    public static void replaceNear (Player target) {
        Location playerLoc = target.getLocation ();
        World w = target.getWorld ();
        ListIterator<CreeperExplosion> iter = explosionList.listIterator ();
        while (iter.hasNext ())
        {
            CreeperExplosion cEx = iter.next ();
            Location loc = cEx.getLocation ();
            if (loc.getWorld () == w && loc.distance (playerLoc) < CreeperConfig.distanceNear)
            {
                cEx.replace_blocks ();
                if (!CreeperConfig.lightweightMode)
                    explosionIndex.removeElement (cEx, loc.getX (), loc.getZ ());
                iter.remove ();
            }
        }

    }

    /**
     * Force the replacement of all explosions in the specified world.
     * 
     * @param world
     *            The world in which the explosions happened.
     */
    public static void forceReplace (WorldConfig world) {
        World w = Bukkit.getServer ().getWorld (world.getName ());

        synchronized (explosionList)
        {
            ListIterator<CreeperExplosion> iter = explosionList.listIterator ();
            while (iter.hasNext ())
            {
                CreeperExplosion ex = iter.next ();
                if (ex.getLocation ().getWorld ().equals (w))
                {
                    ex.replace_blocks ();
                    iter.remove ();
                    if (!CreeperConfig.lightweightMode)
                        explosionIndex.removeElement (ex);
                }
            }
        }

        BurntBlockManager.forceReplaceBurnt (world);
        HangingsManager.replaceHangings ();
    }

    /**
     * Record all the blocks destroyed by an explosion.
     * 
     * @param event
     *            The explosion.
     * @param world
     *            The config for the explosion's world.
     */
    public static void processExplosion (EntityExplodeEvent event, WorldConfig world) {
        processExplosion (event.blockList (), event.getLocation ());
    }

    /**
     * Record all the blocks in the list, with the location as the source of the
     * explosion.
     * 
     * @param list
     *            The list of destroyed blocks.
     * @param location
     *            The location of the explosion.
     */
    public static void processExplosion (List<Block> list, Location location) {
        processExplosion (list, location, null);
    }

    /*
     * Record the blocks in the list and remove them from the world so they
     * don't drop.
     */
    //TODO: Ascending rails pop if their support is gone. Maybe related to dependent blocks.
    protected static void processExplosion (List<Block> blocks, Location location, Entity entitytimed) {
        if (PluginHandler.isInArena (location))
            return;

        CreeperExplosion cEx = new CreeperExplosion (blocks, location);

        explosionList.add (cEx);
        if (!CreeperConfig.lightweightMode)
            explosionIndex.addElement (cEx, location.getX (), location.getZ ());

        // CreeperTrap code
        //        if (entity instanceof TNTPrimed)
        //        {
        //            Block block = location.getBlock ();
        //            if (CreeperTrapHandler.isTrap (block))
        //                Bukkit.getServer ().getScheduler ().scheduleSyncDelayedTask (CreeperHeal.getInstance (), new AddTrapRunnable (cEx, block, Material.TNT));
        //        }

        /*
         * Immediately replace the blocks marked for immediate replacement.
         */
        Bukkit.getServer ().getScheduler ().scheduleSyncDelayedTask (CreeperHeal.getInstance (), new Runnable () {
            @Override
            public void run () {
                BlockManager.replaceProtected ();
            }
        });

    }

    //TODO: date.
    /**
     * Check to see if any block has to be replaced in the explosions.
     */
    public static void checkReplace () { //check to see if any block has to be replaced
        ListIterator<CreeperExplosion> iter = explosionList.listIterator ();
        while (iter.hasNext ())
        {
            CreeperExplosion ex = iter.next ();
            if (ex.checkReplace ())
            {
                if (ex.isEmpty ())
                {
                    iter.remove ();
                    if (!CreeperConfig.lightweightMode)
                        explosionIndex.removeElement (ex);
                }
            }
            else
                break;
        }
        HangingsManager.replaceHangings (new Date ());

    }

    /**
     * Get whether the location is in the radius of an explosion. Do not use
     * when in light weight mode.
     * 
     * @param location
     *            The location to check.
     * @return Whether the location is in the radius of an explosion.
     */
    public static boolean isNextToExplosion (Location location) {
        return explosionIndex.hasNeighbor (location);
    }

    /*
     * Clean the explosion map from useless empty explosions. Do not use when in
     * light weight mode.
     */
    private static void cleanIndex () {
        explosionIndex.clean ();
    }

    /**
     * Get whether there are no more explosions to replace.
     * 
     * @return Whether there are no more explosions to replace.
     */
    public static boolean isExplosionListEmpty () {
        return explosionList.isEmpty ();
    }

}

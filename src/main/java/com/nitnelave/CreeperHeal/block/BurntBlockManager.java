package com.nitnelave.CreeperHeal.block;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;

import com.nitnelave.CreeperHeal.CreeperHeal;
import com.nitnelave.CreeperHeal.config.CreeperConfig;
import com.nitnelave.CreeperHeal.config.WorldConfig;
import com.nitnelave.CreeperHeal.utils.CreeperLog;
import com.nitnelave.CreeperHeal.utils.NeighborFire;

/**
 * Manager to handle the burnt blocks.
 * 
 * @author nitnelave
 * 
 */
public abstract class BurntBlockManager {

    //TODO: burnt blocks
    /*
     * The list of burnt blocks waiting to be replaced.
     */
    private static List<CreeperBurntBlock> burntList = Collections.synchronizedList (new LinkedList<CreeperBurntBlock> ());
    /*
     * If the plugin is not in lightweight mode, the list of recently burnt
     * blocks to prevent them from burning again soon.
     */
    private static Map<Location, Date> recentlyBurnt;
    /*
     * If the plugin is not in lightweight mode, the list of recently burnt
     * blocks for neighbor finding.
     */
    private static NeighborFire fireIndex;

    static
    {
        if (!CreeperConfig.lightweightMode)
        {
            fireIndex = new NeighborFire ();
            recentlyBurnt = Collections.synchronizedMap (new HashMap<Location, Date> ());

            Bukkit.getScheduler ().runTaskTimerAsynchronously (CreeperHeal.getInstance (), new Runnable () {
                @Override
                public void run () {
                    cleanUp ();
                }
            }, 200, 2400);
        }

        if (Bukkit.getServer ().getScheduler ().scheduleSyncRepeatingTask (CreeperHeal.getInstance (), new Runnable () {
            @Override
            public void run () {
                replaceBurnt ();
            }
        }, 200, 20) == -1)
            CreeperLog.warning ("[CreeperHeal] Impossible to schedule the replace-burnt task. Burnt blocks replacement will not work");

    }

    /**
     * Force immediate replacement of all blocks burnt in the past few seconds,
     * or all of them.
     * 
     * @param worldConfig
     *            The world in which to replace the blocks.
     */
    public static void forceReplaceBurnt (WorldConfig worldConfig) { //replace all of the burnt blocks since "since"
        World world = Bukkit.getServer ().getWorld (worldConfig.getName ());

        synchronized (burntList)
        {
            Iterator<CreeperBurntBlock> iter = burntList.iterator ();
            while (iter.hasNext ())
            {
                CreeperBurntBlock cBlock = iter.next ();
                if (cBlock.getWorld () == world)
                {
                    cBlock.replace (false);
                    if (!CreeperConfig.lightweightMode)
                    {
                        recentlyBurnt.put (cBlock.getLocation (), new Date (System.currentTimeMillis () + 1000 * CreeperConfig.waitBeforeBurnAgain));
                        fireIndex.removeElement (cBlock);
                    }
                    iter.remove ();
                }
            }
        }
    }

    //TODO: The dates associated with blocks should all be the date at which they should be replaced.

    /**
     * Replace the burnt blocks that have disappeared for long enough.
     */
    public static void replaceBurnt () {

        Date now = new Date ();
        synchronized (burntList)
        {
            Iterator<CreeperBurntBlock> iter = burntList.iterator ();
            while (iter.hasNext ())
            {
                CreeperBurntBlock cBlock = iter.next ();
                Date time = cBlock.getTime ();
                Block block = cBlock.getBlock ();
                if ((new Date (time.getTime () + CreeperConfig.waitBeforeHealBurnt * 1000).before (now)))
                {
                    if (CreeperBlock.isDependent (block.getTypeId ()))
                    {
                        if (!CreeperBlock.isSolid (block.getRelative (cBlock.getAttachingFace ().getOppositeFace ()).getTypeId ()))
                            cBlock.addTime (CreeperConfig.waitBeforeHealBurnt * 1000);
                        else
                        {
                            cBlock.replace (false);
                            if (!CreeperConfig.lightweightMode)
                                recentlyBurnt.put (cBlock.getLocation (), new Date (System.currentTimeMillis () + 1000 * CreeperConfig.waitBeforeBurnAgain));
                            iter.remove ();
                        }
                    }
                    else
                    {
                        cBlock.replace (false);
                        if (!CreeperConfig.lightweightMode)
                            recentlyBurnt.put (cBlock.getLocation (), new Date (System.currentTimeMillis () + 1000 * CreeperConfig.waitBeforeBurnAgain));
                        iter.remove ();
                    }
                }
                else if (!CreeperBlock.isDependent (block.getTypeId ()))
                    break;
            }
        }
    }

    /*
     * If the block relative to the face is dependent on the main block, record
     * it.
     */
    private static void recordAttachedBurntBlock (Block block, Date now, BlockFace face) {
        BlockState block_up = block.getRelative (face).getState ();
        CreeperBurntBlock cBB = new CreeperBurntBlock (new Date (now.getTime () + 100), block_up);
        if (cBB.getAttachingFace () == rotateCClockWise (face))
        {
            burntList.add (cBB);
            if (!CreeperConfig.lightweightMode)
                fireIndex.addElement (cBB, cBB.getLocation ().getX (), cBB.getLocation ().getZ ());
            block_up.getBlock ().setTypeIdAndData (0, (byte) 0, false);

        }
    }

    /*
     * Get the counter-clockwise face.
     */
    private static BlockFace rotateCClockWise (BlockFace face) {
        switch (face)
        {
            case EAST:
                return BlockFace.NORTH;
            case NORTH:
                return BlockFace.WEST;
            case WEST:
                return BlockFace.SOUTH;
            default:
                return BlockFace.EAST;
        }
    }

    /**
     * Record a burnt block.
     * 
     * @param block
     *            The block to be recorded.
     */
    public static void recordBurntBlock (Block block) {
        if (block.getType () != Material.TNT)
        {
            Date now = new Date ();
            BlockFace[] faces = {BlockFace.UP, BlockFace.EAST, BlockFace.WEST, BlockFace.NORTH, BlockFace.SOUTH};
            for (BlockFace face : faces)
                recordAttachedBurntBlock (block, now, face);
            CreeperBurntBlock cBB = new CreeperBurntBlock (now, block.getState ());
            burntList.add (cBB);
            if (!(CreeperConfig.lightweightMode))
            {
                Location l = cBB.getLocation ();
                fireIndex.addElement (cBB, l.getX (), l.getZ ());
            }
            block.setTypeIdAndData (0, (byte) 0, false);
        }
    }

    /**
     * Get whether the location is close to a recently burnt block.
     * 
     * @param location
     *            The location to check.
     * @return Whether the location is close to a recently burnt block.
     */
    public static boolean isNextToFire (Location location) {
        return fireIndex.hasNeighbor (location);
    }

    /**
     * Get whether there is no recorded blocks to be replaced.
     * 
     * @return Whether there is no recorded blocks to be replaced.
     */
    public static boolean isIndexEmpty () {
        return fireIndex.isEmpty ();
    }

    /**
     * Get whether the block was recently burnt and should burn again.
     * 
     * @param block
     *            The block.
     * @return Whether the block was recently burnt.
     */
    public static boolean wasRecentlyBurnt (Block block) {
        Date d = recentlyBurnt.get (block.getLocation ());
        return d != null && d.after (new Date ());
    }

    /**
     * Clean up the block lists, remove the useless blocks. Do not use when in
     * light weight mode.
     */
    private static void cleanUp () {
        fireIndex.clean ();
        synchronized (recentlyBurnt)
        {
            Iterator<Location> iter = recentlyBurnt.keySet ().iterator ();
            Date now = new Date ();
            while (iter.hasNext ())
            {
                Location l = iter.next ();
                Date d = recentlyBurnt.get (l);
                if (d.before (now))
                    iter.remove ();
            }
        }

    }

}

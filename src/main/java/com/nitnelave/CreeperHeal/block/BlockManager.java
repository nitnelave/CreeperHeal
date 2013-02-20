package com.nitnelave.CreeperHeal.block;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

import com.nitnelave.CreeperHeal.CreeperHeal;
import com.nitnelave.CreeperHeal.config.CreeperConfig;
import com.nitnelave.CreeperHeal.config.WorldConfig;
import com.nitnelave.CreeperHeal.utils.CreeperLog;
import com.nitnelave.CreeperHeal.utils.DateLoc;
import com.nitnelave.CreeperHeal.utils.NeighborDateLoc;

/**
 * Manager to gather block-related methods.
 * 
 * @author nitnelave
 * 
 */
public abstract class BlockManager {

    /*
     * Block to be replaced immediately after an explosion.
     */
    private static Map<Location, Replaceable> toReplace = Collections.synchronizedMap (new HashMap<Location, Replaceable> ());

    /*
     * Blocks whose fall should be prevented.
     */
    private static NeighborDateLoc fallIndex;

    /*
     * Block whose update should be prevented.
     */
    private static Map<CreeperBlock, Date> updateIndex;

    static
    {
        if (!CreeperConfig.lightweightMode)
        {
            fallIndex = new NeighborDateLoc ();
            updateIndex = Collections.synchronizedMap (new HashMap<CreeperBlock, Date> ());

            Bukkit.getScheduler ().runTaskTimerAsynchronously (CreeperHeal.getInstance (), new Runnable () {
                @Override
                public void run () {
                    cleanUp ();
                }
            }, 200, 2400);
        }
    }

    /**
     * Add a block to the list of blocks to be replaced immediately.
     * 
     * @param block
     *            The block to add.
     */
    public static void addToReplace (CreeperBlock block) {
        toReplace.put (block.getLocation (), block);
    }

    /*
     * Replace the blocks that should be immediately replaced after an
     * explosion.
     */
    protected static void replaceProtected () {
        Iterator<Replaceable> iter = toReplace.values ().iterator ();
        while (iter.hasNext ())
            iter.next ().replace (true);
        toReplace.clear ();
    }

    /*
     * Check the living entities in the chunk for suffocating ones, and save
     * them.
     */
    protected static void checkPlayerOneBlock (Location loc) {
        Entity[] play_list = loc.getBlock ().getChunk ().getEntities ();
        for (Entity en : play_list)
            if (en instanceof LivingEntity && loc.distance (en.getLocation ()) < 2)
                en.teleport (check_player_suffocate ((LivingEntity) en));
    }

    /**
     * Check the players and other animals (except in lightweight mode) to see
     * if they were trapped by the explosion's replacement.
     * 
     * @param loc
     *            The center of the explosion.
     * @param radius
     *            The radius of the explosion.
     */
    public static void checkPlayerExplosion (Location loc, double radius) {
        List<? extends Entity> entityList;
        if (CreeperConfig.lightweightMode)
            entityList = loc.getWorld ().getPlayers ();
        else
            entityList = loc.getWorld ().getEntities ();
        for (Entity en : entityList)
            if (en instanceof LivingEntity && loc.distance (en.getLocation ()) < radius + 3)
                en.teleport (check_player_suffocate ((LivingEntity) en));

    }

    /*
     * Check if the block at the coordinates, or one above or below is suitable
     * to put a living being.
     */
    private static boolean check_free_horizontal (Location loc) {
        loc.add (0, -1, 0);
        for (int k = -1; k < 2; k++)
        {
            loc.add (0, 1, 0);
            if (check_free (loc))
                return true;
        }
        loc.add (0, -1, 0);
        return false;
    }

    /*
     * Get whether the location is suitable ground so a player doesn't
     * suffocate.
     */
    private static boolean check_free (Location loc) {
        Block block = loc.getBlock ();
        if (!CreeperBlock.isSolid (block) && !CreeperBlock.isSolid (block.getRelative (0, 1, 0)) && CreeperBlock.isSolid (block.getRelative (0, -1, 0)))
        {
            loc.add (0.5, 0, 0.5);
            return true;
        }
        return false;
    }

    /*
     * Get the location to which an entity should be teleported for safety.
     */
    private static Location check_player_suffocate (LivingEntity en) {
        Location loc = en.getLocation ();

        if (CreeperBlock.isSolid (loc.getBlock ()) || CreeperBlock.isSolid (loc.getBlock ().getRelative (0, 1, 0)))
            for (int k = 1; k + loc.getBlockY () < 127; k++)
            {
                Location l = loc.clone ().add (0, k, 0);
                if (check_free (l))
                    return l;

                l.add (k, -k, 0);
                if (check_free_horizontal (l))
                    return l;

                l.add (-2 * k, 0, 0);
                if (check_free_horizontal (l))
                    return l;

                l.add (k, 0, k);
                if (check_free_horizontal (l))
                    return l;

                l.add (0, 0, -2 * k);
                if (check_free_horizontal (l))
                    return l;
            }
        return loc;
    }

    /*
     * For each world, check if it is the time for timed repairs, and repair.
     */
    private static void checkReplaceTime () {
        for (WorldConfig w : CreeperConfig.world_config.values ())
        {
            long time = Bukkit.getServer ().getWorld (w.name).getTime ();
            if (w.repairTime != -1 && ((Math.abs (w.repairTime - time) < 600) || (Math.abs (Math.abs (w.repairTime - time) - 24000)) < 600))
            {
                ExplodedBlockManager.forceReplace (w);
                BurntBlockManager.forceReplaceBurnt (w);
                HangingsManager.replaceHangings ();
            }
        }
    }

    /**
     * Schedule the timed repair task.
     */
    public static void scheduleTimeRepairs () {
        Bukkit.getScheduler ().runTaskTimerAsynchronously (CreeperHeal.getInstance (), new Runnable () {
            @Override
            public void run () {
                checkReplaceTime ();
            }
        }, 200, 1200);

        CreeperLog.warning ("[CreeperHeal] Impossible to schedule the time-repair task. Time repairs will not work");

    }

    /**
     * Get whether the location is next to a block whose fall is prevented.
     * 
     * @param loc
     *            The location to check.
     * @return Whether the location is next to a block whose fall is prevented.
     */
    public static boolean isNextToFallPrevention (Location loc) {
        return fallIndex.hasNeighbor (loc);
    }

    /**
     * Add the location to the list of blocks that shouldn't fall. The block's
     * fall is prevented until after 200 times the block per block replacement
     * interval.
     * 
     * @param location
     *            The block's location.
     */
    public static void putFallPrevention (Location location) {
        fallIndex.addElement (new DateLoc (new Date (), location), location.getX (), location.getZ ());
    }

    /**
     * Get whether the location is next to a block whose update is prevented.
     * 
     * @param loc
     *            The location to check.
     * @return Whether the location is next to a block whose update is
     *         prevented.
     */
    public static boolean isUpdatePrevented (CreeperBlock b) {
        return updateIndex.containsKey (b);
    }

    /**
     * Add the location to the list of blocks that shouldn't be updated. The
     * block's updates are prevented until after 200 times the block per block
     * replacement interval.
     * 
     * @param location
     *            The block's location.
     */
    public static void putUpdatePrevention (CreeperBlock block) {
        updateIndex.put (block, new Date ());
    }

    /*
     * Clean up by removing the unnecessary blocks from the fall and update
     * indexes.
     */
    private static void cleanUp () {
        fallIndex.clean ();

        Date delay = new Date (new Date ().getTime () - 200 * CreeperConfig.blockPerBlockInterval);
        Iterator<Date> iter;
        synchronized (updateIndex)
        {
            iter = updateIndex.values ().iterator ();
            while (iter.hasNext ())
            {
                Date date = iter.next ();
                if (date.before (delay))
                    iter.remove ();
                else
                    break;
            }
        }
    }

}

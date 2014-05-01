package com.nitnelave.CreeperHeal.block;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Hanging;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.scheduler.BukkitTask;

import com.nitnelave.CreeperHeal.CreeperHeal;
import com.nitnelave.CreeperHeal.PluginHandler;
import com.nitnelave.CreeperHeal.config.CfgVal;
import com.nitnelave.CreeperHeal.config.CreeperConfig;
import com.nitnelave.CreeperHeal.config.WorldConfig;
import com.nitnelave.CreeperHeal.events.CHBlockHealEvent.CHBlockHealReason;
import com.nitnelave.CreeperHeal.events.CHExplosionRecordEvent;
import com.nitnelave.CreeperHeal.utils.NeighborExplosion;
import java.util.ArrayList;

/**
 * Manager for the explosions list and the explosion index.
 * 
 * @author nitnelave
 * 
 */
public class ExplodedBlockManager
{

    /*
     * List of explosions, to replace the blocks.
     */
    private static List<CreeperExplosion> explosionList = new LinkedList<CreeperExplosion>();
    /*
     * Map of the explosions, if the plugin is not in lightweight mode.
     */
    private static NeighborExplosion explosionIndex;

    /*
     * List to temporarily store the paintings before adding them to the
     * explosion right after.
     */
    private static List<CreeperHanging> hangingList = new LinkedList<CreeperHanging>();

    /*
     * Block replacement task.
     */
    private static BukkitTask task;

    public static void init()
    {
        if (CreeperConfig.getBool(CfgVal.LEAVES_VINES))
        {
            explosionIndex = new NeighborExplosion();
            Bukkit.getScheduler().scheduleSyncRepeatingTask(CreeperHeal.getInstance(), new Runnable()
            {
                @Override
                public void run()
                {
                    cleanIndex();
                }
            }, 200, 7200);
        }
        scheduleTask();
    }

    /**
     * Replace all the blocks of the explosions that happened near a player.
     * Near is defined in the config by the parameter "advanced.distance-near".
     * 
     * @param target
     *            The player around whom the explosions are replaced.
     */
    public static void replaceNear(Player target)
    {
        removeExplosionsAround(target.getLocation(), CreeperConfig.getInt(CfgVal.DISTANCE_NEAR));
    }

    /*
     * Remove all the explosions close enough around the location.
     */
    private static void removeExplosionsAround(Location loc, float distanceNear)
    {
        World w = loc.getWorld();
        LinkedList<CreeperExplosion> pass = new LinkedList<CreeperExplosion>();
        ListIterator<CreeperExplosion> iter = explosionList.listIterator();
        while (iter.hasNext())
        {
            CreeperExplosion ex = iter.next();
            Location l = ex.getLocation();
            if (l.getWorld() == w && distanceNear > l.distance(loc))
            {
                ex.replace_blocks(false, CHBlockHealReason.FORCED);
                pass.add(ex);
                iter.remove();
            }
        }
        for (CreeperExplosion ex : pass)
        {
            ex.replace_blocks(true, CHBlockHealReason.FORCED);
            if (CreeperConfig.getBool(CfgVal.LEAVES_VINES))
                explosionIndex.removeElement(ex);
        }

    }

    /**
     * Force the replacement of all explosions in the specified world.
     * 
     * @param world
     *            The world in which the explosions happened.
     */
    public static void forceReplace(WorldConfig world)
    {
        removeExplosionsAround(world.getWorld().getSpawnLocation(), Float.POSITIVE_INFINITY);
        BurntBlockManager.forceReplaceBurnt(world);
    }

    /**
     * Force the replacement of all explosions.
     */
    public static void forceReplace()
    {
        ListIterator<CreeperExplosion> iter = explosionList.listIterator();
        LinkedList<CreeperExplosion> pass = new LinkedList<CreeperExplosion>();
        while (iter.hasNext())
        {
            CreeperExplosion ex = iter.next();
            ex.replace_blocks(false, CHBlockHealReason.FORCED);
            pass.add(ex);
            iter.remove();
        }
        for (CreeperExplosion ex : pass)
        {
            ex.replace_blocks(true, CHBlockHealReason.FORCED);
            if (CreeperConfig.getBool(CfgVal.LEAVES_VINES))
                explosionIndex.removeElement(ex);
        }
        BurntBlockManager.forceReplaceBurnt();
    }

    /**
     * Record all the blocks destroyed by an explosion.
     * 
     * @param event
     *            The explosion.
     */
    public static void processExplosion(EntityExplodeEvent event,
                                        CHExplosionRecordEvent.ExplosionReason reason)
    {
        processExplosion(event.blockList(), event.getLocation(), reason);
    }

    /**
     * Record all the blocks in the list, with the location as the source of the
     * explosion.
     * 
     * @param originalBlockList
     *            The list of destroyed blocks.
     * @param location
     *            The location of the explosion.
     */
    public static void processExplosion(List<Block> originalBlockList, Location location,
                                        CHExplosionRecordEvent.ExplosionReason reason)
    {
        if (PluginHandler.isInArena(location))
            return;

        //process list is the list of blocks yet to be processed by creeperheal.
        CHExplosionRecordEvent event = new CHExplosionRecordEvent(originalBlockList, location, reason);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled())
            return;
        List<Block> processList = new ArrayList(event.getBlocks());
        for(Block b : event.getProtectedBlocks())
        {
            CreeperBlock cb = CreeperBlock.newBlock(b.getState());
            ToReplaceList.addToReplace(cb);
            cb.remove();
        }

        CreeperExplosion cEx = null;

        if (CreeperConfig.getBool(CfgVal.LEAVES_VINES)
            && CreeperConfig.getBool(CfgVal.JOIN_EXPLOSIONS))
            cEx = explosionIndex.getNeighbor(location);

        if (cEx == null || cEx.hasStartedReplacing())
        {
            cEx = new CreeperExplosion(location);
            explosionList.add(cEx);
            if (CreeperConfig.getBool(CfgVal.LEAVES_VINES))
                explosionIndex.addElement(cEx, location.getX(), location.getZ());
        }

        cEx.addBlocks(processList, location);

        for (CreeperHanging h : hangingList)
            cEx.record(h);
        hangingList.clear();

        /*
         * Immediately replace the blocks marked for immediate replacement.
         */
        ToReplaceList.replaceProtected();
    }

    /**
     * Check to see if any block has to be replaced in the explosions.
     */
    private static void checkReplace()
    { //check to see if any block has to be replaced
        ListIterator<CreeperExplosion> iter = explosionList.listIterator();
        while (iter.hasNext())
        {
            CreeperExplosion ex = iter.next();
            if (ex.checkReplace())
            {
                if (ex.isEmpty())
                {
                    iter.remove();
                    if (CreeperConfig.getBool(CfgVal.LEAVES_VINES))
                        explosionIndex.removeElement(ex);
                }
            }
            else
                break;
        }
        //        HangingsManager.replaceHangings (new Date ());

    }

    /**
     * Get whether the location is in the radius of an explosion. Do not use
     * when in light weight mode.
     * 
     * @param location
     *            The location to check.
     * @return Whether the location is in the radius of an explosion.
     */
    public static boolean isNextToExplosion(Location location)
    {
        if (!CreeperConfig.getBool(CfgVal.LEAVES_VINES))
            return false;
        return explosionIndex.hasNeighbor(location);
    }

    /*
     * Clean the explosion map from useless empty explosions. Do not use when in
     * light weight mode.
     */
    private static void cleanIndex()
    {
        if (!CreeperConfig.getBool(CfgVal.LEAVES_VINES))
            return;
        explosionIndex.clean();
    }

    /**
     * Get whether there are no more explosions to replace.
     * 
     * @return Whether there are no more explosions to replace.
     */
    public static boolean isExplosionListEmpty()
    {
        return explosionList.isEmpty();
    }

    /**
     * Record a hanging as part of the explosion.
     * 
     * @param hanging
     *            The hanging to record.
     */
    public static void recordHanging(Hanging hanging)
    {
        CreeperHanging h = CreeperHanging.newHanging(hanging);
        if (h != null)
        {
            hangingList.add(h);
            h.remove();
        }
    }

    private static void scheduleTask()
    {
        task = Bukkit.getServer().getScheduler().runTaskTimer(CreeperHeal.getInstance(), new Runnable()
        {
            @Override
            public void run()
            {
                checkReplace(); //check to replace explosions/blocks
            }
        }, 0, CreeperConfig.getBool(CfgVal.BLOCK_PER_BLOCK) ? CreeperConfig.getInt(CfgVal.BLOCK_PER_BLOCK_INTERVAL)
                                                           : 100);
    }

    /**
     * Cancel and re-schedule the block replacement task, to update the block
     * interval.
     */
    public static void rescheduleTask()
    {
        task.cancel();
        scheduleTask();
    }
}

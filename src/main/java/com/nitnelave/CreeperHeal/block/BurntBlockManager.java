package com.nitnelave.CreeperHeal.block;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;

import com.nitnelave.CreeperHeal.CreeperHeal;
import com.nitnelave.CreeperHeal.config.CfgVal;
import com.nitnelave.CreeperHeal.config.CreeperConfig;
import com.nitnelave.CreeperHeal.config.WorldConfig;
import com.nitnelave.CreeperHeal.utils.NeighborFire;

/**
 * Manager to handle the burnt blocks.
 * 
 * @author nitnelave
 * 
 */
public abstract class BurntBlockManager
{

    /*
     * The list of burnt blocks waiting to be replaced.
     */
    private static List<CreeperBurntBlock> burntList = new LinkedList<CreeperBurntBlock>();
    /*
     * If the plugin is not in lightweight mode, the list of recently burnt
     * blocks to prevent them from burning again soon.
     */
    private static Map<Location, Date> recentlyBurnt;
    /*
     * If the leaves replacement setting is on, the list of recently burnt
     * blocks for neighbor finding.
     */
    private static NeighborFire fireIndex;

    public static void init()
    {
        if (CreeperConfig.getInt(CfgVal.WAIT_BEFORE_BURN_AGAIN) > 0)
            recentlyBurnt = new HashMap<Location, Date>();
        if (CreeperConfig.getBool(CfgVal.LEAVES_VINES))
            fireIndex = new NeighborFire();

        Bukkit.getScheduler().scheduleSyncRepeatingTask(CreeperHeal.getInstance(), new Runnable()
        {
            @Override
            public void run()
            {
                cleanUp();
            }
        }, 300, 7200);

        Bukkit.getScheduler().runTaskTimer(CreeperHeal.getInstance(), new Runnable()
        {
            @Override
            public void run()
            {
                replaceBurnt();
            }
        }, 0, 20);

    }

    /**
     * Force immediate replacement of all blocks burnt in the specified world
     * 
     * @param worldConfig
     *            The world in which to replace the blocks.
     */
    public static void forceReplaceBurnt(WorldConfig worldConfig)
    {
        World world = Bukkit.getServer().getWorld(worldConfig.getName());

        Iterator<CreeperBurntBlock> iter = burntList.iterator();
        int d = CreeperConfig.getInt(CfgVal.WAIT_BEFORE_BURN_AGAIN);
        Date time = new Date(new Date().getTime() + 1000 * d);
        while (iter.hasNext())
        {
            CreeperBurntBlock cBlock = iter.next();
            if (cBlock.getWorld() == world)
            {
                cBlock.replace(true);
                if (d > 0)
                    recentlyBurnt.put(cBlock.getLocation(), time);
                if (CreeperConfig.getBool(CfgVal.LEAVES_VINES))
                    fireIndex.removeElement(cBlock);
                iter.remove();
            }
        }
    }

    /**
     * Force immediate replacement of all blocks burnt.
     */
    public static void forceReplaceBurnt()
    {
        Iterator<CreeperBurntBlock> iter = burntList.iterator();
        int d = CreeperConfig.getInt(CfgVal.WAIT_BEFORE_BURN_AGAIN);
        Date time = new Date(new Date().getTime() + 1000 * d);
        while (iter.hasNext())
            iter.next().replace(true);
        if (d > 0)
        {
            iter = burntList.iterator();
            while (iter.hasNext())
                recentlyBurnt.put(iter.next().getLocation(), time);
        }
        if (CreeperConfig.getBool(CfgVal.LEAVES_VINES))
            fireIndex.clear();
        burntList.clear();
    }

    /**
     * Replace the burnt blocks that have disappeared for long enough.
     */
    private static void replaceBurnt()
    {

        int d = CreeperConfig.getInt(CfgVal.WAIT_BEFORE_BURN_AGAIN);
        Date time = new Date(new Date().getTime() + 1000 * d);
        Iterator<CreeperBurntBlock> iter = burntList.iterator();
        while (iter.hasNext())
        {
            CreeperBurntBlock cBlock = iter.next();
            if (cBlock.checkReplace())
            {
                if (cBlock.wasReplaced())
                {
                    iter.remove();
                    if (d > 0)
                        recentlyBurnt.put(cBlock.getLocation(), time);
                    if (CreeperConfig.getBool(CfgVal.LEAVES_VINES))
                        fireIndex.removeElement(cBlock);
                }
            }
            else
                break;
        }
    }

    /**
     * Record a burnt block.
     * 
     * @param block
     *            The block to be recorded.
     */
    public static void recordBurntBlock(Block block)
    {
        CreeperBlock b = CreeperBlock.newBlock(block.getState());
        if (b == null)
            return;

        for (NeighborBlock neighbor : b.getDependentNeighbors())
            if (neighbor.isNeighbor())
                recordBurntBlock(new CreeperBurntBlock(new Date(new Date().getTime() + 100), neighbor.getBlock().getState()));
        recordBurntBlock(new CreeperBurntBlock(new Date(), b));
    }

    /**
     * Add a block to the list of burnt blocks to be replaced, and remove it
     * from the world.
     * 
     * @param block
     *            The block to add.
     */
    public static void recordBurntBlock(CreeperBurntBlock block)
    {
        if (block.getBlock() != null)
        {
            burntList.add(block);
            if (CreeperConfig.getBool(CfgVal.LEAVES_VINES))
                fireIndex.addElement(block);
            block.remove();
        }
    }

    /**
     * Get whether the location is close to a recently burnt block.
     * 
     * @param location
     *            The location to check.
     * @return Whether the location is close to a recently burnt block.
     */
    public static boolean isNextToFire(Location location)
    {
        if (!CreeperConfig.getBool(CfgVal.LEAVES_VINES))
            return false;
        return fireIndex.hasNeighbor(location);
    }

    /**
     * Get whether there is no recorded blocks to be replaced.
     * 
     * @return Whether there is no recorded blocks to be replaced.
     */
    public static boolean isIndexEmpty()
    {
        if (!CreeperConfig.getBool(CfgVal.LEAVES_VINES))
            return false;
        return fireIndex.isEmpty();
    }

    /**
     * Get whether the block was recently burnt and should burn again.
     * 
     * @param block
     *            The block.
     * @return Whether the block was recently burnt.
     */
    public static boolean wasRecentlyBurnt(Block block)
    {
        if (CreeperConfig.getInt(CfgVal.WAIT_BEFORE_BURN_AGAIN) <= 0)
            return false;
        Date d = recentlyBurnt.get(block.getLocation());
        return d != null && d.after(new Date());
    }

    /**
     * Clean up the block lists, remove the useless blocks. Do not use when in
     * light weight mode.
     */
    private static void cleanUp()
    {
        if (CreeperConfig.getBool(CfgVal.LEAVES_VINES))
            fireIndex.clean();
        if (CreeperConfig.getInt(CfgVal.WAIT_BEFORE_BURN_AGAIN) > 0)
        {
            Iterator<Location> iter = recentlyBurnt.keySet().iterator();
            Date now = new Date();
            while (iter.hasNext())
            {
                Location l = iter.next();
                Date d = recentlyBurnt.get(l);
                if (d.before(now))
                    iter.remove();
            }
        }

    }

}

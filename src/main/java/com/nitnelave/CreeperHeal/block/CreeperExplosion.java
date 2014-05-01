package com.nitnelave.CreeperHeal.block;

import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

import com.nitnelave.CreeperHeal.config.CfgVal;
import com.nitnelave.CreeperHeal.config.CreeperConfig;
import com.nitnelave.CreeperHeal.config.WorldConfig;
import com.nitnelave.CreeperHeal.events.CHBlockHealEvent;
import com.nitnelave.CreeperHeal.events.CHBlockHealEvent.CHBlockHealReason;
import com.nitnelave.CreeperHeal.utils.ShortLocation;
import com.nitnelave.CreeperHeal.utils.Suffocating;

/**
 * Represents an explosion, with the list of blocks destroyed, the time of the
 * explosion, and the radius.
 * 
 * @author nitnelave
 * 
 */
public class CreeperExplosion
{
    /**
     * The time after which the block replacements begin.
     */
    private final LinkedList<Replaceable> blockList;
    private Location loc;
    private int locWeight = 0;
    private double radius = 0;
    private final WorldConfig world;
    private final HashSet<ShortLocation> checked = new HashSet<ShortLocation>();
    private ReplacementTimer timer;

    /**
     * Constructor.
     * 
     * @param loc
     *            The location of the explosion.
     */
    public CreeperExplosion(Location loc)
    {
        world = CreeperConfig.getWorld(loc.getWorld());
        timer = new ReplacementTimer(new Date(new Date().getTime() + 1000
                                              * CreeperConfig.getInt(CfgVal.WAIT_BEFORE_HEAL)), world.isRepairTimed());
        blockList = new LinkedList<Replaceable>();
        this.loc = loc;
    }

    /**
     * Add blocks to an explosion, and reset the timer to the time of the last
     * explosion.
     * 
     * @param blocks
     *            The list of blocks to add.
     */
    public void addBlocks(List<Block> blocks, Location newLoc)
    {
        timer = new ReplacementTimer(new Date(new Date().getTime() + 1000
                                              * CreeperConfig.getInt(CfgVal.WAIT_BEFORE_HEAL)), world.isRepairTimed());
        loc = new Location(loc.getWorld(), (locWeight * loc.getX() + newLoc.getX())
                                           / (locWeight + 1), (locWeight * loc.getY() + newLoc.getY())
                                                              / (locWeight + 1), (locWeight
                                                                                  * loc.getZ() + newLoc.getZ())
                                                                                 / (locWeight + 1));
        locWeight++;
        checked.clear();
        recordBlocks(blocks);
        if (CreeperConfig.getBool(CfgVal.EXPLODE_OBSIDIAN))
            checkForObsidian();

        Collections.sort(blockList, new CreeperComparator(loc));
        radius = computeRadius();
    }

    /**
     * Get the time of the explosion.
     * 
     * @return The time of the explosion.
     */
    public Date getTime()
    {
        return timer.getTime();
    }

    /*
     * Get the distance between the explosion's location and the furthest block.
     */
    private double computeRadius()
    {
        double r = 0;
        for (Replaceable b : blockList)
        {
            Location bl = b.getBlock().getLocation();
            r = Math.max(r, loc.distance(bl));
        }
        return r + 1;
    }

    /**
     * Get the location of the explosion.
     * 
     * @return The location of the explosion.
     */
    public Location getLocation()
    {
        return loc;
    }

    /**
     * Get the radius of the explosion (i.e. the distance between the location
     * and the furthest block).
     * 
     * @return The radius of the explosion.
     */
    public double getRadius()
    {
        return radius;
    }

    /*
     * Replace all the blocks in the list.
     */
    protected void replace_blocks(boolean shouldDrop, CHBlockHealReason reason)
    {
        Iterator<Replaceable> iter = blockList.iterator();
        while (iter.hasNext())
        {
            Replaceable block = iter.next();
            CHBlockHealEvent event = new CHBlockHealEvent(block, shouldDrop, reason);
            Bukkit.getPluginManager().callEvent(event);
            if (!event.isCancelled() && block.replace(event.shouldDrop()))
                iter.remove();
        }
        if (shouldDrop)
        {
            blockList.clear();

            if (CreeperConfig.getBool(CfgVal.TELEPORT_ON_SUFFOCATE))
                Suffocating.checkPlayerExplosion(loc, radius);
        }
    }

    /**
     * Replace the first block of the list.
     * 
     * @return False if the list is now empty.
     */
    private void replace_one_block()
    {
        Replaceable block;
        if (blockList.isEmpty())
            return;
        block = blockList.remove();
        CHBlockHealEvent event = new CHBlockHealEvent(block, false, CHBlockHealReason.BLOCK_BY_BLOCK);
        Bukkit.getPluginManager().callEvent(event);
        if (!event.isCancelled() && !block.replace(event.shouldDrop()))
            block.delayReplacement(CHBlockHealReason.BLOCK_BY_BLOCK);
        if (CreeperConfig.getBool(CfgVal.TELEPORT_ON_SUFFOCATE))
            Suffocating.checkPlayerOneBlock(block.getBlock().getLocation());
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;
        if (o instanceof CreeperExplosion)
        {
            CreeperExplosion e = (CreeperExplosion) o;
            return e.timer == timer && e.loc == loc && e.radius == radius;
        }
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode()
    {
        return (int) (timer.hashCode() + radius + loc.hashCode());
    }

    /*
     * Check for dependent blocks and record them first.
     */
    private void recordBlocks(List<Block> blocks)
    {
        if (!blocks.isEmpty())
        {
            Iterator<Block> iter = blocks.iterator();
            while (iter.hasNext())
            {
                Block b = iter.next();
                if (CreeperBlock.isDependent(b.getTypeId()))
                {
                    record(b);
                    iter.remove();
                }
            }
            for (Block b : blocks)
                record(b);
        }
    }

    /*
     * In case of possible obsidian destruction, check for obsidian around, and
     * give them a chance to be destroyed.
     */
    private void checkForObsidian()
    {
        int radius = CreeperConfig.getInt(CfgVal.OBSIDIAN_RADIUS);
        double chance = ((float) CreeperConfig.getInt(CfgVal.OBSIDIAN_CHANCE)) / 100;
        World w = loc.getWorld();

        Random r = new Random(System.currentTimeMillis());
        boolean table = CreeperConfig.getBool(CfgVal.OBSIDIAN_TABLE);

        for (int i = loc.getBlockX() - radius; i < loc.getBlockX() + radius; i++)
            for (int j = Math.max(0, loc.getBlockY() - radius); j < Math.min(w.getMaxHeight(), loc.getBlockY()
                                                                                               + radius); j++)
                for (int k = loc.getBlockZ() - radius; k < loc.getBlockZ() + radius; k++)
                {
                    Location l = new Location(w, i, j, k);
                    if (l.distance(loc) > radius)
                        continue;
                    Block b = l.getBlock();
                    if (isObsidianLike(b.getType(), table) && r.nextDouble() < chance)
                        record(b);
                }
    }

    private boolean isObsidianLike(Material m, boolean table)
    {
        return m == Material.OBSIDIAN
               || (table && (m == Material.ENCHANTMENT_TABLE || m == Material.ENDER_CHEST));
    }

    /**
     * Record one block and remove it. If it is protected, add to the
     * replace-immediately list. Check for dependent blocks around.
     * 
     * @param block
     *            The block to record.
     */
    public void record(Block block)
    {
    	if (block.getType() == Material.PORTAL)
    		return;
    	
        CreeperBlock cBlock = CreeperBlock.newBlock(block.getState());

        if (cBlock == null || checked.contains(new ShortLocation(block)))
            return;

        checked.add(new ShortLocation(block));

        if ((CreeperConfig.getBool(CfgVal.PREVENT_CHAIN_REACTION) && block.getType().equals(Material.TNT))
            || world.isProtected(block))
        {
            CreeperBlock b = CreeperBlock.newBlock(block.getState());
            if (b != null)
            {
                ToReplaceList.addToReplace(b);
                b.remove();
            }
            return;
        }

        BlockId id = new BlockId(block);
        if (!world.isBlackListed(id))
        {
            // The block should be replaced.

            for (NeighborBlock b : cBlock.getDependentNeighbors())
                if (b.isNeighbor())
                    record(b.getBlock());

            CreeperBlock b = CreeperBlock.newBlock(block.getState());
            if (b != null)
            {
                blockList.add(b);
                b.remove();
            }
        }
        else if (CreeperConfig.getBool(CfgVal.DROP_DESTROYED_BLOCKS))
        {
            cBlock.drop(false);
            cBlock.remove();

        }

    }

    /**
     * Add a Replaceable to the list, and remove it from the world.
     * 
     * @param block
     *            The Replaceable to add.
     */
    public void record(Replaceable block)
    {
        if (block != null)
        {
            blockList.add(block);
            block.remove();
        }
    }

    /**
     * Check if the explosion has blocks to repair, and repair them (or one of
     * them in case of block per block).
     * 
     * @return False if the explosion has not started its replacements yet
     *         because it is not time.
     */
    public boolean checkReplace()
    {
        if (timer.isTimed())
            return true;
        if (timer.checkReplace())
        {
            if (CreeperConfig.getBool(CfgVal.BLOCK_PER_BLOCK))
                replace_one_block();
            else
                replace_blocks(true, CHBlockHealReason.EXPLOSION);
            return true;

        }
        return false;
    }

    /**
     * Get whether the explosion has started replacing (only in block per block
     * mode).
     * 
     * @return True if the explosion has started replacing blocks
     */
    public boolean hasStartedReplacing()
    {
        if (timer.isTimed())
            return false;
        return timer.checkReplace();
    }

    /**
     * Get whether the list of blocks to be replaced is empty.
     * 
     * @return Whether the list is empty.
     */
    public boolean isEmpty()
    {
        return blockList.isEmpty();
    }

}

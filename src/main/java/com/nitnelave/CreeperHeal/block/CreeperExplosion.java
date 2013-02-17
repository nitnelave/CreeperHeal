package com.nitnelave.CreeperHeal.block;

import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

import com.nitnelave.CreeperHeal.PluginHandler;
import com.nitnelave.CreeperHeal.config.CreeperConfig;
import com.nitnelave.CreeperHeal.config.WorldConfig;

/**
 * Represents an explosion, with the list of blocks destroyed, the time of the
 * explosion, and the radius.
 * 
 * @author nitnelave
 * 
 */
public class CreeperExplosion {
    private final Date time;
    private final LinkedList<Replaceable> blockList;
    private final Location loc;
    private final double radius;
    private final WorldConfig world;
    private final boolean timed;

    /**
     * Constructor. Record every block in the list and remove them from the
     * world.
     * 
     * @param blocks
     *            The list of destroyed blocks.
     * @param loc
     *            The location of the explosion.
     */
    public CreeperExplosion (List<Block> blocks, Location loc) {
        world = CreeperConfig.loadWorld (loc.getWorld ());
        timed = world.isRepairTimed ();
        time = timed ? new Date (new Date ().getTime () + 1200000) : new Date ();
        blockList = new LinkedList<Replaceable> ();
        this.loc = loc;

        recordBlocks (blocks);

        if (CreeperConfig.explodeObsidian)
            checkForObsidian ();

        Collections.sort (blockList, new CreeperComparator ());

        radius = computeRadius ();
    }

    /**
     * Get the list of blocks destroyed still to be replaced.
     * 
     * @return The list of blocks still to be replaced.
     */
    public LinkedList<Replaceable> getBlockList () {
        return blockList;
    }

    /**
     * Get the time of the explosion.
     * 
     * @return The time of the explosion.
     */
    public Date getTime () {
        return time;
    }

    /*
     * Get the distance between the explosion's location and the furthest block.
     */
    private double computeRadius () {
        double r = 0;
        for (Replaceable b : blockList)
        {
            Location bl = b.getBlock ().getLocation ();
            r = Math.max (r, loc.distance (bl));
        }
        return r + 1;
    }

    /**
     * Get the location of the explosion.
     * 
     * @return The location of the explosion.
     */
    public Location getLocation () {
        return loc;
    }

    /**
     * Get the radius of the explosion (i.e. the distance between the location
     * and the furthest block).
     * 
     * @return The radius of the explosion.
     */
    public double getRadius () {
        return radius;
    }

    /*
     * Replace all the blocks in the list.
     */
    protected void replace_blocks () {
        for (Replaceable block : blockList)
            block.replace (true);
        blockList.clear ();

        if (CreeperConfig.teleportOnSuffocate)
            BlockManager.check_player_one_block (loc);
    }

    /**
     * Replace the first block of the list.
     * 
     * @return False if the list is now empty.
     */
    protected boolean replace_one_block () {
        Replaceable block = blockList.poll ();
        block.replace (false);
        BlockManager.check_player_one_block (block.getBlock ().getLocation ());
        return !blockList.isEmpty ();
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals (Object o) {
        if (!(o instanceof CreeperExplosion))
            return false;
        CreeperExplosion e = (CreeperExplosion) o;
        return e.time == time && e.loc == loc && e.radius == radius;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode () {
        return (int) (time.hashCode () + radius + loc.hashCode ());
    }

    /*
     * Check for dependent blocks and record them first.
     */
    private void recordBlocks (List<Block> blocks) {
        if (!blocks.isEmpty ())
        {
            Iterator<Block> iter = blocks.iterator ();
            while (iter.hasNext ())
            {
                Block b = iter.next ();
                if (CreeperBlock.isDependent (b.getTypeId ()))
                {
                    record (b);
                    iter.remove ();
                }
            }
            for (Block b : blocks)
                record (b);
        }
    }

    /*
     * In case of possible obsidian destruction, check for obsidian around, and
     * give them a chance to be destroyed.
     */
    private void checkForObsidian () {
        int radius = CreeperConfig.obsidianRadius;
        double chance = ((float) CreeperConfig.obsidianChance) / 100;
        World w = loc.getWorld ();

        Random r = new Random (System.currentTimeMillis ());

        for (int i = loc.getBlockX () - radius; i < loc.getBlockX () + radius; i++)
            for (int j = Math.max (0, loc.getBlockY () - radius); j < Math.min (w.getMaxHeight (), loc.getBlockY () + radius); j++)
                for (int k = loc.getBlockZ () - radius; k < loc.getBlockZ () + radius; k++)
                {
                    Location l = new Location (w, i, j, k);
                    if (l.distance (loc) > radius)
                        continue;
                    Block b = l.getBlock ();
                    if (b.getType () == Material.OBSIDIAN && r.nextDouble () < chance)
                        record (b);
                }
    }

    /*
     * Record one block and remove it. If it is protected, add to the
     * replace-immediately list. Check for dependent blocks around.
     */
    private void record (Block block) {
        if (block.getType () == Material.AIR)
            return;

        if ((CreeperConfig.preventChainReaction && block.getType ().equals (Material.TNT))
                || (CreeperConfig.replaceProtectedChests && PluginHandler.isProtected (block) || world.isProtected (block)))
        {
            CreeperBlock b = CreeperBlock.newBlock (block.getState ());
            if (b != null)
            {
                BlockManager.addToReplace (b);
                b.remove ();
            }
            return;
        }

        BlockId id = new BlockId (block);
        if (world.blockWhiteList.contains (id) || !world.blockBlackList.contains (id))
        {
            // The block should be replaced.
            CreeperBlock cBlock = CreeperBlock.newBlock (block.getState ());

            for (NeighborBlock b : cBlock.getNeighbors ())
            {
                if (b.isNeighbor ())
                    record (b.getBlock ());
            }

            CreeperBlock b = CreeperBlock.newBlock (block.getState ());
            if (b != null)
            {
                blockList.add (b);
                b.remove ();
            }
        }
        else if (CreeperConfig.dropDestroyedBlocks)
        {
            // The block should not be replaced, check if it drops
            Random generator = new Random ();
            if (generator.nextInt (100) < CreeperConfig.dropChance) //percentage
                CreeperBlock.newBlock (block.getState ()).drop ();
            block.setType (Material.AIR);

        }

    }

    /**
     * Check if the explosion has blocks to repair, and repair them (or one of
     * them in case of block per block).
     * 
     * @return False if the explosion has not started its replacements yet
     *         because it is not time.
     */
    public boolean checkReplace () {
        Date now = new Date ();
        Date after = new Date (time.getTime () + CreeperConfig.waitBeforeHeal * 1000);
        if (after.before (now))
        {
            if (CreeperConfig.blockPerBlock)
                replace_one_block ();
            else
                replace_blocks ();
            return true;

        }
        else
            return timed;
    }

    /**
     * Get whether the list of blocks to be replaced is empty.
     * 
     * @return Whether the list is empty.
     */
    public boolean isEmpty () {
        return blockList.isEmpty ();
    }

}

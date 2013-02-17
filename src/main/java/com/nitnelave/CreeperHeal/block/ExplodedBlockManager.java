package com.nitnelave.CreeperHeal.block;

import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
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
 * Manager for the explosions and the resulting blocks to be replaced.
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

    private final static BlockFace[] CARDINALS = {BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST, BlockFace.NORTH, BlockFace.UP, BlockFace.DOWN};

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
        Iterator<CreeperExplosion> iter = explosionList.iterator ();
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
            Iterator<CreeperExplosion> iter = explosionList.iterator ();
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
        processExplosion (event.blockList (), event.getLocation (), event.getEntity (), world.isRepairTimed ());
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
        processExplosion (list, location, null, CreeperConfig.loadWorld (location.getWorld ()).isRepairTimed ());
    }

    /*
     * Record the blocks in the list and remove them from the world so they
     * don't drop.
     */
    //TODO: Ascending rails pop if their support is gone. Maybe related to dependent blocks.
    //TODO: Check several blocks for bed (protection, dropping...)
    protected static void processExplosion (List<Block> blocks, Location location, Entity entity, boolean timed) {
        if (PluginHandler.isInArena (location))
            return;

        Date now = timed ? new Date (new Date ().getTime () + 1200000) : new Date ();
        List<Replaceable> blockList = new LinkedList<Replaceable> ();

        recordBlocks (blocks, blockList);

        if (CreeperConfig.explodeObsidian)
            checkForObsidian (location, blockList);

        Collections.sort (blockList, new CreeperComparator ());

        CreeperExplosion cEx = new CreeperExplosion (now, blockList, location);

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

    //TODO: Move the world loading to the record method?
    /*
     * Check for dependent blocks and record them first.
     */
    private static void recordBlocks (List<Block> blocks, List<Replaceable> blockList) {
        if (!blocks.isEmpty ())
        {
            WorldConfig world = CreeperConfig.loadWorld (blocks.get (0).getWorld ());
            Iterator<Block> iter = blocks.iterator ();
            while (iter.hasNext ())
            {
                Block b = iter.next ();
                if (CreeperBlock.isDependent (b.getTypeId ()))
                {
                    record (b, blockList, world);
                    iter.remove ();
                }
            }
            for (Block b : blocks)
                record (b, blockList, world);
        }
    }

    /*
     * In case of possible obsidian destruction, check for obsidian around, and
     * give them a chance to be destroyed.
     */
    private static void checkForObsidian (Location location, List<Replaceable> listState) {
        int radius = CreeperConfig.obsidianRadius;
        double chance = ((float) CreeperConfig.obsidianChance) / 100;
        World w = location.getWorld ();
        WorldConfig wcfg = CreeperConfig.loadWorld (w);

        Random r = new Random (System.currentTimeMillis ());

        for (int i = location.getBlockX () - radius; i < location.getBlockX () + radius; i++)
            for (int j = Math.max (0, location.getBlockY () - radius); j < Math.min (w.getMaxHeight (), location.getBlockY () + radius); j++)
                for (int k = location.getBlockZ () - radius; k < location.getBlockZ () + radius; k++)
                {
                    Location l = new Location (w, i, j, k);
                    if (l.distance (location) > radius)
                        continue;
                    Block b = w.getBlockAt (l);
                    if (b.getType () == Material.OBSIDIAN && r.nextDouble () < chance)
                        record (b, listState, wcfg);
                }
    }

    /*
     * Record one block and remove it. If it is protected, add to the
     * replace-immediately list. Check for dependent blocks around.
     */
    //TODO: Check several blocks for protection, etc in case of bed, door. Should be delegated to CreeperBlock?
    private static void record (Block block, List<Replaceable> listState, WorldConfig world) {
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

            for (BlockFace face : CARDINALS)
            {
                Block b = block.getRelative (face);
                CreeperBlock cb = CreeperBlock.newBlock (b.getState ());
                if (cb != null && cb.getAttachingFace () == face.getOppositeFace ())
                    record (b, listState, world);
            }

            CreeperBlock b = CreeperBlock.newBlock (block.getState ());
            if (b != null)
            {
                listState.add (b);
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

    //TODO: date.
    /**
     * Check to see if any block has to be replaced in the explosions.
     */
    public static void checkReplace () { //check to see if any block has to be replaced
        Date now = new Date ();

        Iterator<CreeperExplosion> iter = explosionList.iterator ();
        while (iter.hasNext ())
        {
            CreeperExplosion cEx = iter.next ();
            Date time = cEx.getTime ();
            List<Replaceable> blockList = cEx.getBlockList ();
            Date after = new Date (time.getTime () + CreeperConfig.waitBeforeHeal * 1000);
            if (after.before (now))
            {
                if (CreeperConfig.blockPerBlock)
                {
                    if (!blockList.isEmpty () && !cEx.replace_one_block ())
                    {
                        if (!CreeperConfig.lightweightMode)
                            explosionIndex.removeElement (cEx, cEx.getLocation ().getX (), cEx.getLocation ().getZ ());
                        iter.remove ();
                    }
                }
                else
                {
                    cEx.replace_blocks ();
                    iter.remove ();
                }

            }
            else
                break;
        }
        HangingsManager.replaceHangings (now);

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
     * Clean the explosion map from useless exmpty explosions. Do not use when
     * in light weight mode.
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

package com.nitnelave.CreeperHeal.block;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.block.NoteBlock;
import org.bukkit.block.Sign;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Attachable;

import com.nitnelave.CreeperHeal.CreeperHeal;
import com.nitnelave.CreeperHeal.PluginHandler;
import com.nitnelave.CreeperHeal.config.CfgVal;
import com.nitnelave.CreeperHeal.config.CreeperConfig;
import com.nitnelave.CreeperHeal.utils.CreeperUtils;

/**
 * Represents a block that can be replaced. Every special type of block derives
 * from this class, and can only be constructed by using the newBlock method.
 * 
 * @author nitnelave
 * 
 */
public class CreeperBlock implements Replaceable {

    /*
     * These blocks (may) need a block under them not to drop.
     */
    private final static Set<Integer> DEPENDENT_DOWN_BLOCKS = CreeperUtils.createFinalHashSet (6, 26, 27, 28, 31, 32, 37, 38, 39, 40, 55, 59, 63, 64, 66, 70,
            71, 72, 78, 81, 83, 93, 94, 104, 105, 115, 117, 132, 140, 141, 142);
    /*
     * These blocks are dependent on another block
     */
    private final static Set<Integer> DEPENDENT_BLOCKS = CreeperUtils.createFinalHashSet (50, 65, 68, 69, 75, 76, 77, 96, 106, 127, 131, 143);
    /*
     * Blocks a player can breathe in and that are replaced by other blocks.
     */
    protected final static Set<Integer> EMPTY_BLOCKS = CreeperUtils.createFinalHashSet (0, 8, 9, 10, 11, 51, 78);

    public final static BlockFace[] CARDINALS = {BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST, BlockFace.NORTH, BlockFace.UP, BlockFace.DOWN};

    /*
     * The block represented.
     */
    protected BlockState blockState;

    /**
     * Create a new CreeperBlock of the right class. Factory method that should
     * be used as a constructor.
     * 
     * @param blockState
     *            The block to be represented.
     * @return A new CreeperBlock of the right subclass.
     */
    public static CreeperBlock newBlock (BlockState blockState) {
        if (PluginHandler.isSpoutEnabled () && SpoutBlock.isCustomBlock (blockState))
            return new SpoutBlock (blockState);
        if (blockState instanceof InventoryHolder)
            return new CreeperChest (blockState);
        if (blockState.getType ().hasGravity ())
            return new CreeperPhysicsBlock (blockState);
        switch (blockState.getType ())
        {
            case BED_BLOCK:
                return new CreeperBed (blockState);
            case RAILS:
            case POWERED_RAIL:
            case DETECTOR_RAIL:
                return new CreeperRail (blockState);
            case SKULL:
                return new CreeperHead (blockState);
            case PISTON_BASE:
            case PISTON_STICKY_BASE:
            case PISTON_EXTENSION:
                return new CreeperPiston (blockState);
            case WOODEN_DOOR:
            case IRON_DOOR_BLOCK:
                return new CreeperDoor (blockState);
            case NOTE_BLOCK:
                return new CreeperNoteBlock ((NoteBlock) blockState);
            case SIGN_POST:
            case WALL_SIGN:
                return new CreeperSign ((Sign) blockState);
            case MOB_SPAWNER:
                return new CreeperMonsterSpawner ((CreatureSpawner) blockState);
            case WOOD_PLATE:
            case STONE_PLATE:
                return new CreeperPlate (blockState);
            case GRASS:
                return new CreeperGrass (blockState);
            case SMOOTH_BRICK:
            case SMOOTH_STAIRS:
                return new CreeperBrick (blockState);
            case WOOD_BUTTON:
            case STONE_BUTTON:
                return new CreeperButton (blockState);
            case TNT:
            case FIRE:
            case AIR:
                return null;
            default:
                return new CreeperBlock (blockState);
        }
    }

    /*
     * The constructor.
     */
    protected CreeperBlock (BlockState blockState) {
        this.blockState = blockState;
    }

    protected CreeperBlock () {
    }

    /**
     * Replace the block in the world.
     */
    public void update () {
        blockState.update (true);
    }

    /*
     * (non-Javadoc)
     * @see com.nitnelave.CreeperHeal.block.Replaceable#getLocation()
     */
    @Override
    public Location getLocation () {
        return blockState.getLocation ();
    }

    /*
     * (non-Javadoc)
     * @see com.nitnelave.CreeperHeal.block.Replaceable#getWorld()
     */
    @Override
    public World getWorld () {
        return blockState.getWorld ();
    }

    /*
     * (non-Javadoc)
     * @see com.nitnelave.CreeperHeal.block.Replaceable#getBlock()
     */
    @Override
    public Block getBlock () {
        return blockState.getBlock ();
    }

    /*
     * (non-Javadoc)
     * @see com.nitnelave.CreeperHeal.block.Replaceable#getTypeId()
     */
    @Override
    public int getTypeId () {
        return blockState.getTypeId ();
    }

    /**
     * Get the block's raw data.
     * 
     * @return The block's raw data.
     */
    public byte getRawData () {
        return blockState.getRawData ();
    }

    /**
     * Drop the corresponding items on the ground.
     * 
     * @param forced
     *            If false, the block will have a chance to drop, according to
     *            the configuration value of the drop chance. If true, the block
     *            drops every time.
     * @return True if the block dropped.
     */
    @Override
    public boolean drop (boolean forced) {
        if (forced || new Random ().nextInt (100) < CreeperConfig.getInt (CfgVal.DROP_CHANCE))
        {
            Location loc = blockState.getBlock ().getLocation ();
            World w = loc.getWorld ();

            Collection<ItemStack> drop = blockState.getBlock ().getDrops ();
            for (ItemStack s : drop)
                w.dropItemNaturally (loc, s);
            return true;
        }
        return false;
    }

    /*
     * (non-Javadoc)
     * @see com.nitnelave.CreeperHeal.block.Replaceable#replace(boolean)
     */
    @Override
    public final boolean replace (boolean shouldDrop) {
        if (checkForDrop (getBlock ()))
            return true;

        if (!shouldDrop && isDependent (getTypeId ()) && isEmpty (getBlock ().getRelative (getAttachingFace ()).getTypeId ()))
            return false;
        else
            update ();

        checkForAscendingRails ();

        return true;
    }

    protected boolean checkForDrop (Block block) {
        int blockId = block.getTypeId ();

        if (!CreeperConfig.getBool (CfgVal.OVERWRITE_BLOCKS) && !isEmpty (blockId))
        {
            if (CreeperConfig.getBool (CfgVal.DROP_DESTROYED_BLOCKS))
                drop (true);
            return true;
        }
        else if (CreeperConfig.getBool (CfgVal.OVERWRITE_BLOCKS) && !isEmpty (blockId) && CreeperConfig.getBool (CfgVal.DROP_DESTROYED_BLOCKS))
        {
            CreeperBlock b = CreeperBlock.newBlock (block.getState ());
            if (b != null)
            {
                b.drop (true);
                b.remove ();
            }
        }
        return false;

    }

    /*
     * Get whether the block is empty, i.e. if a player can breathe inside it
     * and if it can be replaced by other blocks (snow, water...)
     */
    private static boolean isEmpty (int typeId) {
        return EMPTY_BLOCKS.contains (typeId);
    }

    /*
     * (non-Javadoc)
     * @see com.nitnelave.CreeperHeal.block.Replaceable#delayReplacement()
     */
    @Override
    public void delayReplacement () {
        long delay = CreeperConfig.getInt (CfgVal.BLOCK_PER_BLOCK_INTERVAL);
        DelayReplacement dr = new DelayReplacement (this, 0);
        int id = Bukkit.getServer ().getScheduler ().scheduleSyncRepeatingTask (CreeperHeal.getInstance (), dr, delay, delay);
        dr.setId (id);
    }

    /**
     * Get whether blocks of a type are dependent on the block under.
     * 
     * @param typeId
     *            The type of the block.
     * @return Whether the block is dependent.
     */
    private static boolean isDependentDown (int typeId) {
        return DEPENDENT_DOWN_BLOCKS.contains (typeId);
    }

    /**
     * Get whether blocks of a type are solid.
     * 
     * @param typeId
     *            The type of the block.
     * @return Whether the block is solid.
     */
    public static boolean isSolid (int typeId) {
        return Material.getMaterial (typeId).isSolid ();
    }

    /**
     * Get whether blocks of a type are solid.
     * 
     * @param block
     *            The type of the block.
     * @return Whether the block is solid.
     */
    public static boolean isSolid (Block block) {
        return block.getType ().isSolid ();
    }

    /**
     * Get whether blocks of a type are dependent on another block .
     * 
     * @param typeId
     *            The type of the block.
     * @return Whether the block is dependent.
     */
    public static boolean isDependent (int typeId) {
        return DEPENDENT_BLOCKS.contains (typeId) || isDependentDown (typeId);
    }

    /*
     * Test the blocks directly in contact, and if they are ascending rails, add
     * them to the updatePrevention list.
     */
    private void checkForAscendingRails () {
        Block block = blockState.getBlock ();
        for (BlockFace face : CARDINALS)
        {
            if (face == BlockFace.DOWN)
                continue;
            CreeperBlock cb = CreeperBlock.newBlock (block.getRelative (face).getState ());
            if (cb instanceof CreeperRail)
            {
                CreeperRail r = (CreeperRail) cb;
                if (r.isAscending ())
                    RailsIndex.putUpdatePrevention (r);
            }
        }
    }

    /*
     * (non-Javadoc)
     * @see com.nitnelave.CreeperHeal.block.Replaceable#getAttachingFace()
     */
    @Override
    public BlockFace getAttachingFace () {
        if (blockState.getData () instanceof Attachable)
            return ((Attachable) blockState.getData ()).getAttachedFace ();
        if (isDependentDown (blockState.getTypeId ()))
            return BlockFace.DOWN;
        return BlockFace.SELF;
    }

    /*
     * (non-Javadoc)
     * @see com.nitnelave.CreeperHeal.block.Replaceable#remove()
     */
    @Override
    public void remove () {
        getBlock ().setType (Material.AIR);
    }

    /*
     * (non-Javadoc)
     * @see com.nitnelave.CreeperHeal.block.Replaceable#isDependent()
     */
    @Override
    public boolean isDependent () {
        return getAttachingFace () != BlockFace.SELF;
    }

    /**
     * Get the list of blocks that are possibly dependent on this block. To
     * check if they really are, simply check that neighborBlock.isNeighbor() is
     * true.
     * 
     * @return The list of potentially dependent blocks.
     */
    public List<NeighborBlock> getDependentNeighbors () {
        List<NeighborBlock> neighbors = new ArrayList<NeighborBlock> ();
        Block block = getBlock ();
        for (BlockFace face : CARDINALS)
            neighbors.add (new NeighborBlock (block.getRelative (face), face));
        return neighbors;
    }

}

package com.nitnelave.CreeperHeal.block;

import com.nitnelave.CreeperHeal.config.CfgVal;
import com.nitnelave.CreeperHeal.config.CreeperConfig;
import com.nitnelave.CreeperHeal.utils.CreeperTag;
import com.nitnelave.CreeperHeal.utils.CreeperUtils;
import com.nitnelave.CreeperHeal.utils.ShortLocation;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.World;
import org.bukkit.block.Banner;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Container;
import org.bukkit.block.Jukebox;
import org.bukkit.block.ShulkerBox;
import org.bukkit.block.data.Bisected;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Rail;
import org.bukkit.block.data.type.Piston;
import org.bukkit.block.data.type.PistonHead;
import org.bukkit.block.data.type.Stairs;
import org.bukkit.block.data.type.TrapDoor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Attachable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Represents a block that can be replaced. Every special type of block derives
 * from this class, and can only be constructed by using the newBlock method.
 *
 * @author nitnelave
 *
 */
public class CreeperBlock implements Replaceable
{

    /*
     * Blocks a player can breathe in and that are replaced by other blocks.
     */
    private static final Set<Material> EMPTY_BLOCKS =
            CreeperUtils.createFinalHashSet(Material.AIR, Material.WATER, Material.CAVE_AIR,
                    Material.LAVA, Material.VOID_AIR, Material.FIRE, Material.SNOW);
    /*
     * These blocks (may) need a block under them not to drop.
     */
    private static final Set<Material> DEPENDENT_DOWN_BLOCKS =
            CreeperUtils.createFinalHashSet(Material.BEETROOTS, Material.BROWN_MUSHROOM, Material.CACTUS,
                    Material.CARROTS, Material.CHORUS_FLOWER, Material.CHORUS_PLANT, Material.COMPARATOR,
                    Material.DEAD_BUSH, Material.FERN, Material.GRASS, Material.HEAVY_WEIGHTED_PRESSURE_PLATE,
                    Material.LARGE_FERN, Material.LIGHT_WEIGHTED_PRESSURE_PLATE, Material.LILY_PAD, Material.MELON_STEM,
                    Material.NETHER_WART, Material.NETHER_WART_BLOCK, Material.POTATOES, Material.PUMPKIN_STEM,
                    Material.RED_MUSHROOM, Material.REDSTONE_WIRE, Material.REPEATER, Material.SIGN, Material.SNOW,
                    Material.STONE_PRESSURE_PLATE, Material.SEAGRASS, Material.SUGAR_CANE, Material.SUGAR_CANE,
                    Material.TALL_GRASS, Material.TALL_SEAGRASS, Material.TRIPWIRE, Material.WHEAT);
    /*
     * These blocks are dependent on another block
     */
    private static final Set<Material> DEPENDENT_BLOCKS =
            CreeperUtils.createFinalHashSet(Material.TORCH, Material.LADDER, Material.WALL_SIGN, Material.LEVER,
                    Material.REDSTONE_TORCH, Material.VINE, Material.COCOA, Material.TRIPWIRE_HOOK);

    public static final BlockFace[] CARDINALS = { BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST,
            BlockFace.NORTH, BlockFace.UP, BlockFace.DOWN };

    /*
     * The block represented.
     */
    BlockState blockState;

    /**
     * Create a new CreeperBlock of the right class. Factory method that should
     * be used as a constructor.
     *
     * @param state
     *            The block to be represented.
     * @return A new CreeperBlock of the right subclass.
     */
    public static CreeperBlock newBlock(BlockState state)
    {
        CreeperConfig.getWorld(state.getWorld()).getReplacement(state);

        if (state instanceof ShulkerBox)
            return new CreeperShulkerBox((ShulkerBox) state);
        if (state instanceof Container)
            return new CreeperContainer((Container) state);
        if (state instanceof Banner)
            return new CreeperBanner((Banner) state);
        if (state instanceof Jukebox)
            return new CreeperJukebox((Jukebox) state);
        if (state.getType().hasGravity())
            return new CreeperPhysicsBlock(state);

        BlockData data = state.getBlockData();

        if (data instanceof org.bukkit.block.data.type.Bed)
            return new CreeperBed(state);
        if (data instanceof Bisected && !(data instanceof Stairs) && !(data instanceof TrapDoor))
            return new CreeperBisected(state);
        if (data instanceof Piston || data instanceof PistonHead)
            return new CreeperPiston(state);

        switch (state.getType())
        {
        case AIR:
        case CAVE_AIR:
        case FIRE:
        case VOID_AIR:
            return null;
        case GRASS_BLOCK:
            return new CreeperGrass(state);
        case STONE_BRICKS:
            return new CreeperBrick(state);
        case STONE:
            return new CreeperStone(state);
        default:
            return new CreeperBlock(state);
        }
    }

    /*
     * The constructor.
     */
    CreeperBlock(BlockState blockState)
    {
        this.blockState = blockState;
    }

    /*
     * Get whether the block is empty, i.e. if a player can breathe inside it
     * and if it can be replaced by other blocks (snow, water...)
     */
    static boolean isEmpty(Material type)
    {
        return EMPTY_BLOCKS.contains(type);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.nitnelave.CreeperHeal.block.Replaceable#getLocation()
     */
    @Override
    public Location getLocation()
    {
        return blockState.getLocation();
    }

    /*
     * (non-Javadoc)
     *
     * @see com.nitnelave.CreeperHeal.block.Replaceable#getWorld()
     */
    @Override
    public World getWorld()
    {
        return blockState.getWorld();
    }

    /*
     * (non-Javadoc)
     *
     * @see com.nitnelave.CreeperHeal.block.Replaceable#getBlock()
     */
    @Override
    public Block getBlock()
    {
        return blockState.getBlock();
    }

    /**
     * Get whether blocks of a type are dependent on the block under.
     *
     * @param type
     *            The type of the block.
     * @return Whether the block is dependent.
     */
    private static boolean isDependentDown(Material type)
    {
        if (Tag.SAPLINGS.isTagged(type) || Tag.RAILS.isTagged(type) || Tag.CARPETS.isTagged(type) || Tag.DOORS.isTagged(type)
                || Tag.WOODEN_PRESSURE_PLATES.isTagged(type) || Tag.FLOWER_POTS.isTagged(type)
                || CreeperTag.STANDING_BANNERS.isTagged(type) || CreeperTag.FLOWERS.isTagged(type)
                || CreeperTag.DOUBLE_FLOWERS.isTagged(type)) {
            return true;
        }
        return DEPENDENT_DOWN_BLOCKS.contains(type);
    }

    /**
     * Get whether blocks of a type are solid.
     *
     * @param type
     *            The type of the block.
     * @return Whether the block is solid.
     */
    @SuppressWarnings("unused")
    public static boolean isSolid(Material type)
    {
        return type.isSolid();
    }

    /**
     * Get whether blocks of a type are dependent on another block.
     *
     * @param type
     *            The type of the block.
     * @return Whether the block is dependent.
     */
    public static boolean isDependent(Material type)
    {
        return DEPENDENT_BLOCKS.contains(type) || Tag.BUTTONS.isTagged(type) || CreeperTag.WALL_BANNERS.isTagged(type)
                || isDependentDown(type);
    }

    /**
     * Replace the block in the world.
     */
    public void update()
    {
        getLocation().getChunk().load();
        blockState.update(true, false);
        getWorld().playSound(getLocation(), CreeperConfig.getSound(), CreeperConfig.getInt(CfgVal.SOUND_VOLUME) / 10F,
                ThreadLocalRandom.current().nextFloat() * 2);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.nitnelave.CreeperHeal.block.Replaceable#getType()
     */
    @Override
    public Material getType()
    {
        return blockState.getType();
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
    public boolean drop(boolean forced)
    {
        if (forced || CreeperConfig.shouldDrop())
        {
            BlockState current = blockState.getBlock().getState();
            blockState.update(true, false);
            Collection<ItemStack> drop = blockState.getBlock().getDrops();
            current.update(true, false);
            Location location = blockState.getLocation().add(0.5, 0.5, 0.5);
            World world = blockState.getWorld();
            for (ItemStack itemStack : drop)
                world.dropItemNaturally(location, itemStack);
            return true;
        }
        return false;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.nitnelave.CreeperHeal.block.Replaceable#replace(boolean)
     */
    @Override
    public final boolean replace(boolean shouldDrop)
    {
        if (checkForDrop())
            return true;

        if (!shouldDrop && isDependent(getType())
                && isEmpty(getBlock().getRelative(getAttachingFace()).getType()))
            return false;

        update();
        checkForAscendingRails();

        return true;
    }

    /**
     * Get whether blocks of a type are solid.
     *
     * @param block
     *            The type of the block.
     * @return Whether the block is solid.
     */
    public static boolean isSolid(Block block)
    {
        return block.getType().isSolid();
    }

    boolean checkForDrop()
    {

        Block block = blockState.getBlock();
        Material type = block.getType();

        if (!CreeperConfig.getBool(CfgVal.OVERWRITE_BLOCKS) && !isEmpty(type))
        {
            if (CreeperConfig.getBool(CfgVal.DROP_DESTROYED_BLOCKS))
                drop(true);
            return true;
        } else if (CreeperConfig.getBool(CfgVal.OVERWRITE_BLOCKS) && !isEmpty(type)
                && CreeperConfig.getBool(CfgVal.DROP_DESTROYED_BLOCKS))
        {
            CreeperBlock b = CreeperBlock.newBlock(block.getState());
            if (b == null)
                throw new IllegalArgumentException("Null block for: " + block.getType().toString());
            b.drop(true);
            b.remove();
        }
        return false;

    }

    /*
     * Test the blocks directly in contact, and if they are ascending rails, add
     * them to the updatePrevention list.
     */
    private void checkForAscendingRails()
    {
        Block block = blockState.getBlock();
        for (BlockFace face : CARDINALS)
        {
            if (face == BlockFace.DOWN)
                continue;
            Block relative = block.getRelative(face);
            BlockData blockData = relative.getBlockData();
            if (!(blockData instanceof Rail)) {
                continue;
            }
            if (face == BlockFace.UP || ((Rail) blockData).getShape().name().startsWith("ASCENDING_")) {
                RailsIndex.putUpdatePrevention(relative);
            }
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see com.nitnelave.CreeperHeal.block.Replaceable#getAttachingFace()
     */
    @Override
    public BlockFace getAttachingFace()
    {
        if (blockState.getData() instanceof Attachable)
        {
            return ((Attachable) blockState.getData()).getAttachedFace();
        }
        if (isDependentDown(blockState.getType()))
            return BlockFace.DOWN;
        return BlockFace.SELF;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.nitnelave.CreeperHeal.block.Replaceable#remove()
     */
    @Override
    public void remove()
    {
        getBlock().setType(Material.AIR);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.nitnelave.CreeperHeal.block.Replaceable#isDependent()
     */
    @Override
    public boolean isDependent()
    {
        return getAttachingFace() != BlockFace.SELF;
    }

    /**
     * Get the list of blocks that are possibly dependent on this block. To
     * check if they really are, simply check that neighborBlock.isNeighbor() is
     * true.
     *
     * @return The list of potentially dependent blocks.
     */
    public List<NeighborBlock> getDependentNeighbors()
    {
        List<NeighborBlock> neighbors = new ArrayList<>();
        Block block = getBlock();
        for (BlockFace face : CARDINALS)
            neighbors.add(new NeighborBlock(block.getRelative(face), face));
        return neighbors;
    }

    void record(Collection<ShortLocation> checked)
    {
        checked.add(new ShortLocation(getLocation()));
    }

}

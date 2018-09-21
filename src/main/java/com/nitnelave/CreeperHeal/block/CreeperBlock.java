package com.nitnelave.CreeperHeal.block;

import com.nitnelave.CreeperHeal.config.CfgVal;
import com.nitnelave.CreeperHeal.config.CreeperConfig;
import com.nitnelave.CreeperHeal.utils.CreeperUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.*;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Attachable;
import org.bukkit.material.MaterialData;

import java.util.*;

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
    protected final static Set<Material> EMPTY_BLOCKS =
            CreeperUtils.createFinalHashSet(Material.AIR, Material.WATER, Material.STATIONARY_WATER,
                                            Material.LAVA, Material.STATIONARY_LAVA, Material.FIRE, Material.SNOW);
    /*
     * These blocks (may) need a block under them not to drop.
     */
    private final static Set<Material> DEPENDENT_DOWN_BLOCKS =
            CreeperUtils.createFinalHashSet(Material.SAPLING, Material.BED_BLOCK, Material.POWERED_RAIL,
                                            Material.DETECTOR_RAIL, Material.LONG_GRASS, Material.DEAD_BUSH,
                                            Material.YELLOW_FLOWER, Material.RED_ROSE, Material.BROWN_MUSHROOM,
                                            Material.RED_MUSHROOM, Material.REDSTONE_WIRE, Material.WHEAT,
                                            Material.SIGN_POST, Material.WOODEN_DOOR,
                                            Material.RAILS, Material.STONE_PLATE,
                                            Material.IRON_DOOR_BLOCK, Material.WOOD_PLATE, Material.SNOW,
                                            Material.CACTUS, Material.SUGAR_CANE, Material.SUGAR_CANE_BLOCK,
                                            Material.DIODE_BLOCK_OFF, Material.DIODE_BLOCK_ON, Material.PUMPKIN_STEM,
                                            Material.MELON_STEM, Material.WATER_LILY, Material.NETHER_WART_BLOCK,
                                            Material.NETHER_WARTS,
                                            Material.BREWING_STAND, Material.TRIPWIRE, Material.FLOWER_POT,
                                            Material.CARROT, Material.POTATO, Material.GOLD_PLATE, Material.IRON_PLATE,
                                            Material.REDSTONE_COMPARATOR_OFF, Material.REDSTONE_COMPARATOR_ON,
                                            Material.ACTIVATOR_RAIL, Material.CARPET, Material.DOUBLE_PLANT,
                                            Material.STANDING_BANNER, Material.SPRUCE_DOOR, Material.BIRCH_DOOR,
                                            Material.JUNGLE_DOOR, Material.ACACIA_DOOR, Material.DARK_OAK_DOOR,
                                            Material.CHORUS_PLANT, Material.CHORUS_FLOWER,
                                            Material.BEETROOT_BLOCK);
    /*
     * These blocks are dependent on another block
     */
    private final static Set<Material> DEPENDENT_BLOCKS =
            CreeperUtils.createFinalHashSet(Material.TORCH, Material.LADDER, Material.WALL_SIGN, Material.LEVER,
                                            Material.REDSTONE_TORCH_OFF, Material.REDSTONE_TORCH_ON,
                                            Material.STONE_BUTTON, Material.TRAP_DOOR, Material.VINE, Material.COCOA,
                                            Material.TRIPWIRE_HOOK, Material.WOOD_BUTTON, Material.IRON_TRAPDOOR,
                                            Material.WALL_BANNER);

    public final static BlockFace[] CARDINALS = { BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST,
                                                 BlockFace.NORTH, BlockFace.UP, BlockFace.DOWN };

    private final static Random random = new Random();

    /*
     * The block represented.
     */
    protected BlockState blockState;

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
        if (state instanceof InventoryHolder)
            return new CreeperContainer(state);
        if (state.getType().hasGravity())
            return new CreeperPhysicsBlock(state);
        switch (state.getType())
        {
        case BED_BLOCK:
            return new CreeperBed(state);
        case DOUBLE_PLANT:
            return new CreeperFlower(state);
        case RAILS:
        case POWERED_RAIL:
        case DETECTOR_RAIL:
            return new CreeperRail(state);
        case SKULL:
            return new CreeperHead(state);
        case PISTON_BASE:
        case PISTON_STICKY_BASE:
        case PISTON_EXTENSION:
            return new CreeperPiston(state);
        case WOODEN_DOOR:
        case ACACIA_DOOR:
        case BIRCH_DOOR:
        case DARK_OAK_DOOR:
        case JUNGLE_DOOR:
        case SPRUCE_DOOR:
        case IRON_DOOR_BLOCK:
            return new CreeperDoor(state);
        case NOTE_BLOCK:
            return new CreeperNoteBlock((NoteBlock) state);
        case SIGN_POST:
        case WALL_SIGN:
            return new CreeperSign((Sign) state);
        case MOB_SPAWNER:
            return new CreeperMonsterSpawner((CreatureSpawner) state);
        case WOOD_PLATE:
        case GOLD_PLATE:
        case IRON_PLATE:
        case STONE_PLATE:
            return new CreeperPlate(state);
        case GRASS:
            return new CreeperGrass(state);
        case SMOOTH_BRICK:
        case SMOOTH_STAIRS:
            return new CreeperBrick(state);
        case WOOD_BUTTON:
        case STONE_BUTTON:
            return new CreeperButton(state);
        case FIRE:
        case AIR:
            return null;
        case STANDING_BANNER:
        case WALL_BANNER:
            return new CreeperBanner((Banner) state);
        case STONE:
            return new CreeperStone(state);
        default:
            return new CreeperBlock(state);
        }
    }

    /*
     * The constructor.
     */
    protected CreeperBlock(BlockState blockState)
    {
        this.blockState = blockState;
    }

    /*
     * Get whether the block is empty, i.e. if a player can breathe inside it
     * and if it can be replaced by other blocks (snow, water...)
     */
    protected static boolean isEmpty(Material type)
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
     * Get whether blocks of a type are dependent on another block .
     *
     * @param type
     *            The type of the block.
     * @return Whether the block is dependent.
     */
    public static boolean isDependent(Material type)
    {
        return DEPENDENT_BLOCKS.contains(type) || isDependentDown(type);
    }

    /**
     * Replace the block in the world.
     */
    public void update()
    {
        blockState.update(true, false);
        getWorld().playSound(getLocation(), CreeperConfig.getSound(), CreeperConfig.getInt(CfgVal.SOUND_VOLUME) / 10F, random.nextFloat() * 2);
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
            Location loc = blockState.getBlock().getLocation();
            World w = loc.getWorld();

            Collection<ItemStack> drop = blockState.getBlock().getDrops();
            for (ItemStack s : drop)
                w.dropItemNaturally(loc, s);
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

    protected boolean checkForDrop()
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
            Block rel = block.getRelative(face);
            if (CreeperRail.RAIL_TYPES.contains(rel.getType()))
            {
                CreeperBlock cb = CreeperBlock.newBlock(rel.getState());
                CreeperRail r = (CreeperRail) cb;
                assert r != null;
                if (r.isAscending())
                    RailsIndex.putUpdatePrevention(r);
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
        List<NeighborBlock> neighbors = new ArrayList<NeighborBlock>();
        Block block = getBlock();
        for (BlockFace face : CARDINALS)
            neighbors.add(new NeighborBlock(block.getRelative(face), face));
        return neighbors;
    }

    protected <T extends MaterialData> T castData(BlockState b, Class<T> c)
    {
        MaterialData data = b.getData();
        if (c.isInstance(data))
            return c.cast(data);
        throw new IllegalArgumentException("Invalid block castData: " + data.getClass().toString() +
                                           ", data for a " + b.getType().toString() + ", is not a " + c.toString());
    }

}

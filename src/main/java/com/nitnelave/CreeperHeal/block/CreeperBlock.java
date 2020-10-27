package com.nitnelave.CreeperHeal.block;

import com.nitnelave.CreeperHeal.config.CfgVal;
import com.nitnelave.CreeperHeal.config.CreeperConfig;
import com.nitnelave.CreeperHeal.utils.CreeperUtils;
import com.nitnelave.CreeperHeal.utils.ShortLocation;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Banner;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Bisected;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.block.Jukebox;
import org.bukkit.block.ShulkerBox;
import org.bukkit.block.Sign;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Attachable;
import org.bukkit.material.MaterialData;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.Set;

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
    private final static Set<Material> EMPTY_BLOCKS = CreeperUtils.createFinalHashSet(Material.AIR,
        Material.WATER, Material.LAVA,  Material.FIRE, Material.SNOW);
    /*
     * These blocks (may) need a block under them not to drop.
     */
    private final static Set<Material> DEPENDENT_DOWN_BLOCKS =
            CreeperUtils.createFinalHashSet(
                Material.ACACIA_SAPLING,
                Material.BAMBOO_SAPLING,
                Material.BIRCH_SAPLING,
                Material.DARK_OAK_SAPLING,
                Material.JUNGLE_SAPLING,
                Material.OAK_SAPLING,
                Material.SPRUCE_SAPLING,
                Material.POWERED_RAIL,

                Material.WHITE_CARPET,
                Material.ORANGE_CARPET,
                Material.MAGENTA_CARPET,
                Material.LIGHT_BLUE_CARPET,
                Material.YELLOW_CARPET,
                Material.LIME_CARPET,
                Material.PINK_CARPET,
                Material.GRAY_CARPET,
                Material.LIGHT_GRAY_CARPET,
                Material.CYAN_CARPET,
                Material.PURPLE_CARPET,
                Material.BLUE_CARPET,
                Material.BROWN_CARPET,
                Material.GREEN_CARPET,
                Material.RED_CARPET,
                Material.BLACK_CARPET,

                Material.WHITE_BANNER,
                Material.ORANGE_BANNER,
                Material.MAGENTA_BANNER,
                Material.LIGHT_BLUE_BANNER,
                Material.YELLOW_BANNER,
                Material.LIME_BANNER,
                Material.PINK_BANNER,
                Material.GRAY_BANNER,
                Material.LIGHT_GRAY_BANNER,
                Material.CYAN_BANNER,
                Material.PURPLE_BANNER,
                Material.BLUE_BANNER,
                Material.BROWN_BANNER,
                Material.GREEN_BANNER,
                Material.RED_BANNER,
                Material.BLACK_BANNER,

                Material.DANDELION,
                Material.POPPY,
                Material.BLUE_ORCHID,
                Material.ALLIUM,
                Material.AZURE_BLUET,
                Material.RED_TULIP,
                Material.ORANGE_TULIP,
                Material.WHITE_TULIP,
                Material.PINK_TULIP,
                Material.OXEYE_DAISY,
                Material.CORNFLOWER,
                Material.LILY_OF_THE_VALLEY,
                Material.WITHER_ROSE,
                Material.SUNFLOWER,
                Material.LILAC,
                Material.ROSE_BUSH,
                Material.PEONY,

                Material.ACACIA_SIGN,
                Material.BIRCH_SIGN,
                Material.DARK_OAK_SIGN,
                Material.JUNGLE_SIGN,
                Material.OAK_SIGN,
                Material.SPRUCE_SIGN,
                Material.WARPED_SIGN,
                Material.CRIMSON_SIGN,

                Material.ACACIA_PRESSURE_PLATE,
                Material.BIRCH_PRESSURE_PLATE,
                Material.DARK_OAK_PRESSURE_PLATE,
                Material.JUNGLE_PRESSURE_PLATE,
                Material.OAK_PRESSURE_PLATE,
                Material.SPRUCE_PRESSURE_PLATE,
                Material.WARPED_PRESSURE_PLATE,
                Material.CRIMSON_PRESSURE_PLATE,

                Material.ACACIA_DOOR,
                Material.BIRCH_DOOR,
                Material.DARK_OAK_DOOR,
                Material.JUNGLE_DOOR,
                Material.OAK_DOOR,
                Material.SPRUCE_DOOR,
                Material.WARPED_DOOR,
                Material.CRIMSON_DOOR,
                                            Material.DETECTOR_RAIL, Material.TALL_GRASS, Material.DEAD_BUSH,
                                            Material.BROWN_MUSHROOM,
                                            Material.RED_MUSHROOM, Material.REDSTONE_WIRE, Material.WHEAT,

                                            Material.RAIL, Material.POLISHED_BLACKSTONE_PRESSURE_PLATE, Material.STONE_PRESSURE_PLATE,
                                            Material.IRON_DOOR, Material.SNOW,
                                            Material.CACTUS, Material.SUGAR_CANE,
                                            Material.REPEATER, Material.PUMPKIN_STEM,
                                            Material.MELON_STEM, Material.LILY_PAD, Material.NETHER_WART_BLOCK,
                                            Material.NETHER_WART,
                                            Material.BREWING_STAND, Material.TRIPWIRE, Material.FLOWER_POT,
                                            Material.CARROT, Material.POTATO, Material.HEAVY_WEIGHTED_PRESSURE_PLATE, Material.LIGHT_WEIGHTED_PRESSURE_PLATE,
                                            Material.COMPARATOR,
                                            Material.ACTIVATOR_RAIL,

                                            Material.CHORUS_PLANT, Material.CHORUS_FLOWER,
                                            Material.BEETROOTS);
    /*
     * These blocks are dependent on another block
     */
    private final static Set<Material> DEPENDENT_BLOCKS =
            CreeperUtils.createFinalHashSet(Material.TORCH, Material.LADDER, Material.LEVER,
                                            Material.REDSTONE_TORCH,
                                            Material.STONE_BUTTON, Material.VINE, Material.COCOA,
                Material.ACACIA_TRAPDOOR,
                Material.BIRCH_TRAPDOOR,
                Material.DARK_OAK_TRAPDOOR,
                Material.JUNGLE_TRAPDOOR,
                Material.OAK_TRAPDOOR,
                Material.SPRUCE_TRAPDOOR,
                Material.WARPED_TRAPDOOR,
                Material.CRIMSON_TRAPDOOR,

                Material.WHITE_BED,
                Material.ORANGE_BED,
                Material.MAGENTA_BED,
                Material.LIGHT_BLUE_BED,
                Material.YELLOW_BED,
                Material.LIME_BED,
                Material.PINK_BED,
                Material.GRAY_BED,
                Material.LIGHT_GRAY_BED,
                Material.CYAN_BED,
                Material.PURPLE_BED,
                Material.BLUE_BED,
                Material.BROWN_BED,
                Material.GREEN_BED,
                Material.RED_BED,
                Material.BLACK_BED,

                Material.ACACIA_WALL_SIGN,
                Material.BIRCH_WALL_SIGN,
                Material.DARK_OAK_WALL_SIGN,
                Material.JUNGLE_WALL_SIGN,
                Material.OAK_WALL_SIGN,
                Material.SPRUCE_WALL_SIGN,
                Material.WARPED_WALL_SIGN,
                Material.CRIMSON_WALL_SIGN,
                Material.ACACIA_BUTTON,
                Material.BIRCH_BUTTON,
                Material.DARK_OAK_BUTTON,
                Material.JUNGLE_BUTTON,
                Material.OAK_BUTTON,
                Material.SPRUCE_BUTTON,
                Material.WARPED_BUTTON,
                Material.CRIMSON_BUTTON,

                Material.WHITE_WALL_BANNER,
                Material.ORANGE_WALL_BANNER,
                Material.MAGENTA_WALL_BANNER,
                Material.LIGHT_BLUE_WALL_BANNER,
                Material.YELLOW_WALL_BANNER,
                Material.LIME_WALL_BANNER,
                Material.PINK_WALL_BANNER,
                Material.GRAY_WALL_BANNER,
                Material.LIGHT_GRAY_WALL_BANNER,
                Material.CYAN_WALL_BANNER,
                Material.PURPLE_WALL_BANNER,
                Material.BLUE_WALL_BANNER,
                Material.BROWN_WALL_BANNER,
                Material.GREEN_WALL_BANNER,
                Material.RED_WALL_BANNER,
                Material.BLACK_WALL_BANNER,

                Material.PISTON_HEAD,

                                            Material.TRIPWIRE_HOOK, Material.IRON_TRAPDOOR);

    public final static BlockFace[] CARDINALS = { BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST,
                                                 BlockFace.NORTH, BlockFace.UP, BlockFace.DOWN };

    private final static Random random = new Random();

    /*
     * The block represented.
     */
    BlockState blockState;
    BlockData blockData;

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
        if (state instanceof InventoryHolder)
            return new CreeperContainer((InventoryHolder)state);
        //if (state instanceof Jukebox)
        //    return new CreeperJukebox((Jukebox) state);
        if (state.getBlockData() instanceof Bisected)
            return new CreeperBisected(state, (Bisected) state.getBlockData());
        if (state.getType().hasGravity())
            return new CreeperPhysicsBlock(state);
        switch (state.getType())
        {
          case WHITE_BED:
          case ORANGE_BED:
          case MAGENTA_BED:
          case LIGHT_BLUE_BED:
          case YELLOW_BED:
          case LIME_BED:
          case PINK_BED:
          case GRAY_BED:
          case LIGHT_GRAY_BED:
          case CYAN_BED:
          case PURPLE_BED:
          case BLUE_BED:
          case BROWN_BED:
          case GREEN_BED:
          case RED_BED:
          case BLACK_BED:
            return new CreeperBed(state);
          case RAIL:
          case POWERED_RAIL:
          case DETECTOR_RAIL:
              return new CreeperRail(state);
          case PISTON:
          case PISTON_HEAD:
          case STICKY_PISTON:
              return new CreeperPiston(state);
          case GRASS:
              return new CreeperGrass(state);

              /*
          case SMOOTH_BRICK:
          case SMOOTH_STAIRS:
              return new CreeperBrick(state);
              */
          case STONE:
              return new CreeperStone(state);
          case FIRE:
          case AIR:
          case TNT:
              return null;
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
        this.blockData = blockState.getBlockData();
    }

    /*
     * Get whether the block is empty, i.e. if a player can breathe inside it
     * and if it can be replaced by other blocks (snow, water...)
     */
    static boolean isEmpty(Material type)
    {
        return type.isAir() || EMPTY_BLOCKS.contains(type);
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
     * Get whether blocks of a type are dependent on another block.
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
      update(true);
    }

    protected void update(boolean physics) {
      update(blockData, physics);
    }

    protected void update(BlockData blockData, boolean physics)
    {
        getLocation().getChunk().load();
        getBlock().setBlockData(blockData, physics);
        if (physics) getBlock().getState().update(true);
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

    void record(Collection<ShortLocation> checked)
    {
        checked.add(new ShortLocation(getLocation()));
    }

    <T extends MaterialData> T castData(BlockState b, Class<T> c)
    {
        MaterialData data = b.getData();
        if (c.isInstance(data))
            return c.cast(data);
        throw new IllegalArgumentException("Invalid block castData: " + data.getClass().toString() +
                                           ", data for a " + b.getType().toString() + ", is not a " + c.toString());
    }

}

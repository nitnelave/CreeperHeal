package com.nitnelave.CreeperHeal.block;

import com.nitnelave.CreeperHeal.utils.CreeperUtils;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.Rail;

import java.util.Set;

/**
 * Rail implementation of CreeperBlock.
 *
 * @author nitnelave
 *
 */
class CreeperRail extends CreeperBlock
{
    private static final BlockFace[] FACES = { BlockFace.EAST, BlockFace.WEST, BlockFace.NORTH,
                                              BlockFace.SOUTH };
    private static final BlockFace[] UP_DOWN = { BlockFace.DOWN, BlockFace.SELF, BlockFace.UP };

    public static final Set<Material> RAIL_TYPES =
            CreeperUtils.createFinalHashSet(Material.RAIL,
                                            Material.ACTIVATOR_RAIL,
                                            Material.DETECTOR_RAIL,
                                            Material.POWERED_RAIL);

    private final Rail data;

    /*
     * Constructor.
     */
    CreeperRail(BlockState blockState)
    {
        super(blockState);
        data = (Rail)blockState.getBlockData();
    }

    /*
     * (non-Javadoc)
     *
     * @see com.nitnelave.CreeperHeal.block.CreeperBlock#update()
     */
    @Override
    public void update()
    {

        Rail[][] railData = new Rail[3][4];
        Block block = getBlock();
        for (int i = 0; i < 3; i++)
        {
            Block upBlock = block.getRelative(UP_DOWN[i]);
            for (int j = 0; j < 4; j++)
            {
                Block tmpBlock = upBlock.getRelative(FACES[j]);
                if (RAIL_TYPES.contains(tmpBlock.getType()))
                    railData[i][j] = (Rail)(tmpBlock.getState().getBlockData());
            }
        }
        super.update();
        for (int i = 0; i < 3; i++)
        {
            Block upBlock = block.getRelative(UP_DOWN[i]);
            for (int j = 0; j < 4; j++)
            {
                Block tmpBlock = upBlock.getRelative(FACES[j]);
                if (RAIL_TYPES.contains(tmpBlock.getType()))
                    tmpBlock.getState().setBlockData(railData[i][j]);
            }
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see com.nitnelave.CreeperHeal.block.CreeperBlock#getAttachingFace()
     */
    @Override
    public BlockFace getAttachingFace()
    {
      switch (data.getShape()) {
        case ASCENDING_EAST:
          return BlockFace.EAST;
        case ASCENDING_WEST:
          return BlockFace.WEST;
        case ASCENDING_NORTH:
          return BlockFace.NORTH;
        case ASCENDING_SOUTH:
          return BlockFace.SOUTH;
        default:
          return BlockFace.DOWN;
      }
    }

    /**
     * Checks if the rail is ascending.
     *
     * @return True, if it is ascending
     */
    public boolean isAscending()
    {
      switch (data.getShape()) {
        case ASCENDING_EAST:
        case ASCENDING_WEST:
        case ASCENDING_NORTH:
        case ASCENDING_SOUTH:
          return true;
        default:
          return false;
      }
    }
}

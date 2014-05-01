package com.nitnelave.CreeperHeal.block;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;

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

    /*
     * Constructor.
     */
    protected CreeperRail(BlockState blockState)
    {
        super(blockState);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.nitnelave.CreeperHeal.block.CreeperBlock#update()
     */
    @Override
    public void update()
    {

        byte data = getRawData();
        byte[][] railData = new byte[3][4];
        Block block = getBlock();
        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 4; j++)
            {
                Block tmpBlock = block.getRelative(FACES[j]);
                tmpBlock = tmpBlock.getRelative(UP_DOWN[i]);
                railData[i][j] = tmpBlock.getData();
            }
        super.update();
        block.setData(data);
        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 4; j++)
            {
                Block tmpBlock = block.getRelative(FACES[j]);
                tmpBlock = tmpBlock.getRelative(UP_DOWN[i]);
                tmpBlock.setData(railData[i][j]);
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
        switch (getRawData())
        {
        case 5:
            return BlockFace.WEST;
        case 4:
            return BlockFace.EAST;
        case 3:
            return BlockFace.NORTH;
        case 2:
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
        return getRawData() > 1;
    }
}

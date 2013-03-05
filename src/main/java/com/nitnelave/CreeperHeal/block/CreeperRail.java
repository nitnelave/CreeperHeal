package com.nitnelave.CreeperHeal.block;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;

import com.nitnelave.CreeperHeal.CreeperHeal;

/**
 * Rail implementation of CreeperBlock.
 * 
 * @author nitnelave
 * 
 */
class CreeperRail extends CreeperBlock {

    /*
     * Constructor.
     */
    protected CreeperRail (BlockState blockState) {
        super (blockState);
    }

    /*
     * (non-Javadoc)
     * @see com.nitnelave.CreeperHeal.block.CreeperBlock#update()
     */
    @Override
    public void update () {
        Bukkit.getScheduler ().scheduleSyncDelayedTask (CreeperHeal.getInstance (), new Runnable () {
            @Override
            public void run () {
                replaceRail ();
            }
        });
    }

    /*
     * (non-Javadoc)
     * @see com.nitnelave.CreeperHeal.block.CreeperBlock#getAttachingFace()
     */
    @Override
    public BlockFace getAttachingFace () {
        switch (getRawData ())
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

    /*
     * Replace the rail and enforce the rails' direction, as it sometimes get
     * messed up by the other rails around.
     */
    private void replaceRail () {
        BlockFace[] faces = {BlockFace.EAST, BlockFace.WEST, BlockFace.NORTH, BlockFace.SOUTH};

        byte data = getRawData ();
        byte[][] railData = new byte[3][4];
        Block block = getBlock ();
        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 4; j++)
            {
                Block tmpBlock = block.getRelative (faces[j]);
                if (i == 2)
                    tmpBlock = tmpBlock.getRelative (BlockFace.UP);
                else if (i == 0)
                    tmpBlock = tmpBlock.getRelative (BlockFace.DOWN);
                railData[i][j] = tmpBlock.getData ();
            }
        blockState.update (true);
        block.setData (data);
        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 4; j++)
            {
                Block tmpBlock = block.getRelative (faces[j]);
                if (i == 2)
                    tmpBlock = tmpBlock.getRelative (BlockFace.UP);
                else if (i == 0)
                    tmpBlock = tmpBlock.getRelative (BlockFace.DOWN);
                tmpBlock.setData (railData[i][j]);
            }
    }

    /**
     * Checks if the rail is ascending.
     * 
     * @return True, if it is ascending
     */
    public boolean isAscending () {
        return getRawData () > 1;
    }
}

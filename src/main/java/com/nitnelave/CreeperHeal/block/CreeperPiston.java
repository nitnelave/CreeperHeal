package com.nitnelave.CreeperHeal.block;

import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;

/**
 * Piston implementation of CreeperBlock.
 * 
 * @author nitnelave
 * 
 */
class CreeperPiston extends CreeperBlock
{

    private final BlockFace orientation;
    private final boolean extended;

    /*
     * Constructor.
     */
    protected CreeperPiston(BlockState blockState)
    {
        this.blockState = blockState;
        BlockFace face;
        switch (getRawData() & 7)
        {
        case 0:
            face = BlockFace.DOWN;
            break;
        case 1:
            face = BlockFace.UP;
            break;
        case 2:
            face = BlockFace.NORTH;
            break;
        case 3:
            face = BlockFace.SOUTH;
            break;
        case 4:
            face = BlockFace.WEST;
            break;
        default:
            face = BlockFace.EAST;
        }
        if (blockState.getType().equals(Material.PISTON_EXTENSION))
        {
            extended = true;
            this.blockState = getBlock().getRelative(face.getOppositeFace()).getState();
        }
        else
            extended = (getRawData() & 8) != 0;
        orientation = face;
        this.blockState.setRawData((byte) (blockState.getRawData() & 7));
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.nitnelave.CreeperHeal.block.CreeperBlock#remove()
     */
    @Override
    public void remove()
    {
        getBlock().setType(Material.AIR);
        if (extended)
            getBlock().getRelative(orientation).setType(Material.AIR);
    }

}

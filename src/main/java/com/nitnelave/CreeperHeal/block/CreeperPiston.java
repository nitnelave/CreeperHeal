package com.nitnelave.CreeperHeal.block;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.type.Piston;
import org.bukkit.block.data.type.PistonHead;

/**
 * Piston implementation of CreeperBlock.
 *
 * @author nitnelave
 *
 */
class CreeperPiston extends CreeperBlock
{

    private final Piston pistonData;
    /*
     * Constructor.
     */
    CreeperPiston(BlockState blockState)
    {
        super(blockState);
        Block block = blockState.getBlock();
        if (blockState.getType().equals(Material.PISTON_HEAD))
            block = block.getRelative(((PistonHead)blockData).getFacing().getOppositeFace());
        this.blockState = block.getState();
        blockData = block.getState().getBlockData();
        pistonData = (Piston) blockData.clone();
        pistonData.setExtended(false);
        this.blockState.setBlockData(pistonData);
    }

    private boolean isExtended() {
      return ((Piston)blockData).isExtended();
    }

    @Override
    public void update()
    {
        super.update(pistonData, true);
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
        if (isExtended())
            getBlock().getRelative(pistonData.getFacing()).setType(Material.AIR);
    }
}

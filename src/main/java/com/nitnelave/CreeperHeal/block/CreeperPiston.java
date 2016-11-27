package com.nitnelave.CreeperHeal.block;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.material.PistonBaseMaterial;
import org.bukkit.material.PistonExtensionMaterial;

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
    CreeperPiston(BlockState blockState)
    {
        Block block = blockState.getBlock();
        if (blockState.getType().equals(Material.PISTON_EXTENSION))
            block = block.getRelative(castData(blockState, PistonExtensionMaterial.class).getAttachedFace());
        this.blockState = block.getState();
        PistonBaseMaterial data = castData(this.blockState, PistonBaseMaterial.class);
        orientation = data.getFacing();
        Block extension_block = block.getRelative(orientation);
        extended = extension_block.getType().equals(Material.PISTON_EXTENSION) &&
                   castData(extension_block.getState(), PistonExtensionMaterial.class).getFacing().equals(orientation);
        PistonBaseMaterial newdata = data.clone();
        newdata.setPowered(false);
        this.blockState.setData(newdata);
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

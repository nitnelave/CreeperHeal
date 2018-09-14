package com.nitnelave.CreeperHeal.block;

import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Piston;
import org.bukkit.block.data.type.PistonHead;

/**
 * Piston implementation of the CreeperMultiblock.
 *
 * @author Jikoo
 *
 */
class CreeperPiston extends CreeperMultiblock {

    CreeperPiston(BlockState blockState) {
        super(blockState);

        BlockData blockData = blockState.getBlockData();

        if (blockData instanceof Piston) {

            Piston piston = ((Piston) blockData);
            Block headBlock = blockState.getBlock().getRelative(piston.getFacing());
            BlockData headBlockData = headBlock.getBlockData();

            if (headBlockData instanceof PistonHead && ((PistonHead) headBlockData).getFacing() == piston.getFacing()) {
                this.dependents.add(headBlock.getState());
            }

        } else if (blockData instanceof PistonHead) {

            PistonHead piston = ((PistonHead) blockData);
            Block pistonBlock = blockState.getBlock().getRelative(piston.getFacing().getOppositeFace());
            BlockData pistonBlockData = pistonBlock.getBlockData();

            if (pistonBlockData instanceof Piston && ((Piston) pistonBlockData).getFacing() == piston.getFacing()) {
                this.blockState = pistonBlock.getState();
                this.dependents.add(blockState);
            }

        } else {
            throw new IllegalArgumentException("Invalid BlockData: " + blockData.getClass().getName());
        }
    }

}
